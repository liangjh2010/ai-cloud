package com.proj.ai.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    
    private int code;
    private String msg;
    private T data;
    
    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }
    
    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功返回（自定义消息）
     */
    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(200, msg, data);
    }
    
    /**
     * 失败返回
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }
    
    /**
     * 失败返回（自定义消息）
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
    
    /**
     * 自定义返回
     */
    public static <T> Result<T> build(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}

