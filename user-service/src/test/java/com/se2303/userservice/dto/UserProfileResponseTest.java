package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.UserProfileResponse;
import com.se2303.userservice.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileResponseTest {
    @Test
    void testConvertToUserProfileResponse_Normal() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setNickname("nick");
        user.setAvatar("a.png");
        user.setGender("male");
        user.setBirthday(java.time.LocalDate.of(2000,1,1));
        user.setTags("tag1,tag2");
        user.setLocation("loc");
        user.setBio("bio");
        user.setProfileVisibility("public");
        user.setRegistrationDate(java.time.LocalDate.now());
        UserProfileResponse resp = UserProfileResponse.convertToUserProfileResponse(user);
        assertEquals(user.getId(), resp.getUserId());
        assertEquals(user.getUsername(), resp.getUsername());
        assertEquals(user.getNickname(), resp.getNickname());
        assertEquals(user.getAvatar(), resp.getAvatar());
        assertEquals(user.getGender(), resp.getGender());
        assertEquals("2000-01-01", resp.getBirthday());
        assertEquals(user.getTags(), resp.getTags());
        assertEquals(user.getLocation(), resp.getLocation());
        assertEquals(user.getBio(), resp.getBio());
        assertEquals(user.getProfileVisibility(), resp.getProfileVisibility());
        assertNotNull(resp.getRegistrationDate());
    }

    @Test
    void testConvertToUserProfileResponse_NullFields() {
        User user = new User();
        user.setId(2L);
        user.setUsername("u2");
        // 其它字段均为 null
        UserProfileResponse resp = UserProfileResponse.convertToUserProfileResponse(user);
        assertEquals(user.getId(), resp.getUserId());
        assertEquals(user.getUsername(), resp.getUsername());
        assertNull(resp.getNickname());
        assertNull(resp.getAvatar());
        assertNull(resp.getGender());
        assertNull(resp.getBirthday());
        assertNull(resp.getTags());
        assertNull(resp.getLocation());
        assertNull(resp.getBio());
        assertNull(resp.getProfileVisibility());
        assertNull(resp.getRegistrationDate());
    }

    @Test
    void testSettersAndGetters() {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setUserId(3L);
        resp.setUsername("u3");
        resp.setNickname("n3");
        resp.setAvatar("a3");
        resp.setGender("g3");
        resp.setBirthday("2020-01-01");
        resp.setTags("t3");
        resp.setLocation("l3");
        resp.setBio("b3");
        resp.setProfileVisibility("pv3");
        resp.setRegistrationDate("2024-01-01");
        assertEquals(3L, resp.getUserId());
        assertEquals("u3", resp.getUsername());
        assertEquals("n3", resp.getNickname());
        assertEquals("a3", resp.getAvatar());
        assertEquals("g3", resp.getGender());
        assertEquals("2020-01-01", resp.getBirthday());
        assertEquals("t3", resp.getTags());
        assertEquals("l3", resp.getLocation());
        assertEquals("b3", resp.getBio());
        assertEquals("pv3", resp.getProfileVisibility());
        assertEquals("2024-01-01", resp.getRegistrationDate());
    }
} 