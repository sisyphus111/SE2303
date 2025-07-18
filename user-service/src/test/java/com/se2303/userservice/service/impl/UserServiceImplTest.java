package com.se2303.userservice.service.impl;

import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.repository.UserPreferencesRepository;
import com.se2303.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private UserPreferencesRepository userPreferencesRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private User user;
    private Auth auth;
    private UserPreferences userPreferences;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(123L);
        user.setUsername("user1");
        user.setNickname("nick");
        user.setAvatar("avatar.png");
        user.setProfileVisibility("public");
        user.setRegistrationDate(LocalDate.now());
        auth = new Auth();
        user.setAuth(auth);
        userPreferences = new UserPreferences();
        userPreferences.setAuth(auth);
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        Optional<User> result = userServiceImpl.getUserById(123L);
        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<User> result = userServiceImpl.getUserById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateProfile_AllFields() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        boolean updated = userServiceImpl.updateProfile(123L, "u", "n", "a", "g", LocalDate.now(), "loc", "bio", "tags", "private");
        assertTrue(updated);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateProfile_PartialFields() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        boolean updated = userServiceImpl.updateProfile(123L, null, null, null, null, null, null, null, null, null);
        assertTrue(updated);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        boolean updated = userServiceImpl.updateProfile(999L, "u", "n", "a", "g", LocalDate.now(), "loc", "bio", "tags", "private");
        assertFalse(updated);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_Normal() {
        when(authRepository.save(any(Auth.class))).thenReturn(auth);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(userPreferences);
        User result = userServiceImpl.createUser("123456", "name", "jaccount", "123456789");
        assertNotNull(result);
        verify(authRepository).save(any(Auth.class));
        verify(userRepository).save(any(User.class));
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    void testCreateUser_Admin() {
        when(authRepository.save(any(Auth.class))).thenReturn(auth);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(userPreferences);
        // 管理员ID特殊分支
        User result = userServiceImpl.createUser("900000000438", "admin", "jaccount", "123456789");
        assertNotNull(result);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        List<User> users = userServiceImpl.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void testGetAllNormalUsers() {
        when(userRepository.findByAuthIsUserTrue()).thenReturn(Arrays.asList(user));
        List<User> users = userServiceImpl.getAllNormalUsers();
        assertEquals(1, users.size());
    }

    @Test
    void testGetAllNormalUsers_Empty() {
        when(userRepository.findByAuthIsUserTrue()).thenReturn(Collections.emptyList());
        List<User> users = userServiceImpl.getAllNormalUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void testUpdateUserStatus_Success() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(authRepository.save(any(Auth.class))).thenReturn(auth);
        boolean result = userServiceImpl.updateUserStatus(123L, true);
        assertTrue(result);
        verify(authRepository).save(any(Auth.class));
    }

    @Test
    void testUpdateUserStatus_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        boolean result = userServiceImpl.updateUserStatus(999L, false);
        assertFalse(result);
        verify(authRepository, never()).save(any(Auth.class));
    }

    @Test
    void testGetNormalUserCount() {
        when(userRepository.findByAuthIsUserTrue()).thenReturn(Arrays.asList(user, user));
        int count = userServiceImpl.getNormalUserCount();
        assertEquals(2, count);
    }
} 