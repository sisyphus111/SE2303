package com.se2303.userservice.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void testGetterSetter() {
        User user = new User();
        user.setId(123L);
        user.setUsername("user1");
        user.setNickname("test");
        user.setAvatar("avatar1");
        assertEquals(123L, user.getId());
        assertEquals("user1", user.getUsername());
        assertEquals("test", user.getNickname());
        assertEquals("avatar1", user.getAvatar());
    }
} 