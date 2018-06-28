package com;

import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.util.HttpUtils;
import org.junit.After;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author kaiser·von·d
 * @version 2018/5/11
 */
/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class CreateOrderTest {

    private String userName = "";
    private String pwd = "123456";
    private String token;
    private String orderUrl = "http://127.0.0.1:8080/order/create";
    private String webOrderUrl = "http://127.0.0.1:8080/order/createWeb";
    private String appId = "hh80674e498b7b415f";
    private String appSecret = "2e64a74f68fb42bcb2d15bdbd88a4b7c";

    @Test
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
    public void createWechatOrder() {
        JSONObject map = new JSONObject();
        map.put("spbillCreateIp", "119.131.76.239");
        map.put("appId", "21c3471fb98d4");
        map.put("paymentType", "1");
        map.put("title", "测试");
        map.put("totalFee", "2");
        map.put("token", token);
        String response = HttpUtils.encryptPost("http://127.0.0.1:8080/order/create", map.toJSONString(), "21c3471fb98d4", "d1875262559685e4ead3480efdcf94a0", true);
        System.out.println(response);
        System.out.println("方法执行。。。");
    }


    @Test
    public void createAliapyOrder() {
        JSONObject map = new JSONObject();
        map.put("paymentType", "0");
        map.put("title", "测试");
        map.put("totalFee", "2");
        map.put("token", token);
        map.put("payChannel", "1");
        map.put("extra", "1");

        String response = HttpUtils.encryptPost(orderUrl, map.toJSONString(), appId, appSecret, true);
        System.out.println(response);

    }

    @Test
    public void createAliapyWebOrder() {
        JSONObject map = new JSONObject();
        map.put("paymentType", "0");
        map.put("title", "测试");
        map.put("totalFee", "2");
        map.put("token", token);
        map.put("payChannel", "1");
        map.put("extra", "1");
        map.put("appId", appId);
        map.put("userName", "100157");

        String response = HttpUtils.post(orderUrl,map);
        System.out.println(response);

    }

    @Test
    public void logout() {
        String url = "http://localhost:8080/sso/logout";
        String token = "AB934C48659522782A66CC202A5510BD";
        HttpUtils.post(url,new JSONObject(){{put("token",token);}});
    }


    @Test
    public void register() throws UnsupportedEncodingException {
        String url = "http://sdk.haihungame.com/sso/register";
        JSONObject json = new JSONObject();
        json.put("userName", "15501302275");
        json.put("pwd", "123456");
        String sout = HttpUtils.encryptPost(url, json.toJSONString(), "hh80674e498b7b415f", "2e64a74f68fb42bcb2d15bdbd88a4b7c", false);
        System.out.println(sout);
    }

    @After
    public void after() {
        System.out.println("方法执行之后。。。");
    }




}