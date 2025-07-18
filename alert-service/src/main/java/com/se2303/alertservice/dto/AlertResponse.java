package com.se2303.alertservice.dto;

import com.se2303.alertservice.model.Alert;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.sql.Timestamp;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date followUpDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    
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
        this.followUpDate = alert.getFollowUpDate() == null ? null : Timestamp.valueOf(alert.getFollowUpDate());
        this.createdAt = alert.getCreatedAt() == null ? null : Timestamp.valueOf(alert.getCreatedAt());
        this.updatedAt = alert.getUpdatedAt() == null ? null : Timestamp.valueOf(alert.getUpdatedAt());
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

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
    
    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }

    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}