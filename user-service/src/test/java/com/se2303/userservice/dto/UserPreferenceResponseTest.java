package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.UserPreferenceResponse;
import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.UserPreferences;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserPreferenceResponseTest {
    @Test
    void testConvertToUserPreferenceResponse_Normal() {
        UserPreferences up = new UserPreferences();
        Auth auth = new Auth();
        auth.setId(1L);
        up.setAuth(auth);
        up.setPushNotifications(true);
        up.setEmailNotifications(false);
        up.setCommentNotifications(true);
        up.setLikeNotifications(false);
        up.setSystemNotifications(true);
        up.setSoundEnabled(false);
        up.setShowPostStats(true);
        up.setEnablePostRecommendation(false);
        up.setBlockSensitiveContent(true);
        up.setCrisisInterventionEnabled(false);
        up.setMoodTrackingEnabled(true);
        up.setAnonymousPosting(false);
        up.setMentalHealthDataSharing(true);
        UserPreferenceResponse resp = UserPreferenceResponse.convertToUserPreferenceResponse(up);
        assertEquals(1L, resp.getUserId());
        assertTrue(resp.getPushNotifications());
        assertFalse(resp.getEmailNotifications());
        assertTrue(resp.getCommentNotifications());
        assertFalse(resp.getLikeNotifications());
        assertTrue(resp.getSystemNotifications());
        assertFalse(resp.getSoundEnabled());
        assertTrue(resp.getShowPostStats());
        assertFalse(resp.getEnablePostRecommendation());
        assertTrue(resp.getBlockSensitiveContent());
        assertFalse(resp.getCrisisInterventionEnabled());
        assertTrue(resp.getMoodTrackingEnabled());
        assertFalse(resp.getAnonymousPosting());
        assertTrue(resp.getMentalHealthDataSharing());
    }

    @Test
    void testConvertToUserPreferenceResponse_NullAuth() {
        UserPreferences up = new UserPreferences();
        // 不设置 auth
        assertThrows(NullPointerException.class, () -> {
            UserPreferenceResponse.convertToUserPreferenceResponse(up);
        });
    }

    @Test
    void testSettersAndGetters() {
        UserPreferenceResponse resp = new UserPreferenceResponse();
        resp.setUserId(2L);
        resp.setPushNotifications(true);
        resp.setEmailNotifications(false);
        resp.setCommentNotifications(true);
        resp.setLikeNotifications(false);
        resp.setSystemNotifications(true);
        resp.setSoundEnabled(false);
        resp.setShowPostStats(true);
        resp.setEnablePostRecommendation(false);
        resp.setBlockSensitiveContent(true);
        resp.setCrisisInterventionEnabled(false);
        resp.setMoodTrackingEnabled(true);
        resp.setAnonymousPosting(false);
        resp.setMentalHealthDataSharing(true);
        assertEquals(2L, resp.getUserId());
        assertTrue(resp.getPushNotifications());
        assertFalse(resp.getEmailNotifications());
        assertTrue(resp.getCommentNotifications());
        assertFalse(resp.getLikeNotifications());
        assertTrue(resp.getSystemNotifications());
        assertFalse(resp.getSoundEnabled());
        assertTrue(resp.getShowPostStats());
        assertFalse(resp.getEnablePostRecommendation());
        assertTrue(resp.getBlockSensitiveContent());
        assertFalse(resp.getCrisisInterventionEnabled());
        assertTrue(resp.getMoodTrackingEnabled());
        assertFalse(resp.getAnonymousPosting());
        assertTrue(resp.getMentalHealthDataSharing());
    }
} 