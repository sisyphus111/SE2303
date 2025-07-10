package com.se2303.userservice.dto.request;

// 用户偏好设置更新请求体
public class UpdatePreferencesRequest {
    // 通知设置 - 通知方式
    private Boolean pushNotifications;
    private Boolean emailNotifications;
    
    // 通知设置 - 通知类型
    public Boolean commentNotifications;
    private Boolean likeNotifications;
    private Boolean systemNotifications;
    
    // 个人偏好 - 媒体设置
    private Boolean soundEnabled;
    
    // 个人偏好 - 社区交互
    private Boolean showPostStats;
    private Boolean enablePostRecommendation;
    private Boolean blockSensitiveContent;
    
    // 心理健康设置
    private Boolean crisisInterventionEnabled;
    private Boolean moodTrackingEnabled;
    private Boolean anonymousPosting;
    private Boolean mentalHealthDataSharing;

    public UpdatePreferencesRequest(Boolean pushNotifications, Boolean emailNotifications, Boolean commentNotifications, Boolean likeNotifications, Boolean systemNotifications, Boolean soundEnabled, Boolean showPostStats, Boolean enablePostRecommendation, Boolean blockSensitiveContent, Boolean crisisInterventionEnabled, Boolean moodTrackingEnabled, Boolean anonymousPosting, Boolean mentalHealthDataSharing) {
        this.pushNotifications = pushNotifications;
        this.emailNotifications = emailNotifications;
        this.commentNotifications = commentNotifications;
        this.likeNotifications = likeNotifications;
        this.systemNotifications = systemNotifications;
        this.soundEnabled = soundEnabled;
        this.showPostStats = showPostStats;
        this.enablePostRecommendation = enablePostRecommendation;
        this.blockSensitiveContent = blockSensitiveContent;
    }

    public UpdatePreferencesRequest() {}

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public Boolean getCommentNotifications() {
        return commentNotifications;
    }

    public Boolean getLikeNotifications() {
        return likeNotifications;
    }

    public Boolean getSystemNotifications() {
        return systemNotifications;
    }

    public Boolean getSoundEnabled() {
        return soundEnabled;
    }

    public Boolean getShowPostStats() {
        return showPostStats;
    }

    public Boolean getEnablePostRecommendation() {
        return enablePostRecommendation;
    }

    public Boolean getBlockSensitiveContent() {
        return blockSensitiveContent;
    }

    public Boolean getCrisisInterventionEnabled() {
        return crisisInterventionEnabled;
    }

    public Boolean getMoodTrackingEnabled() {
        return moodTrackingEnabled;
    }

    public Boolean getAnonymousPosting() {
        return anonymousPosting;
    }

    public Boolean getMentalHealthDataSharing() {
        return mentalHealthDataSharing;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public void setCommentNotifications(Boolean commentNotifications) {
        this.commentNotifications = commentNotifications;
    }

    public void setLikeNotifications(Boolean likeNotifications) {
        this.likeNotifications = likeNotifications;
    }

    public void setSystemNotifications(Boolean systemNotifications) {
        this.systemNotifications = systemNotifications;
    }

    public void setSoundEnabled(Boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void setShowPostStats(Boolean showPostStats) {
        this.showPostStats = showPostStats;
    }

    public void setEnablePostRecommendation(Boolean enablePostRecommendation) {
        this.enablePostRecommendation = enablePostRecommendation;
    }

    public void setBlockSensitiveContent(Boolean blockSensitiveContent) {
        this.blockSensitiveContent = blockSensitiveContent;
    }

    public void setCrisisInterventionEnabled(Boolean crisisInterventionEnabled) {
        this.crisisInterventionEnabled = crisisInterventionEnabled;
    }

    public void setMoodTrackingEnabled(Boolean moodTrackingEnabled) {
        this.moodTrackingEnabled = moodTrackingEnabled;
    }

    public void setAnonymousPosting(Boolean anonymousPosting) {
        this.anonymousPosting = anonymousPosting;
    }

    public void setMentalHealthDataSharing(Boolean mentalHealthDataSharing) {
        this.mentalHealthDataSharing = mentalHealthDataSharing;
    }
}