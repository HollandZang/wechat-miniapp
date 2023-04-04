package com.holland.wechatminiapp.kit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Res<T> {
    public final int    code;
    public final String msg;
    public final T      data;

    public static <T> Res<T> success(T data) {
        return new Res<T>(0, "", data);
    }

    public static <T> Res<T> notFound(String obj) {
        return new Res<T>(1, "目标对象未找到:[" + obj + "]", null);
    }

    public static <T> Res<T> exists(String obj) {
        return new Res<T>(1, "目标对象已存在:[" + obj + "]", null);
    }
}
