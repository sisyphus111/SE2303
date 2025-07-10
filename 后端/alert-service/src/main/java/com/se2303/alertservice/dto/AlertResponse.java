package com.se2303.alertservice.dto;

import com.se2303.alertservice.model.Alert;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;

import java.time.LocalDateTime;

/**
 * 警报响应DTO
 */
public class AlertResponse {
    private Long id;
    private String userId;

    private AlertType alertType;
    private String alertTypeDescription;
    private AlertLevel alertLevel;
    private String alertLevelDescription;
    private AlertStatus status;
    private String statusDescription;
    private String description;
    private String operationDetails;
    private String aiSessionId;
    private Integer aiTimeoutDuration;
    private String assignedCounselorId;

    private String followUpNotes;
    private LocalDateTime followUpDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public AlertResponse() {}
    
    public AlertResponse(Alert alert) {
        this.id = alert.getId();
        this.userId = alert.getUserId();

        this.alertType = alert.getAlertType();
        this.alertTypeDescription = alert.getAlertType().getDescription();
        this.alertLevel = alert.getAlertLevel();
        this.alertLevelDescription = alert.getAlertLevel().getDescription();
        this.status = alert.getStatus();
        this.statusDescription = alert.getStatus().getDescription();
        this.description = alert.getDescription();
        this.operationDetails = alert.getOperationDetails();
        this.aiSessionId = alert.getAiSessionId();
        this.aiTimeoutDuration = alert.getAiTimeoutDuration();
        this.assignedCounselorId = alert.getAssignedCounselorId();

        this.followUpNotes = alert.getFollowUpNotes();
        this.followUpDate = alert.getFollowUpDate();
        this.createdAt = alert.getCreatedAt();
        this.updatedAt = alert.getUpdatedAt();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getAlertTypeDescription() {
        return alertTypeDescription;
    }
    
    public void setAlertTypeDescription(String alertTypeDescription) {
        this.alertTypeDescription = alertTypeDescription;
    }
    
    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
    
    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }
    
    public String getAlertLevelDescription() {
        return alertLevelDescription;
    }
    
    public void setAlertLevelDescription(String alertLevelDescription) {
        this.alertLevelDescription = alertLevelDescription;
    }
    
    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    
    public String getStatusDescription() {
        return statusDescription;
    }
    
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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
    
    public String getAssignedCounselorId() {
        return assignedCounselorId;
    }
    
    public void setAssignedCounselorId(String assignedCounselorId) {
        this.assignedCounselorId = assignedCounselorId;
    }
    

    
    public String getFollowUpNotes() {
        return followUpNotes;
    }
    
    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }
    
    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 