package com.haihun.filter;

import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.constant.Constant;
import com.haihun.comm.security.AES;
import com.haihun.comm.security.EncryptUtils;
import com.haihun.comm.security.RSA;
import com.haihun.sdk.pojo.Game;
import com.haihun.sdk.service.GameService;
import com.haihun.sdk.vo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.ParameterMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 请求参数自动解密过滤器
 *
 * @author kaiser·von·d
 * @version 2018/5/3
 */
@Slf4j
public class ParameterFilter implements Filter {

    @Autowired
    public GameService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            String ak = request.getHeader("ak");
            String sign = request.getHeader("sign");
            if (StringUtils.isNotBlank(ak) && StringUtils.isNotBlank(sign)) {
                // 进入加密流程
                HttpServletRequest req = autoDecrypt(request, ak, sign, servletResponse);
                if (req != null) {
                    filterChain.doFilter(req, servletResponse);
                }
                return;
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public HttpServletRequest autoDecrypt(HttpServletRequest request, String ak, String sign, ServletResponse response) {

        CustomHttpServletRequest reqeuest = null;
        try {
            HashMap<String, byte[]> map = EncryptUtils.requestDecrypt(request.getInputStream());
            if (map == null) {
                return null;
            }

            //　根据appId 查询到游戏
            Game game = service.findById(ak);


            if (game != null) {
                if (EncryptUtils.checkSign(game.getAppSecret(), map.get("body"), sign)) {
                    // 解密得出 aesKey，数据报原文
                    byte[] aesKey = RSA.decryptByPrivateArr(map.get("aesKey"), RSA.getPrivateKey(Constant.APP_PRIVATE_KEY));

                    String json = new String(AES.decrypt(map.get("payload"), aesKey), "UTF-8");

                    JSONObject param = JSONObject.parseObject(json);

                    // 将传入参数去除空格
                    List<String> keys = new ArrayList<>(param.keySet());
                    keys.forEach(k -> {
                        Object val = param.get(k);
                        param.remove(k);
                        String key = k.trim();
                        param.put(key, val);
                    });

                    param.put("appId", ak);

                    ParameterMap<String, String[]> parameterMap = (ParameterMap) request.getParameterMap();

                    // 解除参数容器锁定
                    parameterMap.setLocked(false);

                    // 将参数put到参数容器
                    param.forEach((k, v) -> {
                        request.setAttribute(k, v);
                        parameterMap.put(k, new String[]{String.valueOf(v)});
                    });

                    // 重新上锁
                    reqeuest = new CustomHttpServletRequest(request, param.toJSONString().getBytes());
                    parameterMap.setLocked(true);
                    reqeuest.setAttribute("aesKey", aesKey);
                    reqeuest.setAttribute("appSecret", game.getAppSecret());
                    reqeuest.setAttribute("ak", game.getAppId());
                } else {
                    Result result = new Result();
                    result.error("Incorrect  Sign !");
                    log.error("Auto Dencrypt");
                    response.getOutputStream().write(JSONObject.toJSONString(result).getBytes());
                    return null;
                }
                // 放行请求
                return reqeuest;
            } else {
                Result result = new Result();
                result.error("Not Found AppId !");
                response.getOutputStream().write(JSONObject.toJSONString(result).getBytes());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public void destroy() {

    }
}
