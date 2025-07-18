package com.se2303.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jaccount")
public class JaccountConfig {
    private String clientId = "REDACTED";
    private String clientSecret = "REDACTED";
    private String tokenUrl = "https://jaccount.sjtu.edu.cn/oauth2/token";
    private String userInfoUrl = "https://api.sjtu.edu.cn/v1/me/profile";
    private String logoutUrl = "http://jaccount.sjtu.edu.cn/oauth2/logout";
    
    // Getters and Setters
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getTokenUrl() {
        return tokenUrl;
    }
    
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }
    
    public String getUserInfoUrl() {
        return userInfoUrl;
    }
    
    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }
    
    public String getLogoutUrl() {
        return logoutUrl;
    }
    
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
} 