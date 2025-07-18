package com.se2303.alertservice.dto;

import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建警报请求DTO
 */
public class CreateAlertRequest {
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotNull(message = "警报类型不能为空")
    private AlertType alertType;
    
    private AlertLevel alertLevel = AlertLevel.MEDIUM;
    
    private String description;
    
    private String operationDetails;
    
    private String aiSessionId;
    
    private Integer aiTimeoutDuration;
    
    // 构造函数
    public CreateAlertRequest() {}

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public AlertType getAlertType() {
        return alertType;
    }
    
    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }
    
    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
    
    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOperationDetails() {
        return operationDetails;
    }
    
    public void setOperationDetails(String operationDetails) {
        this.operationDetails = operationDetails;
    }
    
    public String getAiSessionId() {
        return aiSessionId;
    }
    
    public void setAiSessionId(String aiSessionId) {
        this.aiSessionId = aiSessionId;
    }
    
    public Integer getAiTimeoutDuration() {
        return aiTimeoutDuration;
    }
    
    public void setAiTimeoutDuration(Integer aiTimeoutDuration) {
        this.aiTimeoutDuration = aiTimeoutDuration;
    }
} 