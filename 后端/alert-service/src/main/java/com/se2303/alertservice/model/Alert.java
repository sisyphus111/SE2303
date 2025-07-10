package com.se2303.alertservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 警报实体类
 * 用于存储用户危险操作和AI超时的警报信息
 */
@Entity
@Table(name = "alert", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    @Column(name = "alert_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;
    
    @Column(name = "alert_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertLevel alertLevel = AlertLevel.MEDIUM;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertStatus status = AlertStatus.PENDING;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "operation_details", columnDefinition = "TEXT")
    private String operationDetails;
    
    @Column(name = "ai_session_id")
    private String aiSessionId;
    
    @Column(name = "ai_timeout_duration")
    private Integer aiTimeoutDuration; // 超时时长（分钟）
    
    @Column(name = "assigned_counselor_id")
    private String assignedCounselorId;
    

    
    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Alert() {}
    
    public Alert(String userId, AlertType alertType) {
        this.userId = userId;
        this.alertType = alertType;
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
    
    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", alertType=" + alertType +
                ", alertLevel=" + alertLevel +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
} 