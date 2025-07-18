package com.se2303.chatservice.dto;

public class ApiResponse {
    private boolean success;
    private Object object;
    private String message;

    public ApiResponse() {}

    public ApiResponse(boolean success, Object object, String message) {
        this.success = success;
        this.object = object;
        this.message = message;
    }

    public static ApiResponse success(Object object, String message) {
        return new ApiResponse(true, object, message);
    }

    public static ApiResponse success(Object object) {
        return new ApiResponse(true, object, "操作成功");
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, null, message);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, null, message);
    }

    // Getter和Setter方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public void setMessage(String message) {
        this.message = message;
    }
} 