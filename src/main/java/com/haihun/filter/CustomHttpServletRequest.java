package com.haihun.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 自定义request请求处理类
 *
 * @author kaiser·von·d
 * @version 2018/5/4
 */
public class CustomHttpServletRequest extends HttpServletRequestWrapper {

    // 用于缓存在自动解密过程中 reuqest被读取掉的流
    private final byte[] body;

    public CustomHttpServletRequest(HttpServletRequest request, byte[] data) throws IOException {
        super(request);
        body = data;

    }



    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            //重写read方法 使其读取缓存中的字节
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }


}
