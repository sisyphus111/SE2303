package com.se2303.alertservice.dto;

/**
 * 统一API响应格式
 */
public class ApiResponse {
    private boolean success;
    private Object object;
    private String message;
    
    // 构造函数
    public ApiResponse() {}
    
    public ApiResponse(boolean success, Object object, String message) {
        this.success = success;
        this.object = object;
        this.message = message;
    }
    
    // 静态工厂方法
    public static ApiResponse success(Object object, String message) {
        return new ApiResponse(true, object, message);
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

    public Object getObject() {
        return object;
    }

    public String getMessage() {
        return message;
    }

}