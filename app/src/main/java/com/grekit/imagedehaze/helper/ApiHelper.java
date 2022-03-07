package com.grekit.imagedehaze.helper;

public class ApiHelper<T> {
    private int code;        // 返回码
    private String message;  // 返回信息
    private T response;      // 返回数据

    public ApiHelper(int code, String message, T response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getResponse() {
        return response;
    }
}
