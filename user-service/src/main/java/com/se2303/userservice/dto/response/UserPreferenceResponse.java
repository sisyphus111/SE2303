package com.se2303.userservice.dto.response;

import com.se2303.userservice.entity.UserPreferences;

public class UserPreferenceResponse {
    private Long userId;
    private Boolean pushNotifications;
    private Boolean emailNotifications;
    private Boolean commentNotifications;
    private Boolean likeNotifications;
    private Boolean systemNotifications;
    private Boolean soundEnabled;
    private Boolean showPostStats;
    private Boolean enablePostRecommendation;
    private Boolean blockSensitiveContent;
    private Boolean crisisInterventionEnabled;
    private Boolean moodTrackingEnabled;
    private Boolean anonymousPosting;
    private Boolean mentalHealthDataSharing;

    public UserPreferenceResponse() {}

    public Long getUserId() {
        return userId;
    }

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

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public static UserPreferenceResponse convertToUserPreferenceResponse(UserPreferences userPreferences) {
        UserPreferenceResponse userPreferenceResponse = new UserPreferenceResponse();
        userPreferenceResponse.setUserId(userPreferences.getAuth().getId());
        userPreferenceResponse.setPushNotifications(userPreferences.getPushNotifications());
        userPreferenceResponse.setEmailNotifications(userPreferences.getEmailNotifications());
        userPreferenceResponse.setCommentNotifications(userPreferences.getCommentNotifications());
        userPreferenceResponse.setLikeNotifications(userPreferences.getLikeNotifications());
        userPreferenceResponse.setSystemNotifications(userPreferences.getSystemNotifications());
        userPreferenceResponse.setSoundEnabled(userPreferences.getSoundEnabled());
        userPreferenceResponse.setShowPostStats(userPreferences.getShowPostStats());
        userPreferenceResponse.setEnablePostRecommendation(userPreferences.getEnablePostRecommendation());
        userPreferenceResponse.setBlockSensitiveContent(userPreferences.getBlockSensitiveContent());
        userPreferenceResponse.setCrisisInterventionEnabled(userPreferences.getCrisisInterventionEnabled());
        userPreferenceResponse.setMoodTrackingEnabled(userPreferences.getMoodTrackingEnabled());
        userPreferenceResponse.setAnonymousPosting(userPreferences.getAnonymousPosting());
        userPreferenceResponse.setMentalHealthDataSharing(userPreferences.getMentalHealthDataSharing());
        return userPreferenceResponse;
    }
}
