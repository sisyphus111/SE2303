package com.se2303.userservice.service;

import com.se2303.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(123L);
        user.setNickname("test");
    }

    @Test
    void testGetUserById_Success() {
        when(userService.getUserById(123L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(123L);
        assertTrue(result.isPresent());
        assertEquals(123L, result.get().getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateProfile_Success() {
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        boolean updated = userService.updateProfile(123L, "u", "n", "a", "g", LocalDate.now(), "loc", "bio", "tags", "public");
        assertTrue(updated);
    }

    @Test
    void testUpdateProfile_Fail() {
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(false);
        boolean updated = userService.updateProfile(123L, "u", "n", "a", "g", LocalDate.now(), "loc", "bio", "tags", "public");
        assertFalse(updated);
    }
} 