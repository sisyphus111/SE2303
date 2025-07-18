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

}