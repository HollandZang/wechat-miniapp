package com.holland.wechatminiapp.kit;

public class ThrowableKit {
    public static String getMsg(Throwable throwable) {
        String message = throwable.getMessage();
        return null != message
                ? message
                : throwable.getClass().getName();
    }
}
