package com.se2303.userservice.service.impl;

import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.UserPreferences;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.repository.UserPreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceImplTest {
    @Mock
    private UserPreferencesRepository userPreferencesRepository;
    @Mock
    private AuthRepository authRepository;
    @InjectMocks
    private UserPreferencesServiceImpl userPreferencesServiceImpl;
    private UserPreferences preferences;
    private Auth auth;

    @BeforeEach
    void setUp() {
        preferences = new UserPreferences();
        auth = new Auth();
        preferences.setAuth(auth);
    }

    @Test
    void testGetUserPreferencesByUserId_Found() {
        when(userPreferencesRepository.findById(123L)).thenReturn(Optional.of(preferences));
        Optional<UserPreferences> result = userPreferencesServiceImpl.getUserPreferencesByUserId(123L);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserPreferencesByUserId_NotFound() {
        when(userPreferencesRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<UserPreferences> result = userPreferencesServiceImpl.getUserPreferencesByUserId(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateDefaultPreferences() {
        when(authRepository.findById(123L)).thenReturn(Optional.of(auth));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(preferences);
        UserPreferences result = userPreferencesServiceImpl.createDefaultPreferences(123L);
        assertNotNull(result);
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    void testSaveOrUpdateUserPreferencesByUserId_Update() {
        UserPreferences newPrefs = new UserPreferences();
        when(userPreferencesRepository.findById(123L)).thenReturn(Optional.of(preferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(preferences);
        Optional<UserPreferences> result = userPreferencesServiceImpl.saveOrUpdateUserPreferencesByUserId(123L, newPrefs);
        assertTrue(result.isPresent());
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    void testSaveOrUpdateUserPreferencesByUserId_Create() {
        UserPreferences newPrefs = new UserPreferences();
        when(userPreferencesRepository.findById(123L)).thenReturn(Optional.empty());
        when(authRepository.findById(123L)).thenReturn(Optional.of(auth));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenReturn(newPrefs);
        Optional<UserPreferences> result = userPreferencesServiceImpl.saveOrUpdateUserPreferencesByUserId(123L, newPrefs);
        assertTrue(result.isPresent());
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    void testSaveOrUpdateUserPreferencesByUserId_UserNotFound() {
        UserPreferences newPrefs = new UserPreferences();
        when(userPreferencesRepository.findById(123L)).thenReturn(Optional.empty());
        when(authRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userPreferencesServiceImpl.saveOrUpdateUserPreferencesByUserId(123L, newPrefs));
    }
} 