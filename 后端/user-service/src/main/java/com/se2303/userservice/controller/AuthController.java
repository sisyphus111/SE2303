package com.se2303.userservice.controller;

import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.service.UserService;
import com.se2303.userservice.service.JaccountService;

import com.se2303.userservice.dto.request.JaccountCallbackRequest;
import com.se2303.userservice.dto.response.ApiResponse;
import com.se2303.userservice.dto.response.JaccountUserInfo;
import com.se2303.userservice.dto.response.UserProfileResponse;

import com.se2303.userservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JaccountService jaccountService;


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response, 
                                                   @RequestParam(required = false) String postLogoutRedirectUri,
                                                   @RequestParam(required = false) String state) {
        ApiResponse<Void> result = new ApiResponse<Void>();
        
        try {
            // 1. 向jAccount发送登出请求
            if (postLogoutRedirectUri != null && !postLogoutRedirectUri.trim().isEmpty()) {
                logger.info("开始向jAccount发送登出请求");
                jaccountService.logout(postLogoutRedirectUri, state);
            } else {
                logger.info("未提供post_logout_redirect_uri，跳过jAccount登出请求");
            }
            
            // 2. 清除httpOnly cookie (手动设置Set-Cookie header以支持SameSite=None)
            String accessTokenClearValue = String.format(
                "accessToken=; Path=/; Domain=%s; HttpOnly; %sMaxAge=0; SameSite=None",
                cookieDomain,
                cookieSecure ? "Secure; " : ""
            );
            response.addHeader("Set-Cookie", accessTokenClearValue);
            
            String refreshTokenClearValue = String.format(
                "refreshToken=; Path=/; Domain=%s; HttpOnly; %sMaxAge=0; SameSite=None",
                cookieDomain,
                cookieSecure ? "Secure; " : ""
            );
            response.addHeader("Set-Cookie", refreshTokenClearValue);
            
            logger.info("🍪 Cookie清除完成 - Secure: {}, SameSite: None", cookieSecure);
            
            result.setSuccess(true);
            result.setMessage("登出成功");
            logger.info("用户登出成功");
            
        } catch (Exception e) {
            logger.error("登出过程中发生异常", e);
            result.setSuccess(false);
            result.setMessage("登出失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }


    @PostMapping("/jaccount/callback")
    public ResponseEntity<ApiResponse<UserProfileResponse>> handleJaccountCallback(
            @RequestBody JaccountCallbackRequest request, 
            HttpServletResponse response) {
        
        logger.info("收到Jaccount回调请求: {}", request);
        
        ApiResponse<UserProfileResponse> result = new ApiResponse<>();
        
        try {
            // 1. 参数校验
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                result.setSuccess(false);
                result.setMessage("授权码不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            if (request.getRedirectUri() == null || request.getRedirectUri().trim().isEmpty()) {
                result.setSuccess(false);
                result.setMessage("回调地址不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 2. 用code换取access_token
            logger.info("开始用授权码换取access_token");
            String accessToken = jaccountService.exchangeCodeForToken(
                request.getCode(), 
                request.getRedirectUri()
            );
            
            // 3. 用access_token获取用户信息
            logger.info("开始获取用户信息");

            JaccountUserInfo userInfo = jaccountService.getUserInfo(accessToken);
            String jaccount = userInfo.getAccount();
            String sjtuId = userInfo.getCode(); // 使用code字段作为sjtuId（学工号）
            String name = userInfo.getName();
            String phone = userInfo.getMobile();

            // 4. 若该用户不存在，则创建
            if(authRepository.findBySjtuId(sjtuId).isEmpty()){
                logger.info("开始创建或更新用户");
                User user = userService.createUser(sjtuId, name, jaccount, phone);
            }

            // 5. 生成JWT token
            logger.info("开始生成JWT token");
            Optional<Auth> authOpt = authRepository.findBySjtuId(sjtuId);
            if (!authOpt.isPresent()) {
                logger.info("发生错误，没有成功创建用户");
                result.setSuccess(false);
                result.setMessage("发生错误，没有成功创建用户");
                return ResponseEntity.badRequest().body(result);
            }
            
            Auth auth = authOpt.get();
            Long userId = auth.getId(); // 获取本地用户ID
            
            boolean valid = auth.getValid(); // 获取用户状态
            logger.info("用户状态检查 - userId: {}, valid: {}", userId, valid);
            
            if(valid == false){
                logger.info("用户被禁用");
                result.setSuccess(false);
                result.setMessage("用户被禁用");
                return ResponseEntity.badRequest().body(result);
            }

            boolean identity = auth.getIsUser(); // 获取用户身份
            logger.info("用户身份信息 - userId: {}, valid: {}, identity: {}", userId, valid, identity);
            
            String jwtAccessToken = JwtUtil.generateToken(userId, valid, identity);
            String jwtRefreshToken = JwtUtil.generateRefreshToken(userId, valid, identity);
            
            logger.info("JWT Token生成完成 - userId: {}, valid: {}, identity: {}", userId, valid, identity);
            
            // 调试：验证生成的token内容
            logger.info("=== 验证生成的JWT Token内容 ===");
            JwtUtil.printTokenClaims(jwtAccessToken);
            
            // 6. 设置httpOnly Cookie (手动设置Set-Cookie header以支持SameSite=None)
            String accessTokenCookieValue = String.format(
                "accessToken=%s; Path=/; Domain=%s; HttpOnly; %sMaxAge=%d; SameSite=None",
                jwtAccessToken,
                cookieDomain,
                cookieSecure ? "Secure; " : "",
                24 * 60 * 60 // 1天
            );
            response.addHeader("Set-Cookie", accessTokenCookieValue);
            
            String refreshTokenCookieValue = String.format(
                "refreshToken=%s; Path=/; Domain=%s; HttpOnly; %sMaxAge=%d; SameSite=None",
                jwtRefreshToken,
                cookieDomain,
                cookieSecure ? "Secure; " : "",
                7 * 24 * 60 * 60 // 7天
            );
            response.addHeader("Set-Cookie", refreshTokenCookieValue);
            
            logger.info("🍪 Cookie设置完成 - Secure: {}, SameSite: None", cookieSecure);
            
            // 7. 返回成功响应
            result.setSuccess(true);
            result.setMessage("登录成功");

            Optional<User> userOpt = userService.getUserById(auth.getId());
            if (userOpt.isPresent()) {
                result.setData(UserProfileResponse.convertToUserProfileResponse(userOpt.get()));
            } else {
                logger.error("无法找到用户信息，authId={}", auth.getId());
                result.setSuccess(false);
                result.setMessage("用户信息获取失败");
                return ResponseEntity.badRequest().body(result);
            }
            
            logger.info("Jaccount登录成功: SJTUId={}, name={}, jaccount={}", sjtuId, name, jaccount);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Jaccount登录失败", e);
            
            result.setSuccess(false);
            result.setMessage("登录失败: " + e.getMessage());
            
            return ResponseEntity.ok(result);
        }
    }    
}
