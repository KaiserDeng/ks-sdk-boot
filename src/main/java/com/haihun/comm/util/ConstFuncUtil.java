package com.haihun.comm.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.haihun.comm.constant.CodeConst;
import com.haihun.comm.security.MD5;
import com.haihun.sdk.pojo.Bank;
import com.haihun.sdk.pojo.GameOrder;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.net.URLEncoder;
import java.util.*;

/**
 * 常用方法工具类
 *
 * @author kaiser·von·d
 * @version 2018/4/27
 */
@Slf4j
public class ConstFuncUtil {


    public static final String SIGN_TYPE_RSA2 = "RSA2";
    public static final String SIGN_TYPE_RSA = "RSA";

    /**
     * 随机生成N位的随机验证码
     *
     * @param n 位数，不能小于1位
     * @return 生成数字
     */
    public static final long generateRandomNumber(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long) (Math.random() * 9 * Math.pow(10, n - 1)) + (long) Math.pow(10, n - 1);
    }


    /**
     * 格构建一个系统错误返回信息
     */
    public static Result buildErrorResult(Exception e) {
        return buildErrorResult(e, Result.class);
    }

    /**
     * 格构建一个系统错误返回信息
     */
    public static <T> T buildErrorResult(Exception e, Class<? extends Result> clazz) {
        try {
            return (T) clazz.getDeclaredConstructor(int.class, String.class, String.class).newInstance(CodeConst.ERROR.getValue(), "System Error ! Please Retry Again ! ", null);
        } catch (Exception cause) {
        }
        return null;
    }


    public static String buildAlipaySign(GameOrder order, Bank bank) {
        try {
            SortedMap<String, String> params = new TreeMap<>();

            // 业务参数
            JSONObject content = buildAlipayBusinessParam(order);

            //必传参数
            params.put("biz_content", content.toJSONString());
            params.put("charset", "UTF-8");
            params.put("format", "json");
            params.put("method", "alipay.trade.app.pay");
            params.put("notify_url", order.getNotifyUrl());
            params.put("sign_type", SIGN_TYPE_RSA2);
            params.put("timestamp", DateFormatUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            params.put("version", "1.0");

            params.put("app_id", bank.getAppId());

            // 得到参数串k=v&k=v
            String param = getSignContent(params);

            // 计算出sign
            String sign = AlipaySignature.rsa256Sign(param, bank.getAppPrivateKey(), "UTF-8");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // 对每个value编码
                params.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return getSignContent(params) + "&sign=" + URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {

            log.error("Alipay Generator Sign Error! detail : " + e.getMessage());
        }
        return null;
    }

    private static JSONObject buildAlipayBusinessParam(GameOrder order) {
        JSONObject content = new JSONObject();
        content.put("body", "充值" + order.getTitle());
        content.put("out_trade_no", order.getOrderId());
        content.put("subject", order.getOrderId());
        content.put("total_amount", order.getTotalFee());
        return content;
    }

    public static JSONObject buildAlipayAppBusinessParam(GameOrder order) {
        JSONObject content = buildAlipayBusinessParam(order);
        content.put("product_code", "QUICK_MSECURITY_PAY");
        return content;
    }

    public static JSONObject buildAlipayWebBusinessParam(GameOrder order) {
        JSONObject content = buildAlipayBusinessParam(order);
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");
        return content;
    }


    /**
     * @param sortedParams
     * @return
     */
    public static String getSignContent(Map sortedParams) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = String.valueOf(sortedParams.get(key));
            if (StringUtils.areNotEmpty(key, value)) {
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                index++;
            }
        }
        return content.toString();
    }

    public static String buildWechatWapPayParam(GameOrder order, Bank bank) {
        JSONObject json = buildWechatPayBaseParam(order, bank);
        json.put("trade_type", "MWEB");
        JSONObject sceneInfo = new JSONObject();
        json.put("scene_info", "{\"h5_info\":" + new JSONObject() {{
            put("type", "Wap");
            put("wap_url", "https://www.baidu.com");
            put("wap_name", "海魂SDK游戏充值");
        }}.toJSONString() + "}");
        json.put("sign", buildWechatSign(json, bank.getApiKey()));
        String xml = "";
        try {
            xml = XMLUtils.jsonToXml(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }


    /**
     * 构建微信App支付参数
     *
     * @param order 订单信息
     * @param bank  银行信息
     * @return 微信需要的xml参数
     */
    public static String buildWechatAppPayParam(GameOrder order, Bank bank) {
        JSONObject json = buildWechatPayBaseParam(order, bank);
        json.put("trade_type", "APP");
        json.put("sign", buildWechatSign(json, bank.getApiKey()));
        String xml = "";
        try {
            xml = XMLUtils.jsonToXml(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * 构建微信支付基础信息
     *
     * @param order 订单信息
     * @param bank  银行信息
     */
    private static JSONObject buildWechatPayBaseParam(GameOrder order, Bank bank) {
        JSONObject json = new JSONObject();
        json.put("appid", bank.getAppId());
        json.put("body", order.getTitle());
        json.put("mch_id", bank.getMchId());
        json.put("nonce_str", getUUID());
        json.put("notify_url", order.getNotifyUrl());
        json.put("out_trade_no", order.getOrderId());
        json.put("spbill_create_ip", order.getSpbillCreateIp());
        json.put("total_fee", order.getTotalFee());
        return json;
    }

    /**
     * 构建一个微信带key的sign
     *
     * @param params
     * @return
     */
    public static String buildWechatSign(JSONObject params, String key) {
        String sigContent = getSignContent(params) + "&key=" + key;
        try {

            return MD5.getMD5(sigContent).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取一个UUID随机字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 传入参数是否通过检验
     *
     * @param result
     * @return
     */
    public static boolean checkResult(Result result) {
        if (CodeConst.OK.getValue() != result.getStatus()) {
            return true;
        }
        return false;
    }

}
