package com.se2303.gateway.dto.response;

public class ApiResponse {
    boolean success;
    Object object;
    String message;

    public ApiResponse(boolean success, Object object, String message) {
        this.success = success;
        this.object = object;
        this.message = message;
    }

    public ApiResponse(){}

    public boolean getSuccess() { return success; }
    public Object getObject() { return object; }
    public String getMessage() { return message; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setObject(Object object) { this.object = object; }
    public void setMessage(String message) { this.message = message; }
}


