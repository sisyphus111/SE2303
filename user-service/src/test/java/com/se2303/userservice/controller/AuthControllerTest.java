package com.se2303.userservice.controller;

import com.se2303.userservice.dto.request.JaccountCallbackRequest;
import com.se2303.userservice.dto.response.ApiResponse;
import com.se2303.userservice.dto.response.JaccountUserInfo;
import com.se2303.userservice.dto.response.UserProfileResponse;
import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.service.JaccountService;
import com.se2303.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private JaccountService jaccountService;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private AuthController authController;
    private Auth auth;
    private User user;

    @BeforeEach
    void setUp() {
        auth = new Auth();
        auth.setId(1L);
        auth.setValid(true);
        auth.setIsUser(true);
        user = new User();
        user.setId(1L);
        // 全局 lenient mock，避免不必要的 stubbing 报错
        lenient().when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(authRepository.findBySjtuId(anyString())).thenReturn(Optional.of(auth));
    }

    @Test
    void testLogout_Success() {
        ResponseEntity<ApiResponse<Void>> resp = authController.logout(response, "redirect", "state");
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().getSuccess());
    }

    @Test
    void testLogout_Exception() {
        doThrow(new RuntimeException("fail")).when(jaccountService).logout(anyString(), anyString());
        ResponseEntity<ApiResponse<Void>> resp = authController.logout(response, "redirect", "state");
        assertEquals(200, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_Success() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("code");
        req.setRedirectUri("uri");
        when(jaccountService.exchangeCodeForToken(anyString(), anyString())).thenReturn("token");
        JaccountUserInfo userInfo = new JaccountUserInfo();
        userInfo.setAccount("acc");
        userInfo.setCode("code");
        userInfo.setName("name");
        userInfo.setMobile("123");
        when(jaccountService.getUserInfo(anyString())).thenReturn(userInfo);
        when(authRepository.findBySjtuId(anyString())).thenReturn(Optional.of(auth));
        lenient().when(userService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(user);
        lenient().when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_CodeEmpty() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("");
        req.setRedirectUri("uri");
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(400, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_RedirectUriEmpty() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("code");
        req.setRedirectUri("");
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(400, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_UserNotFound() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("code");
        req.setRedirectUri("uri");
        when(jaccountService.exchangeCodeForToken(anyString(), anyString())).thenReturn("token");
        JaccountUserInfo userInfo = new JaccountUserInfo();
        userInfo.setAccount("acc");
        userInfo.setCode("code");
        userInfo.setName("name");
        userInfo.setMobile("123");
        when(jaccountService.getUserInfo(anyString())).thenReturn(userInfo);
        when(authRepository.findBySjtuId(anyString())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(400, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_AuthNotPresent() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("code");
        req.setRedirectUri("uri");
        when(jaccountService.exchangeCodeForToken(anyString(), anyString())).thenReturn("token");
        JaccountUserInfo userInfo = new JaccountUserInfo();
        userInfo.setAccount("acc");
        userInfo.setCode("code");
        userInfo.setName("name");
        userInfo.setMobile("123");
        when(jaccountService.getUserInfo(anyString())).thenReturn(userInfo);
        when(authRepository.findBySjtuId(anyString())).thenReturn(Optional.of(auth));
        auth.setValid(false);
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(400, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }

    @Test
    void testHandleJaccountCallback_Exception() {
        JaccountCallbackRequest req = new JaccountCallbackRequest();
        req.setCode("code");
        req.setRedirectUri("uri");
        when(jaccountService.exchangeCodeForToken(anyString(), anyString())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<ApiResponse<UserProfileResponse>> resp = authController.handleJaccountCallback(req, response);
        assertEquals(200, resp.getStatusCode().value());
        assertFalse(resp.getBody().getSuccess());
    }
} 