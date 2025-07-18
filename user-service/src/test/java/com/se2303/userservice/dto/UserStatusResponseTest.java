package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.UserStatusResponse;
import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserStatusResponseTest {
    @Test
    void testConvertToUserStatusResponse_Normal() {
        User user = new User();
        user.setId(1L);
        Auth auth = new Auth();
        auth.setIsUser(true);
        auth.setValid(true);
        user.setAuth(auth);
        UserStatusResponse resp = UserStatusResponse.convertToUserStatusResponse(user);
        assertEquals(user.getId(), resp.getUserId());
        assertTrue(resp.getIdentity());
        assertTrue(resp.getValid());
    }

    @Test
    void testConvertToUserStatusResponse_NullAuth() {
        User user = new User();
        user.setId(2L);
        // 不设置 auth
        assertThrows(NullPointerException.class, () -> {
            UserStatusResponse.convertToUserStatusResponse(user);
        });
    }

    @Test
    void testSettersAndGetters() {
        UserStatusResponse resp = new UserStatusResponse();
        resp.setUserId(3L);
        resp.setIdentity(false);
        resp.setValid(true);
        assertEquals(3L, resp.getUserId());
        assertFalse(resp.getIdentity());
        assertTrue(resp.getValid());
    }
} 