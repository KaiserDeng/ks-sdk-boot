package com.haihun.comm.util;

import com.haihun.comm.security.EncryptUtils;
import com.haihun.comm.security.EncryptVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http工具类
 *
 * @author Kaiser·von·d
 * @version 2018/5/12
 */
@Slf4j
public class HttpUtils {

    
    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build();
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=utf-8");

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String get(String url, Map<String, Object> queries) {
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.size() > 0) {
            sb.append("?");
            List<String> keys = new ArrayList<>(queries.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                sb.append((i == 0 ? "" : "&") + key + "=" + queries.get(key));
            }
        }
        Request request = new Request.Builder().url(sb.toString()).build();
        try {
            return newCall(request).toString();
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        }

        return null;
    }


    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String getForHeader(String url, Map<String, String> queries, Map<String, Object> heads) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            sb.append("?");
            List<String> keys = new ArrayList<>(queries.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                sb.append((i == 0 ? "" : "&") + key + "=" + queries.get(key));
            }
        }
        Request.Builder build = new Request.Builder();
        if (queries != null && queries.keySet().size() > 0) {
            heads.forEach((k, v) -> build.addHeader(k, String.valueOf(v)));
        }
        try {
            responseBody = newCall(build.url(sb.toString()).build()).toString();
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        }
        return responseBody;
    }

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, ? extends Object> params) {
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, String.valueOf(params.get(key)));
            }
        }
        return postSubmit(url, builder.build());
    }

    /**
     * Post请求发送JSON数据....{"xxx":""xx,"xx":"xxx"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public static String postJsonParams(String url, String jsonParams) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
        return postSubmit(url, requestBody);
    }


    /**
     * 发起Post加密请求
     *
     * @param url        地址
     * @param jsonParams 请求参数
     * @param appId      appId
     * @param appSecret  app密钥
     * @param isDecrypt  响应是否解密
     * @return 响应参数
     */
    public static String encryptPost(String url, String jsonParams, String appId, String appSecret, boolean isDecrypt) {
        EncryptVO enData = EncryptUtils.requestEncrypt(jsonParams, appSecret, appId);
        Request.Builder builder = new Request.Builder();
        builder.addHeader("ak", appId);
        builder.addHeader("sign", enData.getSign());
        Request request = builder.post(RequestBody.create(MEDIA_TYPE_JSON, enData.getBody())).url(url).build();
        return newCallAndDecrypt(request, enData.getAesKey(), isDecrypt);
    }


    /**
     * Post请求发送xml数据....
     * 参数一：请求Url
     * 参数二：请求的xmlString
     * 参数三：请求回调
     */
    public static String postXmlParams(String url, String xml) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_XML, xml);
        return postSubmit(url, requestBody);
    }

    /**
     * 提交Post请求公共方法
     *
     * @param url         请求地址
     * @param requestBody 请求参数
     * @return 响应字符串
     */
    private static String postSubmit(String url, RequestBody requestBody) {
        String responseBody = "";
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            responseBody = newCall(request).toString();
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        }
        return responseBody;
    }


    /**
     * 发送请求公共方法，响应封装
     *
     * @param request 请求
     * @return 响应参数
     */
    private static String newCall(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            throw new IOException("okhttp3 post error >> ex = {}" + ".sss");
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * 发送加密请求，并解密
     *
     * @param request 请求
     * @return 解密参数
     */
    private static String newCallAndDecrypt(Request request, byte[] aesKey, boolean isDecrypt) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && isDecrypt) {
                return EncryptUtils.responseDecrypt(response.body().bytes(), aesKey);
            } else if (response.isSuccessful() && !isDecrypt) {
                return response.body().string();
            }
            throw new RuntimeException();
        } catch (Exception e) {
            log.error("send encrypt request error! detail : " + e.getMessage());
        }
        return null;
    }
}