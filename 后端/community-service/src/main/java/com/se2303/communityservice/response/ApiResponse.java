package com.se2303.communityservice.response;

public class ApiResponse {
    private boolean success;
    private Object object;
    private String message;

    public ApiResponse(boolean success, Object object, String message) {
        this.success = success;
        this.object = object;
        this.message = message;
    }

    // Getters and setters
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
