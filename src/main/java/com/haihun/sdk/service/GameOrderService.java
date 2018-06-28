package com.haihun.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.haihun.comm.constant.CodeConst;
import com.haihun.comm.security.MD5;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.HttpUtils;
import com.haihun.comm.util.XMLUtils;
import com.haihun.config.redis.JedisService;
import com.haihun.pay.PayTypeEnum;
import com.haihun.pay.alipay.AlipayEnum;
import com.haihun.pay.alipay.AlipayUrlConst;
import com.haihun.pay.unionpay.sdk.AcpService;
import com.haihun.pay.unionpay.sdk.CertUtil;
import com.haihun.pay.unionpay.sdk.SDKConfig;
import com.haihun.pay.wechatpay.WechatPayUrlConst;
import com.haihun.sdk.mapper.GameOrderMapper;
import com.haihun.sdk.pojo.*;
import com.haihun.sdk.service.base.BaseService;
import com.haihun.sdk.vo.result.OrderListResult;
import com.haihun.sdk.vo.result.OrderResult;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author kaiser·von·d
 * @version 2018/4/26
 */
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class GameOrderService extends BaseService<GameOrder> {

    public static final String RETRY_ORDER_CONTNET = "order_content";


    /**
     * 最后一次重试时间
     */
    public static final String LAST_SEND_TIME = "last_send_time";

    /**
     * 通知次数
     */
    public static final String RETRY_NOTIFY_COUNT = "retry_notify_count";

    /**
     * 通知url
     */
    public static final String NOTIFY_URL = "notify_url";


    /**
     * 订单状态field值，用于鉴别当前订单是否有人在处理
     */
    public static final String ORDER_HANDLER_STATUS = "order_handler_status";

    /**
     * 订单缓存前缀key值
     */
    public static final String HANDLER_ORDER_PREFIX = "handler_order_prefix_";


    /**
     * 订单通知队列key
     */
    public static final String ORDER_RETRY_NOTIFY_QUEUE = "order_retry_notify_queue";


    private final String CHARSET_UTF8 = "UTF-8";

    @Autowired
    private GameOrderMapper orderMapper;

    @Autowired
    private GameService gameService;

    @Autowired
    private JedisService jedisService;


    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;

    /**
     * 创建订单
     *
     * @return
     * @throws Exception
     */
    public OrderResult createOrder(GameOrder order, OrderResult result) {
        try {
            String token = order.getToken();
            // 验证token
            String userInfoJson = userService.queryUserByToken(token);
            User userInfo = null;

            if ((StringUtils.isBlank(userInfoJson) && "1".equalsIgnoreCase(order.getPayChannel()))) {
                result.notFound("token expire Or userName error ！");
                return result;
            } else if ("2".equalsIgnoreCase(order.getPayChannel()) && ((userInfo = userService.findByUserName(order.getUserName())) == null)) {
                result.notFound("userName error ！");
                return result;
            }
            if (userInfo == null) {
                JSONObject json = JSONObject.parseObject(userInfoJson);
                userInfo = userService.findByUserName(json.getString("userName"));
            }

            Game game = gameService.findById(order.getAppId());
            if (game == null) {
                result.notFound("game not found ！");
                return result;
            }


            //设置订单信息并保存
            //生成订单号 = 随机数4位+ 时间戳
            String orderId = ConstFuncUtil.generateRandomNumber(4) + "" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");

            order.setOrderId(orderId);
            order.setUserName(userInfo.getUserName());

            order.setCreateTime(new Date());
            order.setUpdateTime(order.getCreateTime());
            // 未付款
            order.setStatus(GameOrder.GameOrderTypeEnum.WAIT_BUYER_PAY.getVal());


            // 返回数据
            // 支付回写地址
            PayTypeEnum payType = getPaymentType(order);
            order.setPaymentType(payType.getKey());

            //因为支付渠道的不同，所以需要保存原始充值金额，便于自己的规则保存
            String totalFee = order.getTotalFee();

            if (!PayTypeEnum.Wechatpay.equals(payType)) {
                order.setTotalFee(String.valueOf(Double.valueOf(order.getTotalFee()) / 100));
            }

            // 根据支付类型生成订单
            Bank bank = bankService.findBankByGameIdAndPayType(order.getAppId(), order.getPaymentType());
            if (bank == null) {
                result.notFound("Game must binding payType ! ");
            }
            switch (payType) {
                case AliPay:
                    order.setNotifyUrl(PayTypeEnum.AliPay.getValue());
                    if ("2".equalsIgnoreCase(order.getPayChannel())) {
                        result.setData(alipayTradeWapPay(bank, order));
                    } else {
                        result.setData(new JSONObject() {{
                            put("content", URLEncoder.encode(ConstFuncUtil.buildAlipaySign(order, bank), CHARSET_UTF8));
                        }}.toJSONString());
                    }
                    break;
                case UnionPay:
                    order.setNotifyUrl(PayTypeEnum.UnionPay.getValue());
                    this.buildUnionpayOrder(order, result);
                    break;
                case Wechatpay:
                    order.setNotifyUrl(PayTypeEnum.Wechatpay.getValue());
                    if ("2".equalsIgnoreCase(order.getPayChannel())) {
                        this.buildWechatWapOrder(order,result,bank);
                    } else {
                        this.buildWechatAppOrder(order, result, bank);
                    }
                    break;
                default:
                    result.notFound("Not Found PaymentType : " + order.getPaymentType());
                    return result;
            }
            if (ConstFuncUtil.checkResult(result)) return result;

            order.setTotalFee(totalFee);

            // 创建订单
            save(order);

            //返回订单号
            return result;
        } catch (Exception e) {
            log.error("Create order error : " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String alipayTradeWapPay(Bank bank, GameOrder order) {
        AlipayClient alipayClient = getAlipayClient(bank);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        JSONObject param = ConstFuncUtil.buildAlipayWebBusinessParam(order);
        request.setBizContent(param.toJSONString());
        request.setNotifyUrl("http://17586q9d82.iask.in/pay/alipayCallBack");
        request.setReturnUrl("http://17586q9d82.iask.in/analy/index");
        String form = null;
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (Exception e) {
            log.error("alipay web payType error ! detail : {}", e.getMessage());
        }
        return form;

    }


    /**
     * 构建银联消费订单
     *
     * @param order  本地订单
     * @param result 返回结构体
     */
    private void buildUnionpayOrder(GameOrder order, OrderResult result) {
        Map<String, String> reqData = new TreeMap<>();

        reqData.put("version", "5.1.0");
        reqData.put("encoding", "UTF-8");
        reqData.put("certId", CertUtil.getSignCertId());
        reqData.put("signMethod", "01");
        reqData.put("txnType", "01");
        reqData.put("txnSubType", "01");
        reqData.put("bizType", "000201");
        reqData.put("channelType", "07");
        reqData.put("accessType", "0");
        reqData.put("merId", "777290058110048");
        reqData.put("backUrl", PayTypeEnum.UnionPay.getValue());
        reqData.put("orderId", order.getOrderId());
        reqData.put("currencyCode", "156");
        reqData.put("txnAmt", order.getTotalFee());
        reqData.put("orderDesc", order.getTitle());
        reqData.put("txnTime", DateFormatUtils.format(order.getCreateTime(), "yyyyMMddHHmmss"));

        reqData = AcpService.sign(reqData, "UTF-8");
        String url = SDKConfig.getConfig().getProperties().getProperty("acpsdk.appTransUrl");
//        String url = "https://gateway.test.95516.com/gateway/api/appTransReq.do";
        Map<String, String> respData = AcpService.post(reqData, url, "UTF-8");
        reqData.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
        if (AcpService.validate(respData, "UTF-8")) {
            result.setData(new JSONObject() {{
                put("tn", respData.get("tn"));
            }}.toJSONString());
        } else {
            throw new RuntimeException("Create UnionPay Order Failed ! ");
        }


    }

    /**
     *
     * @param order
     * @param result
     * @param bank
     * @throws IOException
     */
    private void buildWechatWapOrder(GameOrder order, OrderResult result, Bank bank) throws IOException {
        String param = ConstFuncUtil.buildWechatWapPayParam(order, bank);
        JSONObject json = wechatSubmit(param, WechatPayUrlConst.WAP_UNIFIED_ORDER, bank.getApiKey());
        if (json.getBoolean("check")) {
            json = json.getJSONObject("json");
            // 记录预订单
            order.setOutTradeNo(json.getString("prepay_id"));
            // 返回微信h5支付地址
            result.setData(json.getString("mweb_url"));
        } else {
            throw new RuntimeException(" Create Order Error. please Retry Again!");
        }
    }

    /**
     * 构建微信App预订单
     *
     * @param order  本地订单
     * @param result 返回结果
     * @throws IOException
     */
    private void buildWechatAppOrder(GameOrder order, OrderResult result, Bank bank) throws IOException {
        String param = ConstFuncUtil.buildWechatAppPayParam(order, bank);
        // 创建微信订单
        JSONObject json = wechatSubmit(param, WechatPayUrlConst.APP_UNIFIED_ORDER, bank.getApiKey());

//         封装返回信息
        if (json.getBoolean("check")) {
            json = json.getJSONObject("json");
            JSONObject params = new JSONObject();
            // 记录预订单
            order.setOutTradeNo(json.getString("prepay_id"));

            params.put("package", "Sign=WXPay");
            params.put("appid", json.get("appid"));
            params.put("prepayid", json.get("prepay_id"));
            params.put("partnerid", json.get("mch_id"));
            params.put("noncestr", ConstFuncUtil.getUUID());
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("sign", ConstFuncUtil.buildWechatSign(params, bank.getApiKey()));
            params.put("orderId", order.getOrderId());

            result.setData(params.toJSONString());
        } else {
            throw new RuntimeException(" Create Order Error. please Retry Again!");
        }
    }

    /**
     * 提交微信请求
     *
     * @param param 请求参数
     * @param url   请求地址
     * @return 返回参数
     * @throws InterruptedException
     * @throws IOException
     */
    private JSONObject wechatSubmit(String param, String url, String key) throws IOException {
        String resonpseXml;
        AtomicInteger count = new AtomicInteger(1);

        // 发送失败则重试3次
        while (StringUtils.isBlank(resonpseXml = HttpUtils.postXmlParams(url, param))) {
            if (count.intValue() > 3) break;
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
            }
            count.incrementAndGet();
        }
        // 如果三次都没有响应则，视为连接超时
        if (StringUtils.isBlank(resonpseXml)) {
            throw new RuntimeException("Operation order timeout! ");
        }
        JSONObject json = XMLUtils.xmlToJson(resonpseXml);
        String returnCode = json.getString("return_code");
        if ("success".equalsIgnoreCase(returnCode)) {

            // 保存 sign用于验签
            String responseSign = json.getString("sign");
            // 删除sign
            json.remove("sign");
            String sign = ConstFuncUtil.buildWechatSign(json, key);
            if (!responseSign.equals(sign)) {
                throw new RuntimeException("Response data is changed! ");
            }

            return new JSONObject() {{
                put("check", "SUCCESS".equals(returnCode));
                put("json", json);
            }};
        }
        throw new RuntimeException("request wechat server failed ! detail : " + json.getString("return_msg"));
    }


    /**
     * 根据订单id查询订单
     *
     * @param orderId 订单号
     * @return
     */
    public OrderResult queryOrderByOrderId(String orderId) {
        try {
            OrderResult result = new OrderResult();


            if (StringUtils.isBlank(orderId)) {
                result.warn("orderId is empty！");
                return result;
            }
            // 查询到该订单
            GameOrder order = findById(orderId);
            if (order == null) {
                result.notFound(" order not found ！");
                return result;
            }
            //  根据订单号去查询订单信息。
            if (GameOrder.GameOrderTypeEnum.WAIT_BUYER_PAY.getVal().equals(order.getStatus())) {

                PayTypeEnum payType = getPaymentType(order);
                switch (payType) {
                    case AliPay:
                        queryAlipayOrderStatus(order, result);
                        break;
                    case Wechatpay:
                        queryWechatOrderStatus(order, result);
                        break;
                    case UnionPay:
                    default:
                        result.notFound("Not Found PaymentType : " + order.getPaymentType());
                        return result;
                }
                if (result.getStatus() != CodeConst.OK.getValue()) return result;
                if (jedisService.hsetnx(HANDLER_ORDER_PREFIX + order.getOrderId(), ORDER_HANDLER_STATUS, "1") == 1) {
                    // 更新订单
                    updateSelective(order);
                    jedisService.del(HANDLER_ORDER_PREFIX + order.getOrderId());
                }
            }
            JSONObject retJson = buildNotifyData(order);
            retJson.remove("sign");
            result.setData(retJson.toJSONString());
            return result;
        } catch (Exception e) {
            log.error("Error details when querying an order : " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 查询微信订单
     *
     * @param order
     */
    private void queryWechatOrderStatus(GameOrder order, Result result) {
        Bank bank = bankService.findBankByGameIdAndPayType(order.getAppId(), PayTypeEnum.Wechatpay.getKey());
        String outTradeNo = order.getOutTradeNo();
        JSONObject params = new JSONObject();
        params.put("appid", bank.getAppId());
        params.put("mch_id", bank.getMchId());
        params.put("transaction_id", outTradeNo);
        params.put("out_trade_no", order.getOrderId());
        params.put("nonce_str", ConstFuncUtil.getUUID());
        String sign = ConstFuncUtil.buildWechatSign(params, bank.getApiKey());
        params.put("sign", sign);
        try {
            JSONObject json = wechatSubmit(XMLUtils.jsonToXml(params), WechatPayUrlConst.APP_QUERY_ORDER, bank.getApiKey());
            if (json.getBoolean("check")) {
                updateSelectiveOrder(order, json.getJSONObject("json"));
            }
        } catch (Exception e) {
            result.notFound("not found order!");
            return;
        }


    }

    private void updateSelectiveOrder(GameOrder order, JSONObject json) {
        // 更新订单状态
        order.setStatus(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal());
        // 保存流水号
        order.setOutTradeNo(json.getString("transaction_id"));

        // 更新支付时间. 把微信 yyyyMMddHhmmss 转为 yyyy-MM-dd HH:mm:ss
        LocalDateTime localDateTime = LocalDateTime.parse(json.getString("time_end"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        order.setPaymentTime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        // 更新订单
        order.setEndTime(new Date());
        // 更新订单状态
        order.setStatus(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal());
    }


    /**
     * 查询支付宝订单
     *
     * @param order
     * @return
     * @throws com.alipay.api.AlipayApiException
     */
    private Result queryAlipayOrderStatus(GameOrder order, Result result) throws com.alipay.api.AlipayApiException {
        Bank bank = bankService.findBankByGameIdAndPayType(order.getAppId(), order.getPaymentType());
        // 使用 alipay sdk查询用户订单
        AlipayClient alipayClient = getAlipayClient(bank);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(new JSONObject() {{
            put("out_trade_no", order.getOrderId());
        }}.toJSONString());

        AlipayTradeQueryResponse queryResponse = alipayClient.execute(request);
        JSONObject body = JSONObject.parseObject(queryResponse.getBody());
        JSONObject response = JSONObject.parseObject(String.valueOf(body.get("alipay_trade_query_response")));

        if (!response.containsKey("trade_status")) {
            result.notFound(response.getString("sub_msg"));
            return result;
        }

        String status = response.getString("trade_status");

        if (GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.name().equals(status)) {
            order.setStatus(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal());
            order.setPaymentTime(response.getDate("send_pay_date"));
            order.setEndTime(response.getDate("send_pay_date"));
        }
        return result;
    }

    private AlipayClient getAlipayClient(Bank bank) {
        return new DefaultAlipayClient(AlipayUrlConst.ALIPAY_GATEWAY, bank.getAppId(), bank.getAppPrivateKey(), "json", "UTF-8", bank.getAlipayPublicKey(), "RSA2");
    }


    /**
     * 处理支付宝支付回调
     *
     * @param param
     */
    public void handlerAlipayCallBackOrder(AlipayParam param) {

        try {
            log.info("Pay to Complete ！Start handler Order. Order No： " + param.getOut_trade_no());

            GameOrder order = findById(param.getOut_trade_no());

            if (order == null) {
                log.error(" handlerAlipayCallBackOrder -> Game Order Not Found ! " + param.toString());
            }
            String status = order.getStatus();
            if (status.equals(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal()) || status.equals(GameOrder.GameOrderTypeEnum.TRADE_CLOSED.getVal())) {
                log.info("The order has been processed or cancelled ! Order No： " + param.getOut_trade_no());
                return;
            }
            // 交易成功
            if (AlipayEnum.valueOf(param.getTrade_status()).equals(AlipayEnum.TRADE_SUCCESS)) {
                log.info("Order transaction success！ Order No： " + param.getOut_trade_no());

                // 保存流水号
                order.setOutTradeNo(param.getTrade_no());
                // 更新支付时间
                order.setPaymentTime(param.getGmt_payment());
                // 更新订单
                order.setEndTime(new Date());
                // 更新订单状态
                order.setStatus(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal());
            } else if (AlipayEnum.valueOf(param.getTrade_status()).equals(AlipayEnum.TRADE_CLOSED)) {
                log.info("Order Close ！ Order No： " + param.getOut_trade_no());
                // 订单关闭。
                // 更新订单
                // 保存流水号
                order.setOutTradeNo(param.getTrade_no());
                order.setUpdateTime(new Date());
                order.setCloseTime(new Date());
                order.setStatus(GameOrder.GameOrderTypeEnum.TRADE_CLOSED.getVal());
            } else if (AlipayEnum.valueOf(param.getTrade_status()).equals(AlipayEnum.TRADE_FINISHED)) {
                // 交易结束，不可以退款
                return;
            }

            update(order);
            Game game = gameService.findById(order.getAppId());
            order.setNotifyUrl(game.getPayCallBackUrl());

            // 将消息通知到游戏
            notifyToGame(order);
        } catch (Exception e) {
            log.error(" update order status error detail : {}", e.getMessage());
            throw new RuntimeException("update order status error detail : " + e.getMessage());
        }

    }

    /**
     * 通知支付信息到 游戏服务器
     *
     * @param order 订单
     */
    private void notifyToGame(GameOrder order) {
        String notifyUrl = order.getNotifyUrl();
        JSONObject postGameData = buildNotifyData(order);

        notifyToGame(new JSONObject() {{
            put(RETRY_ORDER_CONTNET, postGameData.toJSONString());
        }}, notifyUrl, 1);
    }

    /**
     * 构建订单信息
     *
     * @param order
     * @return
     */
    private JSONObject buildNotifyData(GameOrder order) {
        JSONObject postGameData = new JSONObject();
        postGameData.put("orderId", order.getOrderId());
        postGameData.put("totalFee", order.getTotalFeeFormat());
        postGameData.put("paymentType", order.getPaymentTypeName());
        postGameData.put("paymentTime", new DateTime(order.getPaymentTime()).toString("yyyy-MM-dd HH:mm:ss"));
        postGameData.put("extra", order.getExtra());
        postGameData.put("title", order.getTitle());
        postGameData.put("status", order.getStatus());
        String tmpSign = ConstFuncUtil.getSignContent(postGameData);
        Game game = gameService.findById(order.getAppId());

        tmpSign += "&key=" + game.getAppSecret();
        String sign = null;
        try {
            sign = MD5.getMD5(tmpSign);
        } catch (Exception e) {
        }
        postGameData.put("sign", sign);
        return postGameData;
    }

    /**
     * 通知信息到游戏方。
     *
     * @param postGameData
     * @param notifyUrl
     * @param sendCount
     */
    public void notifyToGame(JSONObject postGameData, String notifyUrl, Integer sendCount) {
        String responseData = null;
        try {
            responseData = HttpUtils.post(notifyUrl, JSONObject.parseObject(postGameData.getString(RETRY_ORDER_CONTNET)));
        } catch (Exception e) {
            log.error("notify game failed !");
        }
        // 加入重试队列
        //    程序执行完后必须打印输出“success”（不包含引号）。
        if (StringUtils.isBlank(responseData) || !"success".equalsIgnoreCase(responseData)) {
            postGameData.put(RETRY_ORDER_CONTNET, postGameData.getString(RETRY_ORDER_CONTNET));
            postGameData.put(LAST_SEND_TIME, String.valueOf(System.currentTimeMillis()));
            postGameData.put(RETRY_NOTIFY_COUNT, sendCount);
            postGameData.put(NOTIFY_URL, notifyUrl);
            jedisService.lPush(ORDER_RETRY_NOTIFY_QUEUE, postGameData.toJSONString());
        }
    }

    /**
     * 处理微信请求回调
     *
     * @param json
     */
    public void handlerWechatPayCallBackOrder(JSONObject json) {
        try {
            log.info("Start handler Order orderId : {}", json.getString("out_trade_no"));
            GameOrder order = findById(json.getString("out_trade_no"));
            if (order == null) {
                log.error(" Game Order Not Found ! params : {} ", json.toJSONString());
            }
            String status = order.getStatus();
            if (status.equals(GameOrder.GameOrderTypeEnum.TRADE_SUCCESS.getVal()) || status.equals(GameOrder.GameOrderTypeEnum.TRADE_CLOSED.getVal())) {
                log.info("The order has been processed or cancelled ！ orderId ：{} ", json.getString("out_trade_no"));
                return;
            }
            // 交易成功
            log.info("Successful order transaction！ Order No ： " + json.getString("out_trade_no"));
            // 更新订单状态
            updateSelectiveOrder(order, json);

            // 更新订单
            update(order);

            Game game = gameService.findById(order.getAppId());
            order.setNotifyUrl(game.getPayCallBackUrl());

            // 将消息通知到游戏
            notifyToGame(order);

        } catch (Exception e) {
            log.error(" update order status error detail : {}", e.getMessage());
            throw new RuntimeException("update order status error detail : " + e.getMessage());
        }
    }


    /**
     * 获取订单支付类型。
     *
     * @param order 订单
     * @return 订单类型
     */
    public PayTypeEnum getPaymentType(GameOrder order) {
        return getPayTypeEnum(order.getPaymentType());
    }

    private PayTypeEnum getPayTypeEnum(String paymentType) {
        PayTypeEnum[] values = PayTypeEnum.values();
        for (PayTypeEnum type : values) {
            if (type.getKey().equals(paymentType)) {
                return type;
            }

        }
        return null;
    }


    public String convertStatus(String status) {
        // 1、未付款，2、已付款，3、已取消
        switch (status) {
            case "1":
                return "未付款";
            case "2":
                return "已付款";
            case "3":
                return "已取消";
            default:
                return "未付款";
        }

    }

    /**
     * 处理订单，此处利用了分布式锁的特性。保证订单不被重复的处理。
     *
     * @param responseData
     * @param payType
     */
    public void handlerOrder(JSONObject responseData, PayTypeEnum payType) {
        String orderId = responseData.getString("out_trade_no");
        try {
            String handlerKey = HANDLER_ORDER_PREFIX + orderId;
            long isHandler = jedisService.hsetnx(handlerKey, ORDER_HANDLER_STATUS, "1");

            if (isHandler == 1) {
                jedisService.expire(handlerKey, 10);
                switch (payType) {
                    case AliPay:
                        AlipayParam param = JSONObject.parseObject(JSONObject.toJSONString(responseData), AlipayParam.class);
                        handlerAlipayCallBackOrder(param);
                        break;
                    case Wechatpay:
                        handlerWechatPayCallBackOrder(responseData);
                        break;
                    default:
                        return;
                }
                // 释放事务锁
                jedisService.del(handlerKey);
            }
        } catch (Exception e) {
            throw new RuntimeException("update order status error detail : " + e.getMessage());
        }
    }


    /**
     * 查询用户订单列表
     *
     * @param page  页号
     * @param limit 页大小
     * @param token token
     * @return 订单列表
     */
    public OrderListResult list(Integer page, Integer limit, String token) {
        try {
            OrderListResult result = new OrderListResult();
            if (page <= 0 || limit <= 0) {
                result.warn("page or limit must > 0 !");
                return result;
            }

            String infoJson;
            if (StringUtils.isBlank(token) || StringUtils.isBlank((infoJson = userService.queryUserByToken(token)))) {
                result.notFound("invalid token !");
                return result;
            }
            JSONObject userInfo = JSONObject.parseObject(infoJson);
            String userName = userInfo.getString("userName");

            List<Map<String, Object>> list = orderMapper.findListByPageAndUserName(userName, (page - 1) * limit, limit);

            list.stream().forEach(x -> {
                x.put("paymentType", getPayTypeEnum(String.valueOf(x.get("paymentType"))).getName());
            });

            result.setData(JSONObject.toJSONString(list));
            return result;
        } catch (Exception e) {
            log.error("query order list error ! detail : {} " + e.getMessage());
            throw new RuntimeException("query order list error ! detail : " + e.getMessage());
        }
    }
}
