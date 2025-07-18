package com.se2303.talkservice.dto;

/**
 * API响应DTO
 */
public class ApiResponse {
    
    private boolean success;        // 是否成功
    private String message;         // 响应消息
    private Object data;           // 响应数据
    private int code;              // 响应代码
    private long timestamp;        // 时间戳
    
    // 构造函数
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ApiResponse(boolean success, String message, Object data, int code) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }
    
    // 成功响应
    public static ApiResponse success(Object data, String message) {
        return new ApiResponse(true, message, data, 200);
    }
    
    public static ApiResponse success(Object data) {
        return success(data, "操作成功");
    }
    
    public static ApiResponse success(String message) {
        return success(null, message);
    }
    
    public static ApiResponse success() {
        return success(null, "操作成功");
    }
    
    // 错误响应
    public static ApiResponse error(String message, int code) {
        return new ApiResponse(false, message, null, code);
    }
    
    public static ApiResponse error(String message) {
        return error(message, 500);
    }
    
    public static ApiResponse error() {
        return error("操作失败", 500);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                ", timestamp=" + timestamp +
                '}';
    }
} 