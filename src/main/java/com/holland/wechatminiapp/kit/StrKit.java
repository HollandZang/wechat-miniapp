package com.holland.wechatminiapp.kit;

public class StrKit {
    public static String trunc(String field, int length) {
        if (field == null)
            return null;
        if (field.length() <= length)
            return field;
        return field.substring(0, length);
    }
}
