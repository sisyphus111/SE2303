package com.se2303.inspectionservice.dto;

public class ApiResponse {
    Boolean success;
    Object data;
    String message;
    
    public ApiResponse(Boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public ApiResponse() {}

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
