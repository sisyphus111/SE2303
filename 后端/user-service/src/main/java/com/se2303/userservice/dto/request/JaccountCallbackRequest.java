package com.se2303.userservice.dto.request;

public class JaccountCallbackRequest {
    private String code;
    private String redirectUri;
    private String state;
    
    // 无参构造函数
    public JaccountCallbackRequest() {}
    
    // 全参构造函数
    public JaccountCallbackRequest(String code, String redirectUri, String state) {
        this.code = code;
        this.redirectUri = redirectUri;
        this.state = state;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return "JaccountCallbackRequest{" +
                "code='" + (code != null ? code.substring(0, Math.min(10, code.length())) + "..." : "null") + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
} 