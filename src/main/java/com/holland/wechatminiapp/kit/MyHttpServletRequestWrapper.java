package com.holland.wechatminiapp.kit;


import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final String               bodyStr;
    public final  MyServletInputStream inputStream;

    public MyHttpServletRequestWrapper(HttpServletRequest request, String bodyStr) {
        super(request);
        this.bodyStr = bodyStr;
        this.inputStream = new MyServletInputStream(bodyStr);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }
}
