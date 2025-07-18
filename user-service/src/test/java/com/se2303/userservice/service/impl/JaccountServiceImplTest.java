package com.se2303.userservice.service.impl;

import com.se2303.userservice.config.JaccountConfig;
import com.se2303.userservice.dto.response.JaccountTokenResponse;
import com.se2303.userservice.dto.response.JaccountUserInfo;
import com.se2303.userservice.dto.response.JaccountUserInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JaccountServiceImplTest {
    @Mock
    private JaccountConfig jaccountConfig;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private JaccountServiceImpl jaccountServiceImpl;
    private JaccountTokenResponse tokenResponse;
    private JaccountUserInfoResponse userInfoResponse;
    private JaccountUserInfo userInfo;

    @BeforeEach
    void setUp() {
        tokenResponse = new JaccountTokenResponse();
        tokenResponse.setAccessToken("token");
        tokenResponse.setExpiresIn(3600);
        tokenResponse.setTokenType("Bearer");
        userInfo = new JaccountUserInfo();
        userInfo.setId("id");
        userInfo.setAccount("acc");
        userInfo.setName("name");
        userInfoResponse = new JaccountUserInfoResponse();
        userInfoResponse.setErrno(0);
        userInfoResponse.setEntities(Collections.singletonList(userInfo));
    }

    @Test
    void testExchangeCodeForToken_Success() {
        when(jaccountConfig.getClientId()).thenReturn("cid");
        when(jaccountConfig.getClientSecret()).thenReturn("secret");
        when(jaccountConfig.getTokenUrl()).thenReturn("url");
        ResponseEntity<JaccountTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(JaccountTokenResponse.class))).thenReturn(responseEntity);
        String token = jaccountServiceImpl.exchangeCodeForToken("code", "redirect");
        assertEquals("token", token);
    }

    @Test
    void testExchangeCodeForToken_Fail_HttpError() {
        when(jaccountConfig.getClientId()).thenReturn("cid");
        when(jaccountConfig.getClientSecret()).thenReturn("secret");
        when(jaccountConfig.getTokenUrl()).thenReturn("url");
        ResponseEntity<JaccountTokenResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(), eq(JaccountTokenResponse.class))).thenReturn(responseEntity);
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.exchangeCodeForToken("code", "redirect"));
    }

    @Test
    void testExchangeCodeForToken_Fail_NullToken() {
        when(jaccountConfig.getClientId()).thenReturn("cid");
        when(jaccountConfig.getClientSecret()).thenReturn("secret");
        when(jaccountConfig.getTokenUrl()).thenReturn("url");
        ResponseEntity<JaccountTokenResponse> responseEntity = new ResponseEntity<>(new JaccountTokenResponse(), HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(JaccountTokenResponse.class))).thenReturn(responseEntity);
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.exchangeCodeForToken("code", "redirect"));
    }

    @Test
    void testExchangeCodeForToken_Exception() {
        when(jaccountConfig.getClientId()).thenReturn("cid");
        when(jaccountConfig.getClientSecret()).thenReturn("secret");
        when(jaccountConfig.getTokenUrl()).thenReturn("url");
        when(restTemplate.postForEntity(anyString(), any(), eq(JaccountTokenResponse.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.exchangeCodeForToken("code", "redirect"));
    }

    @Test
    void testGetUserInfo_Success() {
        when(jaccountConfig.getUserInfoUrl()).thenReturn("url");
        ResponseEntity<JaccountUserInfoResponse> responseEntity = new ResponseEntity<>(userInfoResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(JaccountUserInfoResponse.class))).thenReturn(responseEntity);
        JaccountUserInfo info = jaccountServiceImpl.getUserInfo("token");
        assertNotNull(info);
        assertEquals("id", info.getId());
    }

    @Test
    void testGetUserInfo_Fail_HttpError() {
        when(jaccountConfig.getUserInfoUrl()).thenReturn("url");
        ResponseEntity<JaccountUserInfoResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(JaccountUserInfoResponse.class))).thenReturn(responseEntity);
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.getUserInfo("token"));
    }

    @Test
    void testGetUserInfo_Fail_NullBody() {
        when(jaccountConfig.getUserInfoUrl()).thenReturn("url");
        ResponseEntity<JaccountUserInfoResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(JaccountUserInfoResponse.class))).thenReturn(responseEntity);
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.getUserInfo("token"));
    }

    @Test
    void testGetUserInfo_Fail_Errno() {
        when(jaccountConfig.getUserInfoUrl()).thenReturn("url");
        JaccountUserInfoResponse errorResp = new JaccountUserInfoResponse();
        errorResp.setErrno(1);
        errorResp.setEntities(Collections.emptyList());
        ResponseEntity<JaccountUserInfoResponse> responseEntity = new ResponseEntity<>(errorResp, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(JaccountUserInfoResponse.class))).thenReturn(responseEntity);
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.getUserInfo("token"));
    }

    @Test
    void testGetUserInfo_Exception() {
        when(jaccountConfig.getUserInfoUrl()).thenReturn("url");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(JaccountUserInfoResponse.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> jaccountServiceImpl.getUserInfo("token"));
    }

    @Test
    void testLogout_Success() {
        when(jaccountConfig.getLogoutUrl()).thenReturn("logoutUrl");
        when(jaccountConfig.getClientId()).thenReturn("cid");
        ResponseEntity<String> responseEntity = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);
        assertDoesNotThrow(() -> jaccountServiceImpl.logout("redirect", "state"));
    }

    @Test
    void testLogout_Exception() {
        when(jaccountConfig.getLogoutUrl()).thenReturn("logoutUrl");
        when(jaccountConfig.getClientId()).thenReturn("cid");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenThrow(new RuntimeException("fail"));
        assertDoesNotThrow(() -> jaccountServiceImpl.logout("redirect", "state"));
    }
} 