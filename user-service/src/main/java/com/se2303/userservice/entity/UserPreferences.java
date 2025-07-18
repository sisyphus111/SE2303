package com.se2303.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    private Long id; // 直接使用User的id作为主键
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 使用Auth的id作为这个表的主键
    @JoinColumn(name = "id")
    private Auth auth;
    
    // 通知设置 - 通知方式
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications; // 移除默认值，让它保持null直到被显式设置
    
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications; // 移除默认值
    
    // 通知设置 - 通知类型
    @Column(name = "comment_notifications", nullable = false)
    private Boolean commentNotifications; // 移除默认值
    
    @Column(name = "like_notifications", nullable = false)
    private Boolean likeNotifications; // 移除默认值
    
    @Column(name = "system_notifications", nullable = false)
    private Boolean systemNotifications; // 移除默认值
    
    // 个人偏好 - 媒体设置
    @Column(name = "sound_enabled", nullable = false)
    private Boolean soundEnabled; // 移除默认值
    
    // 个人偏好 - 社区交互
    @Column(name = "show_post_stats", nullable = false)
    private Boolean showPostStats; // 移除默认值
    
    @Column(name = "enable_post_recommendation", nullable = false)
    private Boolean enablePostRecommendation; // 移除默认值
    
    @Column(name = "block_sensitive_content", nullable = false)
    private Boolean blockSensitiveContent; // 移除默认值
    
    // 心理健康设置
    @Column(name = "crisis_intervention_enabled", nullable = false)
    private Boolean crisisInterventionEnabled; // 移除默认值
    
    @Column(name = "mood_tracking_enabled", nullable = false)
    private Boolean moodTrackingEnabled; // 移除默认值
    
    @Column(name = "anonymous_posting", nullable = false)
    private Boolean anonymousPosting; // 移除默认值
    
    @Column(name = "mental_health_data_sharing", nullable = false)
    private Boolean mentalHealthDataSharing; // 移除默认值
    
    // 时间戳
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public UserPreferences() {
        // 设置默认值（只在创建新记录时使用）
        this.pushNotifications = true;
        this.emailNotifications = true;
        this.commentNotifications = true;
        this.likeNotifications = false;
        this.systemNotifications = true;
        this.soundEnabled = true;
        this.showPostStats = true;
        this.enablePostRecommendation = true;
        this.blockSensitiveContent = true;
        this.crisisInterventionEnabled = true;
        this.moodTrackingEnabled = true;
        this.anonymousPosting = false;
        this.mentalHealthDataSharing = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Auth getAuth() {
        return auth;
    }
    
    public void setAuth(Auth auth) {
        this.auth = auth;
        // 由于使用@MapsId，ID会在保存时自动从User中映射
        // 不需要手动设置ID，让JPA自动处理
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getCommentNotifications() {
        return commentNotifications;
    }
    
    public void setCommentNotifications(Boolean commentNotifications) {
        this.commentNotifications = commentNotifications;
    }
    
    public Boolean getLikeNotifications() {
        return likeNotifications;
    }
    
    public void setLikeNotifications(Boolean likeNotifications) {
        this.likeNotifications = likeNotifications;
    }
    
    public Boolean getSystemNotifications() {
        return systemNotifications;
    }
    
    public void setSystemNotifications(Boolean systemNotifications) {
        this.systemNotifications = systemNotifications;
    }
    
    public Boolean getSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(Boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }
    
    public Boolean getShowPostStats() {
        return showPostStats;
    }
    
    public void setShowPostStats(Boolean showPostStats) {
        this.showPostStats = showPostStats;
    }
    
    public Boolean getEnablePostRecommendation() {
        return enablePostRecommendation;
    }
    
    public void setEnablePostRecommendation(Boolean enablePostRecommendation) {
        this.enablePostRecommendation = enablePostRecommendation;
    }
    
    public Boolean getBlockSensitiveContent() {
        return blockSensitiveContent;
    }
    
    public void setBlockSensitiveContent(Boolean blockSensitiveContent) {
        this.blockSensitiveContent = blockSensitiveContent;
    }
    
    public Boolean getCrisisInterventionEnabled() {
        return crisisInterventionEnabled;
    }
    
    public void setCrisisInterventionEnabled(Boolean crisisInterventionEnabled) {
        this.crisisInterventionEnabled = crisisInterventionEnabled;
    }
    
    public Boolean getMoodTrackingEnabled() {
        return moodTrackingEnabled;
    }
    
    public void setMoodTrackingEnabled(Boolean moodTrackingEnabled) {
        this.moodTrackingEnabled = moodTrackingEnabled;
    }
    
    public Boolean getAnonymousPosting() {
        return anonymousPosting;
    }
    
    public void setAnonymousPosting(Boolean anonymousPosting) {
        this.anonymousPosting = anonymousPosting;
    }
    
    public Boolean getMentalHealthDataSharing() {
        return mentalHealthDataSharing;
    }
    
    public void setMentalHealthDataSharing(Boolean mentalHealthDataSharing) {
        this.mentalHealthDataSharing = mentalHealthDataSharing;
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

    public void updatePreferences(UserPreferences preferences) {
        if(preferences.getPushNotifications() != null) this.pushNotifications = preferences.getPushNotifications();
        if(preferences.getEmailNotifications() != null) this.emailNotifications = preferences.getEmailNotifications();
        if(preferences.getCommentNotifications() != null) this.commentNotifications = preferences.getCommentNotifications();
        if(preferences.getLikeNotifications() != null) this.likeNotifications = preferences.getLikeNotifications();
        if(preferences.getSystemNotifications() != null) this.systemNotifications = preferences.getSystemNotifications();
        if(preferences.getSoundEnabled() != null) this.soundEnabled = preferences.getSoundEnabled();
        if(preferences.getShowPostStats() != null) this.showPostStats = preferences.getShowPostStats();
        if(preferences.getEnablePostRecommendation() != null) this.enablePostRecommendation = preferences.getEnablePostRecommendation();
        if(preferences.getBlockSensitiveContent() != null) this.blockSensitiveContent = preferences.getBlockSensitiveContent();
        if(preferences.getCrisisInterventionEnabled() != null) this.crisisInterventionEnabled = preferences.getCrisisInterventionEnabled();
        if(preferences.getMoodTrackingEnabled() != null) this.moodTrackingEnabled = preferences.getMoodTrackingEnabled();
        if(preferences.getAnonymousPosting() != null) this.anonymousPosting = preferences.getAnonymousPosting();
        if(preferences.getMentalHealthDataSharing() != null) this.mentalHealthDataSharing = preferences.getMentalHealthDataSharing();
    }
}
