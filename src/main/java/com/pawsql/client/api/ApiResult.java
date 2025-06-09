package com.pawsql.client.api;

import java.io.Serializable;

public class ApiResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public ApiResult() {
    }

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> succ(T data) {
        return succ(200, "操作成功", data);
    }

    public static <T> ApiResult<T> succ(int code, String msg, T data) {
        ApiResult<T> r = new ApiResult<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setData(data);
        return r;
    }

    public static <T> ApiResult<T> fail(String msg) {
        return fail(400, msg, null);
    }

    public static <T> ApiResult<T> fail(String msg, T data) {
        return fail(400, msg, data);
    }

    public static <T> ApiResult<T> fail(int code, String msg, T data) {
        ApiResult<T> r = new ApiResult<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setData(data);
        return r;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
