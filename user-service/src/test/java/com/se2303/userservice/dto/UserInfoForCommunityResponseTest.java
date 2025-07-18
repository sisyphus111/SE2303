package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.UserInfoForCommunityResponse;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserInfoForCommunityResponseTest {
    @Test
    void testConvertToUserInfoForCommunityResponse_Normal() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setNickname("nick");
        user.setAvatar("a.png");
        UserPreferences up = new UserPreferences();
        up.setAnonymousPosting(true);
        UserInfoForCommunityResponse resp = UserInfoForCommunityResponse.convertToUserInfoForCommunityResponse(user, up);
        assertEquals(user.getId(), resp.getUserId());
        assertEquals(user.getUsername(), resp.getUsername());
        assertEquals(user.getNickname(), resp.getNickname());
        assertEquals(user.getAvatar(), resp.getAvatar());
        assertTrue(resp.getValid());
    }

    @Test
    void testConvertToUserInfoForCommunityResponse_NullFields() {
        User user = new User();
        UserPreferences up = new UserPreferences();
        up.setAnonymousPosting(false);
        UserInfoForCommunityResponse resp = UserInfoForCommunityResponse.convertToUserInfoForCommunityResponse(user, up);
        assertNull(resp.getUserId());
        assertNull(resp.getUsername());
        assertNull(resp.getNickname());
        assertNull(resp.getAvatar());
        assertFalse(resp.getValid());
    }

    @Test
    void testSettersAndGetters() {
        UserInfoForCommunityResponse resp = new UserInfoForCommunityResponse();
        resp.setUserId(2L);
        resp.setUsername("u2");
        resp.setNickname("n2");
        resp.setAvatar("a2");
        resp.setValid(true);
        assertEquals(2L, resp.getUserId());
        assertEquals("u2", resp.getUsername());
        assertEquals("n2", resp.getNickname());
        assertEquals("a2", resp.getAvatar());
        assertTrue(resp.getValid());
    }
} 