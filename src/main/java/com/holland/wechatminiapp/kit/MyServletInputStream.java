package com.holland.wechatminiapp.kit;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyServletInputStream extends ServletInputStream {
    public final ByteArrayInputStream byteArrayInputStream;

    public MyServletInputStream(String bodyStr) {
        this.byteArrayInputStream = new ByteArrayInputStream(bodyStr.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }
}
