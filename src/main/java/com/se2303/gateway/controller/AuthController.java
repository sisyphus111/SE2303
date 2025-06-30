package com.se2303.gateway.controller;

import com.se2303.gateway.entity.User;
import com.se2303.gateway.service.JAccountService;
import com.se2303.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 * 提供JAccount登录、登出、用户信息查询等API接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final JAccountService jAccountService;
    private final JwtUtil jwtUtil;
    
    public AuthController(JAccountService jAccountService, JwtUtil jwtUtil) {
        this.jAccountService = jAccountService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 获取JAccount登录URL
     */
    @GetMapping("/jaccount/url")
    public ResponseEntity<Map<String, String>> getJAccountLoginUrl() {
        String state = UUID.randomUUID().toString();
        String authUrl = jAccountService.getAuthorizationUrl(state);
        
        Map<String, String> response = new HashMap<>();
        response.put("authUrl", authUrl);
        response.put("state", state);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * JAccount登录回调处理
     */
    @GetMapping("/jaccount/callback")
    public Mono<ResponseEntity<Map<String, Object>>> handleJAccountCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {
        
        logger.info("Handling JAccount callback with code: {}, state: {}", code, state);
        
        return jAccountService.handleJAccountCallback(code)
                .map(user -> {
                    // 生成JWT token（只包含学号）
                    String accessToken = jwtUtil.generateAccessToken(
                            user.getId().toString(),
                            user.getAccount(),
                            user.getCode()  // 只包含学号
                    );
                    String refreshToken = jwtUtil.generateRefreshToken(user.getAccount());
                    
                    // 设置cookie
                    ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                            .httpOnly(true)
                            .secure(false) // 开发环境设为false，生产环境应为true
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .build();
                    
                    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofDays(7))
                            .build();
                    
                    // 构造响应（只返回学号）
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("user", Map.of(
                            "id", user.getId(),
                            "account", user.getAccount(),
                            "code", user.getCode()  // 只返回学号
                    ));
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                            .body(response);
                })
                .onErrorReturn(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("success", false, "message", "Login failed"))
                );
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // 清除cookie
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
    
    /**
     * 获取当前用户信息（只返回学号）
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @CookieValue(value = "access_token", required = false) String accessToken) {
        
        if (accessToken == null || !jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Unauthorized"));
        }
        
        try {
            String userId = jwtUtil.getUserIdFromToken(accessToken);
            String account = jwtUtil.getUsernameFromToken(accessToken);
            String code = jwtUtil.getCodeFromToken(accessToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                    "id", userId,
                    "account", account,
                    "code", code  // 只返回学号
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get user info"));
        }
    }
    
    /**
     * 刷新访问token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid refresh token"));
        }
        
        try {
            String account = jwtUtil.getUsernameFromToken(refreshToken);
            User user = jAccountService.findUserByAccount(account)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // 生成新的访问token（只包含学号）
            String newAccessToken = jwtUtil.generateAccessToken(
                    user.getId().toString(),
                    user.getAccount(),
                    user.getCode()  // 只包含学号
            );
            
            ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .build();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token refreshed");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .body(response);
        } catch (Exception e) {
            logger.error("Failed to refresh token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to refresh token"));
        }
    }
}
