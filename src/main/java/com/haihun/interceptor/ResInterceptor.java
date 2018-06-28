package com.haihun.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.security.EncryptUtils;
import com.haihun.comm.security.EncryptVO;
import com.haihun.annotation.ResponseEncrypt;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * 响应拦截器
 *
 * @author kaiser·von·d
 * @version 2018/5/3
 */
@ControllerAdvice(basePackages = "com.haihun.sdk.controller")
public class ResInterceptor implements ResponseBodyAdvice {


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果有声明 注解则排除拦截直接返回原文

        ResponseEncrypt filter = returnType.getMethodAnnotation(ResponseEncrypt.class);
        if (filter != null) {
            return true;
        }
        return false;
    }

    /**
     * 拦截未被声明的响应
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body != null) {
            ServletServerHttpRequest req = (ServletServerHttpRequest) serverHttpRequest;
            HttpServletRequest request = req.getServletRequest();
            // 将响应数据进行编码
            EncryptVO encode = EncryptUtils.responseEncrypt(JSONObject.toJSONString(body), (String) request.getAttribute("appSecret"), (String) request.getAttribute("ak"), (byte[]) request.getAttribute("aesKey"));
            if (encode == null) {
                return new HashMap<>();
            }
            ServletServerHttpResponse resp = (ServletServerHttpResponse) serverHttpResponse;
            HttpServletResponse response = resp.getServletResponse();

            response.setHeader("sign", encode.getSign());
            // 将响应数据写入到流中
            try (OutputStream out = response.getOutputStream()) {
                out.write(encode.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new HashMap<>();
        }
        return body;
    }
}
