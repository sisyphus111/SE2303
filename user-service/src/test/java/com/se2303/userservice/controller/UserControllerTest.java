package com.se2303.userservice.controller;

import com.se2303.userservice.dto.request.UpdatePreferencesRequest;
import com.se2303.userservice.dto.request.UpdateProfileRequest;
import com.se2303.userservice.dto.response.*;
import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;
import com.se2303.userservice.service.FileUploadService;
import com.se2303.userservice.service.UserPreferencesService;
import com.se2303.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserPreferencesService userPreferencesService;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private UserController userController;
    private User user;
    private UserPreferences userPreferences;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setNickname("nick");
        user.setAvatar("avatar.png");
        user.setProfileVisibility("public");
        user.setRegistrationDate(LocalDate.now());

        // mock Auth
        Auth auth = new Auth();
        auth.setId(1L);
        auth.setJaccount("jaccount1");
        auth.setSjtuId("123456");
        auth.setName("testuser");
        auth.setTelephone("REDACTED1");
        auth.setIsUser(true);
        auth.setValid(true);
        auth.setCreatedAt(java.time.LocalDateTime.now());
        user.setAuth(auth);

        userPreferences = new UserPreferences();
        userPreferences.setAuth(auth);
        // 全局 lenient mock，避免不必要的 stubbing 报错
        lenient().when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        lenient().when(userPreferencesService.getUserPreferencesByUserId(anyLong())).thenReturn(Optional.of(userPreferences));
    }

    @Test
    void testGetCommunityInfo_Success() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userPreferencesService.getUserPreferencesByUserId(1L)).thenReturn(Optional.of(userPreferences));
        ResponseEntity<ApiResponse<UserInfoForCommunityResponse>> resp = userController.getCommunityInfo(1L, request);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().getSuccess());
    }

    @Test
    void testGetCommunityInfo_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse<UserInfoForCommunityResponse>> resp = userController.getCommunityInfo(1L, request);
        assertEquals(404, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testGetCommunityInfo_Exception() {
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("fail"));
        ResponseEntity<ApiResponse<UserInfoForCommunityResponse>> resp = userController.getCommunityInfo(1L, request);
        assertEquals(500, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testGetProfile_Success() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.getProfile(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetProfile_Invalid() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.getProfile(request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getProfile(request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetProfile_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.getProfile(request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_Success() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("user1");
        req.setNickname("nick");
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_Invalid() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_NotIdentity() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        lenient().when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_EmptyUsername() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("");
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_BirthdayFormatError() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setBirthday("not-a-date");
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfile_Fail() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(false);
        ResponseEntity<?> response = userController.updateProfile(req, request);
        int code = response.getStatusCode().value();
        assertTrue(code == 400 || code == 500);
    }

    @Test
    void testUpdateProfile_Exception() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.updateProfile(req, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetUserStatus_Success() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.getUserStatus(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetUserStatus_Invalid() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.getUserStatus(request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetUserStatus_UserNotFound() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getUserStatus(request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetUserStatus_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.getUserStatus(request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_Success() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userPreferencesService.getUserPreferencesByUserId(1L)).thenReturn(Optional.of(userPreferences));
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_Invalid() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_NotIdentity() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_CreateDefault() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userPreferencesService.getUserPreferencesByUserId(1L)).thenReturn(Optional.empty());
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userPreferencesService.createDefaultPreferences(1L)).thenReturn(userPreferences);
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_UserNotFound() {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userPreferencesService.getUserPreferencesByUserId(1L)).thenReturn(Optional.empty());
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetUserPreferences_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.getUserPreferences(request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetNormalUserCount() {
        when(userService.getNormalUserCount()).thenReturn(5);
        ResponseEntity<?> response = userController.getNormalUserCount(request);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5, ((ApiResponse<?>)response.getBody()).getData());
    }

    @Test
    void testUpdateUserPreferences_Success() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userPreferencesService.saveOrUpdateUserPreferencesByUserId(eq(1L), any())).thenReturn(Optional.of(userPreferences));
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserPreferences_Invalid() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserPreferences_NotIdentity() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserPreferences_UserNotFound() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserPreferences_SaveFail() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userPreferencesService.saveOrUpdateUserPreferencesByUserId(eq(1L), any())).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserPreferences_Exception() {
        UpdatePreferencesRequest req = new UpdatePreferencesRequest();
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.updateUserPreferences(req, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_Success() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(fileUploadService.uploadAvatar(any(), eq(1L))).thenReturn("new_avatar.png");
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        doNothing().when(fileUploadService).deleteFile(anyString());
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_Invalid() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_NotIdentity() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_UserNotFound() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_UpdateFail() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(fileUploadService.uploadAvatar(any(), eq(1L))).thenReturn("new_avatar.png");
        when(userService.updateProfile(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(false);
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_IllegalArgument() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(fileUploadService.uploadAvatar(any(), eq(1L))).thenThrow(new IllegalArgumentException("bad file"));
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_IOException() throws IOException {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(fileUploadService.uploadAvatar(any(), eq(1L))).thenThrow(new IOException("io fail"));
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testUploadAvatar_Exception() throws IOException {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.uploadAvatar(multipartFile, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetUserById_Success() {
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.getUserById(1L, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetUserById_Invalid() {
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.getUserById(1L, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetUserById_Identity() {
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.getUserById(1L, request);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getUserById(1L, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetUserById_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.getUserById(1L, request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers_Success() {
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        when(userService.getAllNormalUsers()).thenReturn(Arrays.asList(user));
        ResponseEntity<?> response = userController.getAllUsers(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers_Invalid() {
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.getAllUsers(request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers_Identity() {
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.getAllUsers(request);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testGetAllUsers_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.getAllUsers(request);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_Success() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        when(userService.updateUserStatus(1L, true)).thenReturn(true);
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_NoLogin() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_Invalid() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(request.getHeader("X-Valid")).thenReturn("false");
        when(request.getHeader("X-Identity")).thenReturn("false");
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_Identity() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("true");
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_Fail() {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(request.getHeader("X-Valid")).thenReturn("true");
        when(request.getHeader("X-Identity")).thenReturn("false");
        when(userService.updateUserStatus(1L, true)).thenReturn(false);
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateUserStatus_Exception() {
        when(request.getHeader(anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = userController.updateUserStatus(1L, true, request);
        assertEquals(500, response.getStatusCode().value());
    }
} 