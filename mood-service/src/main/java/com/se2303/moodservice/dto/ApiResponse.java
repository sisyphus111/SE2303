package com.se2303.moodservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private Object object;
    private String message;

    // 默认构造函数
    public ApiResponse() {
    }

    // 全参数构造函数
    public ApiResponse(boolean success, Object object, String message) {
        this.success = success;
        this.object = object;
        this.message = message;
    }

    // 成功响应静态方法
    public static ApiResponse success(String message) {
        return new ApiResponse(true, null, message);
    }

    public static ApiResponse success(Object object, String message) {
        return new ApiResponse(true, object, message);
    }

    // 失败响应静态方法
    public static ApiResponse error(String message) {
        return new ApiResponse(false, null, message);
    }

    public static ApiResponse error(Object object, String message) {
        return new ApiResponse(false, object, message);
    }

    // Getter和Setter方法
    public boolean isSuccess() {
        return success;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", object=" + object +
                ", message='" + message + '\'' +
                '}';
    }
} 