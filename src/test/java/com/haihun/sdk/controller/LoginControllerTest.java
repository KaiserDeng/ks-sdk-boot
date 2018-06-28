package com.haihun.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.haihun.comm.util.HttpUtils;
import com.haihun.comm.util.XMLUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;

/**
 * @author kaiser·von·d
 * @version 2018/6/26
 */
public class LoginControllerTest {


    private String userName = "";
    private String pwd = "123456";
    private String token;
    private String code;
    private String mobile = "15501302275";
    private String id;
    private String orderUrl = "http://127.0.0.1:8080/order/create";
    private String webOrderUrl = "http://127.0.0.1:8080/order/createWeb";
    private String appId = "hh80674e498b7b415f";
    private String appSecret = "2e64a74f68fb42bcb2d15bdbd88a4b7c";

    private String localLogin = "http://localhost:8080/sso/login";
    private String remoteLogin = "http://sdk.haihungame.com/sso/login";

    @Before
    public void before() {
        userName = "15501302275";
        pwd = "123456";
        JSONObject json = new JSONObject();
        json.put("userName", userName);
        json.put("pwd", pwd);
        json.put("duid", "2a1b0f2ca99b56e73b25a5c57b4d3a2e");
        String response = HttpUtils.encryptPost("http://localhost:8080/sso/login", json.toJSONString(), appId, appSecret, true);
        json = JSONObject.parseObject(response);
        json = JSONObject.parseObject((String) json.get("data"));
        token = ((String) json.get("token"));
        System.out.println(token);
    }

    @Test
    public void checkToken() {
//        String url = "http://127.0.0.1:8080/sso/checkToken";
        String url = "http://sdk.haihungame.com/sso/checkToken";
        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("userId", id);
        json.put("appKey", appId);
        String response = HttpUtils.post(url, json);

        System.out.println(response);

    }


    @Test
    public void callBack() throws JsonProcessingException {
        String param = "{\n" +
                "    \"appid\": \"wx5a8ac11ef3276dcf\",\n" +
                "    \"attach\": \"微信支付测试\",\n" +
                "    \"bank_type\": \"CFT\",\n" +
                "    \"cash_fee\": \"1\",\n" +
                "    \"fee_type\": \"CNY\",\n" +
                "    \"is_subscribe\": \"N\",\n" +
                "    \"mch_id\": \"1501954481\",\n" +
                "    \"nonce_str\": \"a9c3f95dc11f4a51a3889e6591508880\",\n" +
                "    \"openid\": \"o9VPw1LxelocQWgijqpw33hRoR6U\",\n" +
                "    \"out_trade_no\": \"523920180626144730\",\n" +
                "    \"result_code\": \"SUCCESS\",\n" +
                "    \"return_code\": \"SUCCESS\",\n" +
                "    \"sign\": \"9E633E7416DC9FF05C82C7021E5F5CE6\",\n" +
                "    \"time_end\": \"20180626144953\",\n" +
                "    \"total_fee\": \"1\",\n" +
                "    \"trade_type\": \"APP\",\n" +
                "    \"transaction_id\": \"4200000131201806266277428153\"\n" +
                "}";
        String url = "http://localhost:8080/pay/weChatPayCallBack";
        String xml = XMLUtils.jsonToXml(JSONObject.parseObject(param));
        String response = HttpUtils.postXmlParams(url, xml);
        System.out.println(response);
    }


    @Test
    public void queryOrderByOrderId() {
        String url = "http://localhost:8080/order/queryOrderByOrderId";
        String order = HttpUtils.post(url, new JSONObject() {{
//            put("orderId", "523920180626144730");
            put("orderId", "805620180626172830");
        }});
        System.out.println(order);
    }

    @Test
    public void getCode() {
        String url = "http://localhost:8080/sso/smsVerifyCode";
        JSONObject poaram = new JSONObject();
        poaram.put("mobile", mobile);
        poaram.put("status", "0");
        String response = HttpUtils.post(url, poaram);
    }

    @Test
    public void unBindMobile( ) {
        String url = "http://localhost:8080/sso/unBindMobile";
        JSONObject param = new JSONObject();
        param.put("token", "155B6363CB9617D71C878CE5A849B579");
        param.put("mobile", mobile);
        param.put("code", "1029");
        String response = HttpUtils.post(url, param);
        System.out.println(response);

    }

    @Test
    public void bindMobile( ) {
        String url = "http://localhost:8080/sso/bindMobile";
        JSONObject param = new JSONObject();
        param.put("token", "155B6363CB9617D71C878CE5A849B579");
        param.put("mobile", mobile);
        param.put("smsVerifyCode", "1372");
        param.put("pwd", "123456");
        String response = HttpUtils.encryptPost(url,param.toJSONString(),appId,appSecret,true);
        System.out.println(response);

    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String code = sc.next();
        System.out.println(code);
    }
}