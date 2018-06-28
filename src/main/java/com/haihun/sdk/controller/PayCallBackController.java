package com.haihun.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StreamUtil;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.XMLUtils;
import com.haihun.pay.PayTypeEnum;
import com.haihun.pay.alipay.AlipayKey;
import com.haihun.sdk.pojo.Bank;
import com.haihun.sdk.pojo.GameOrder;
import com.haihun.sdk.service.BankService;
import com.haihun.sdk.service.GameOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/5/2
 */
@Api(tags="pay")
@Slf4j
@RestController
@RequestMapping("/pay")
public class PayCallBackController {


    @Autowired
    private GameOrderService service;

    @Autowired
    private BankService bankService;

    @ApiOperation(value = "支付宝回调地址")
    @PostMapping("/alipayCallBack")
    public void alipayCallBack(HttpServletResponse response, HttpServletRequest request) {
        try {
            Map<String, String> params = getAlipayParams(request);
            System.out.println();
            log.info("Receive Alipay Callback params ：{}", params.toString());
            log.info(" Start Handler and Check Sign ... Sign : {} ", params.get("sign"));
            if (AlipaySignature.rsaCheckV1(params, AlipayKey.ALI_PAY_PUBLIC_KEY, "UTF-8", AlipayConstants.SIGN_TYPE_RSA2)) {
                log.info("Check Successful ！ Start Handler Order ... orderId : {} ", params.get("out_trade_no"));
                // 验签成功，向支付宝返回 success ！);
                response.getOutputStream().write("success".getBytes());

                service.handlerOrder(JSONObject.parseObject(JSONObject.toJSONString(params)), PayTypeEnum.AliPay);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "微信回调地址")
    @PostMapping("/weChatPayCallBack")
    public void wechatPayCallBack(HttpServletResponse response, HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            String xmlParam = StreamUtil.readText(inputStream, "UTF-8");

            JSONObject json = XMLUtils.xmlToJson(xmlParam);
            log.info(" Receive WechatPay Callback Request  params : {}", json.toJSONString());
            log.info(" Receive OrderId : {}", json.get("out_trade_no"));
            String returnCode = String.valueOf(json.get("return_code"));
            // 保存 sign 用于验签
            Object responseSign = json.get("sign");
            //
            json.remove("sign");
            GameOrder order = service.findById(json.getString("out_trade_no"));
            if (order == null) {
                return;
            }
            Bank bank = bankService.findBankByGameIdAndPayType(order.getAppId(), order.getPaymentType());
            if (bank == null) {
                return;
            }
            String sign = ConstFuncUtil.buildWechatSign(json, bank.getApiKey());
            log.info(" Start Handler and Check Sign ... ");
            log.info("ResponseSign : {} ", responseSign);
            log.info("EncryptSign : ", sign);

            if ("SUCCESS".equals(returnCode) && responseSign.equals(sign)) {
                log.info(" Check Sign Succesful ! ");
                // 先响应请求。
                response.getOutputStream().print(XMLUtils.jsonToXml(new JSONObject() {{
                    put("return_code", "SUCCESS");
                    put("return_msg", "OK");
                }}));
                // 开始处理订单
                service.handlerOrder(json, PayTypeEnum.Wechatpay);

            } else if (!"SUCCESS".equals(returnCode)) {
                log.info("Order Payment Failed ! OrderId : {}", json.get("out_trade_no"));
            } else if (!responseSign.equals(sign)) {
                log.warn("ResponseSign {} Unequal to {}", responseSign, sign);
            }
        } catch (Exception e) {
            log.error("Handler WechatPay Callback Happen Error detial : {} ", e.getMessage());
        }
    }


    /**
     * 获取支付宝响应参数
     *
     * @param request
     * @return
     */
    private Map<String, String> getAlipayParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();

        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }


}
