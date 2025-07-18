package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.UserManagementResponse;
import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserManagementResponseTest {
    @Test
    void testConvertToUserManagementResponse_Normal() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setNickname("nick");
        user.setAvatar("a.png");
        user.setRegistrationDate(java.time.LocalDate.now());
        Auth auth = new Auth();
        auth.setSjtuId("s1");
        auth.setJaccount("j1");
        auth.setValid(true);
        auth.setIsUser(true);
        auth.setTelephone("REDACTED1");
        auth.setName("real");
        user.setAuth(auth);
        UserManagementResponse resp = UserManagementResponse.convertToUserManagementResponse(user);
        assertEquals(user.getId(), resp.getId());
        assertEquals(user.getUsername(), resp.getUsername());
        assertEquals(user.getNickname(), resp.getNickname());
        assertEquals(user.getAvatar(), resp.getAvatar());
        assertEquals(auth.getSjtuId(), resp.getSjtuId());
        assertEquals(auth.getJaccount(), resp.getJaccount());
        assertEquals(auth.getValid(), resp.getValid());
        assertEquals(auth.getIsUser(), resp.getIsUser());
        assertEquals(auth.getTelephone(), resp.getPhone());
        assertEquals(auth.getName(), resp.getRealname());
        assertNotNull(resp.getRegistrationDate());
    }

    @Test
    void testConvertToUserManagementResponse_NullAuth() {
        User user = new User();
        user.setId(2L);
        user.setUsername("u2");
        // 不设置 auth
        assertThrows(NullPointerException.class, () -> {
            UserManagementResponse.convertToUserManagementResponse(user);
        });
    }

    @Test
    void testSettersAndGetters() {
        UserManagementResponse resp = new UserManagementResponse();
        resp.setId(1L);
        resp.setUsername("u");
        resp.setNickname("n");
        resp.setAvatar("a");
        resp.setSjtuId("s");
        resp.setJaccount("j");
        resp.setRegistrationDate("2024-01-01");
        resp.setValid(true);
        resp.setIsUser(false);
        resp.setPhone("p");
        resp.setRealname("r");
        assertEquals(1L, resp.getId());
        assertEquals("u", resp.getUsername());
        assertEquals("n", resp.getNickname());
        assertEquals("a", resp.getAvatar());
        assertEquals("s", resp.getSjtuId());
        assertEquals("j", resp.getJaccount());
        assertEquals("2024-01-01", resp.getRegistrationDate());
        assertTrue(resp.getValid());
        assertFalse(resp.getIsUser());
        assertEquals("p", resp.getPhone());
        assertEquals("r", resp.getRealname());
    }
} 