package com.se2303.userservice.controller;

import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;

import com.se2303.userservice.repository.AuthRepository;

import com.se2303.userservice.service.UserService;
import com.se2303.userservice.service.UserPreferencesService;
import com.se2303.userservice.service.FileUploadService;

import com.se2303.userservice.util.JwtUtil;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.se2303.userservice.dto.request.UpdateProfileRequest;
import com.se2303.userservice.dto.request.UpdatePreferencesRequest;
import com.se2303.userservice.dto.response.UserPreferenceResponse;
import com.se2303.userservice.dto.response.UserProfileResponse;
import com.se2303.userservice.dto.response.UserStatusResponse;
import com.se2303.userservice.dto.response.UserManagementResponse;

import com.se2303.userservice.dto.response.ApiResponse;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserPreferencesService userPreferencesService;
    
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(HttpServletRequest request) {
        ApiResponse<UserProfileResponse> result = new ApiResponse<UserProfileResponse>();
        
        try {
            System.out.println("=== GET /profile 请求开始 ===");

            // 打印所有请求头信息
            System.out.println("=== 请求头信息 ===");
            System.out.println("X-User-Id: " + request.getHeader("X-User-Id"));
            System.out.println("X-Valid: " + request.getHeader("X-Valid"));
            System.out.println("X-Identity: " + request.getHeader("X-Identity"));
            System.out.println("X-Gateway-Auth: " + request.getHeader("X-Gateway-Auth"));

            System.out.println("✅ Token验证成功");
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            System.out.println("=== 解析结果 ===");
            System.out.println("UserId: " + userId);
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity); 

            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }

            // 3. 通过userId获取用户信息
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            
            User user = userOpt.get();
            
            // 4. 返回用户信息
            result.setSuccess(true);
            result.setMessage("用户信息获取成功");
            result.setData(UserProfileResponse.convertToUserProfileResponse(user));
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<UserStatusResponse>> getUserStatus(HttpServletRequest request) {
        ApiResponse<UserStatusResponse> result = new ApiResponse<UserStatusResponse>();
        
        try {
            System.out.println("=== GET /status 请求开始 ===");
    
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            System.out.println("UserId: " + userId);
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity);

            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }

            // 3. 通过virtualId获取用户信息
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            
            User user = userOpt.get();
            
            // 4. 返回用户状态信息
            result.setSuccess(true);
            result.setMessage("用户状态查询成功");
            result.setData(UserStatusResponse.convertToUserStatusResponse(user));
            System.out.println("✅ 用户状态查询成功: identity=true, valid=" + user.getAuth().getValid());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.out.println("❌ 获取用户状态时发生错误: " + e.getMessage());
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@RequestBody UpdateProfileRequest updateRequest, HttpServletRequest request) {
        ApiResponse<UserProfileResponse> result = new ApiResponse<UserProfileResponse>();
        
        try {
            // 添加调试日志
            System.out.println("接收到的updateRequest: ");
            System.out.println("username: " + updateRequest.getUsername());
            System.out.println("nickname: " + updateRequest.getNickname());
            System.out.println("profileVisibility: " + updateRequest.getProfileVisibility());
            System.out.println("tags: " + updateRequest.getTags());
            
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            System.out.println("UserId: " + userId);
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity);
            
            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (!identity) {
                result.setSuccess(true);
                result.setMessage("用户是管理员");
                return ResponseEntity.ok(result);
            }
            
            // 3. 通过userId获取用户信息
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            
            User user = userOpt.get();
            
            // 4. 参数校验
            if (updateRequest.getUsername() != null && updateRequest.getUsername().trim().isEmpty()) {
                result.setSuccess(false);
                result.setMessage("用户名不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 5. 处理生日字段转换
            LocalDate birthday = null;
            if (updateRequest.getBirthday() != null && !updateRequest.getBirthday().trim().isEmpty()) {
                try {
                    birthday = LocalDate.parse(updateRequest.getBirthday());
                } catch (DateTimeParseException e) {
                    result.setSuccess(false);
                    result.setMessage("生日格式不正确，应为YYYY-MM-DD格式");
                    return ResponseEntity.badRequest().body(result);
                }
            }
            
            // 6. 调用service更新用户资料
            boolean success = userService.updateProfile(
                user.getId(), 
                updateRequest.getUsername(),
                updateRequest.getNickname(), 
                updateRequest.getAvatar(),
                updateRequest.getGender(),
                birthday,
                updateRequest.getLocation(),
                updateRequest.getBio(),
                updateRequest.getTags(),
                updateRequest.getProfileVisibility()
            );
            
            if (success) {
                result.setSuccess(true);
                result.setMessage("用户资料更新成功");
                // 返回更新后的用户信息
                Optional<User> updatedUserOpt = userService.getUserById(user.getId());
                if (updatedUserOpt.isPresent()) {
                    result.setData(UserProfileResponse.convertToUserProfileResponse(updatedUserOpt.get()));
                }
                return ResponseEntity.ok(result);
            } else {
                result.setSuccess(false);
                result.setMessage("更新失败");
                return ResponseEntity.status(500).body(result);
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<UserPreferenceResponse>> getUserPreferences(HttpServletRequest request) {
        ApiResponse<UserPreferenceResponse> result = new ApiResponse<UserPreferenceResponse>();
        
        try {
            System.out.println("=== GET /preferences 请求开始 ===");
            
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            System.out.println("UserId from token: " + userId);
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity);
            
            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (!identity) {
                result.setSuccess(true);
                result.setMessage("用户是管理员");
                return ResponseEntity.ok(result);
            }

            // 3. 获取用户偏好设置
            Optional<UserPreferences> preferencesOpt = userPreferencesService.getUserPreferencesByUserId(userId);
            
            if (preferencesOpt.isPresent()) {
                // 返回现有偏好设置
                result.setSuccess(true);
                result.setMessage("用户偏好设置查询成功");
                result.setData(UserPreferenceResponse.convertToUserPreferenceResponse(preferencesOpt.get()));
                System.out.println("✅ 用户偏好设置查询成功");
                return ResponseEntity.ok(result);
            } else {
                // 如果没有偏好设置，创建默认设置
                Optional<User> userOpt = userService.getUserById(userId);
                if (userOpt.isPresent()) {
                    UserPreferences defaultPrefs = userPreferencesService.createDefaultPreferences(userOpt.get().getId());
                    result.setSuccess(true);
                    result.setMessage("用户偏好设置查询成功");
                    result.setData(UserPreferenceResponse.convertToUserPreferenceResponse(defaultPrefs));
                    System.out.println("✅ 创建默认偏好设置成功");
                    return ResponseEntity.ok(result);
                } else {
                    result.setSuccess(false);
                    result.setMessage("用户不存在");
                    return ResponseEntity.status(404).body(result);
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ 获取用户偏好设置时发生错误: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    

    @PostMapping("/preferences")
    public ResponseEntity<?> updateUserPreferences(@RequestBody UpdatePreferencesRequest updateRequest, HttpServletRequest request) {
        ApiResponse<UserPreferenceResponse> result = new ApiResponse<UserPreferenceResponse>();
        
        try {
            System.out.println("=== POST /preferences 请求开始 ===");
            System.out.println("收到的请求数据: " + updateRequest);
            System.out.println("pushNotifications: " + updateRequest.getPushNotifications());
            System.out.println("emailNotifications: " + updateRequest.getEmailNotifications());
            System.out.println("commentNotifications: " + updateRequest.getCommentNotifications());
            System.out.println("likeNotifications: " + updateRequest.getLikeNotifications());
            System.out.println("systemNotifications: " + updateRequest.getSystemNotifications());
            
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            System.out.println("UserId: " + userId);
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity);
            
            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (!identity) {
                result.setSuccess(true);
                result.setMessage("用户是管理员");
                return ResponseEntity.ok(result);
            }

            // 3. 通过userId获取用户信息
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            // 4. 创建UserPreferences对象，只设置传递的字段
            UserPreferences preferences = new UserPreferences();
            preferences.setAuth(userOpt.get().getAuth());
            
            // 设置通知偏好 - 通知方式
            if (updateRequest.getPushNotifications() != null) {
                System.out.println("设置 pushNotifications = " + updateRequest.getPushNotifications());
                preferences.setPushNotifications(updateRequest.getPushNotifications());
            }
            if (updateRequest.getEmailNotifications() != null) {
                System.out.println("设置 emailNotifications = " + updateRequest.getEmailNotifications());
                preferences.setEmailNotifications(updateRequest.getEmailNotifications());
            }
            
            // 设置通知偏好 - 通知类型
            if (updateRequest.getCommentNotifications() != null) {
                System.out.println("设置 commentNotifications = " + updateRequest.getCommentNotifications());
                preferences.setCommentNotifications(updateRequest.getCommentNotifications());
            }
            if (updateRequest.getLikeNotifications() != null) {
                System.out.println("设置 likeNotifications = " + updateRequest.getLikeNotifications());
                preferences.setLikeNotifications(updateRequest.getLikeNotifications());
            }
            if (updateRequest.getSystemNotifications() != null) {
                System.out.println("设置 systemNotifications = " + updateRequest.getSystemNotifications());
                preferences.setSystemNotifications(updateRequest.getSystemNotifications());
            }
            
            // 设置个人偏好 - 媒体设置
            if (updateRequest.getSoundEnabled() != null) {
                System.out.println("设置 soundEnabled = " + updateRequest.getSoundEnabled());
                preferences.setSoundEnabled(updateRequest.getSoundEnabled());
            }
            
            // 设置个人偏好 - 社区交互
            if (updateRequest.getShowPostStats() != null) {
                System.out.println("设置 showPostStats = " + updateRequest.getShowPostStats());
                preferences.setShowPostStats(updateRequest.getShowPostStats());
            }
            if (updateRequest.getEnablePostRecommendation() != null) {
                System.out.println("设置 enablePostRecommendation = " + updateRequest.getEnablePostRecommendation());
                preferences.setEnablePostRecommendation(updateRequest.getEnablePostRecommendation());
            }
            if (updateRequest.getBlockSensitiveContent() != null) {
                System.out.println("设置 blockSensitiveContent = " + updateRequest.getBlockSensitiveContent());
                preferences.setBlockSensitiveContent(updateRequest.getBlockSensitiveContent());
            }
            
            // 设置心理健康偏好
            if (updateRequest.getCrisisInterventionEnabled() != null) {
                System.out.println("设置 crisisInterventionEnabled = " + updateRequest.getCrisisInterventionEnabled());
                preferences.setCrisisInterventionEnabled(updateRequest.getCrisisInterventionEnabled());
            }
            if (updateRequest.getMoodTrackingEnabled() != null) {
                System.out.println("设置 moodTrackingEnabled = " + updateRequest.getMoodTrackingEnabled());
                preferences.setMoodTrackingEnabled(updateRequest.getMoodTrackingEnabled());
            }
            if (updateRequest.getAnonymousPosting() != null) {
                System.out.println("设置 anonymousPosting = " + updateRequest.getAnonymousPosting());
                preferences.setAnonymousPosting(updateRequest.getAnonymousPosting());
            }
            if (updateRequest.getMentalHealthDataSharing() != null) {
                System.out.println("设置 mentalHealthDataSharing = " + updateRequest.getMentalHealthDataSharing());
                preferences.setMentalHealthDataSharing(updateRequest.getMentalHealthDataSharing());
            }
            
            // 4. 保存或更新偏好设置
            Optional<UserPreferences> savedOpt = userPreferencesService.saveOrUpdateUserPreferencesByUserId(userId, preferences);
            
            if (savedOpt.isPresent()) {
                result.setSuccess(true);
                result.setMessage("用户偏好设置更新成功");
                result.setData(UserPreferenceResponse.convertToUserPreferenceResponse(savedOpt.get()));
                System.out.println("✅ 用户偏好设置更新成功");
                return ResponseEntity.ok(result);
            } else {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 更新用户偏好设置时发生错误: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 上传用户头像接口
     * 
     * 处理流程：
     * 1. 验证用户身份（JWT Token）
     * 2. 调用文件上传服务将头像上传到腾讯云COS
     * 3. 更新用户数据库中的头像URL字段
     * 4. 删除旧头像文件（如果存在）
     * 5. 返回新的头像URL
     * 
     * @param file 上传的头像文件
     * @param request HTTP请求对象（用于获取JWT Token）
     * @return 包含头像URL的响应
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file, 
            HttpServletRequest request) {
        
        ApiResponse<Map<String, String>> result = new ApiResponse<>();
        
        try {
            System.out.println("=== POST /avatar 头像上传请求开始 ===");
            System.out.println("文件名: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("文件大小: " + (file != null ? file.getSize() : 0) + " bytes");
            
            Long userId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            
            System.out.println("UserId: " + userId);
            System.out.println("Valid: " + valid);
            System.out.println("Identity: " + identity);
            
            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (!identity) {
                result.setSuccess(false);
                result.setMessage("管理员不允许上传头像");
                return ResponseEntity.status(403).body(result);
            }

            // 3. 验证用户是否存在
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                return ResponseEntity.status(404).body(result);
            }
            
            User user = userOpt.get();
            String oldAvatarUrl = user.getAvatar();

            // 4. 上传文件到腾讯云COS
            String newAvatarUrl = fileUploadService.uploadAvatar(file, userId);
            System.out.println("✅ 文件上传成功，新头像URL: " + newAvatarUrl);

            // 5. 更新用户的头像URL
            user.setAvatar(newAvatarUrl);
            boolean updateSuccess = userService.updateProfile(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                newAvatarUrl,  // 新的头像URL
                user.getGender(),
                user.getBirthday(),
                user.getLocation(),
                user.getBio(),
                user.getTags(),
                user.getProfileVisibility()
            );
            
            if (!updateSuccess) {
                System.err.println("❌ 更新用户头像URL失败");
                result.setSuccess(false);
                result.setMessage("更新用户头像失败");
                return ResponseEntity.status(500).body(result);
            }

            // 6. 删除旧头像文件（如果存在且不是默认头像）
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty() && 
                !oldAvatarUrl.equals("default_avatar.png") && 
                !oldAvatarUrl.startsWith("data:")) {
                try {
                    fileUploadService.deleteFile(oldAvatarUrl);
                } catch (Exception e) {
                    System.err.println("删除旧头像时出错（不影响上传结果）: " + e.getMessage());
                }
            }

            // 7. 返回成功响应
            Map<String, String> responseData = new HashMap<>();
            responseData.put("avatarUrl", newAvatarUrl);
            
            result.setSuccess(true);
            result.setMessage("头像上传成功");
            result.setData(responseData);
            
            System.out.println("✅ 头像上传流程完成");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // 文件验证错误（文件类型、大小等）
            System.err.println("❌ 文件验证失败: " + e.getMessage());
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(result);
            
        } catch (IOException e) {
            // 文件上传错误
            System.err.println("❌ 文件上传失败: " + e.getMessage());
            result.setSuccess(false);
            result.setMessage("文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
            
        } catch (Exception e) {
            // 其他未预期的错误
            System.err.println("❌ 头像上传过程中发生未知错误: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 管理员功能：获取所有普通用户列表
     */
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<UserManagementResponse>>> getAllUsers(HttpServletRequest request) {
        ApiResponse<List<UserManagementResponse>> result = new ApiResponse<>();
        
        try {
            System.out.println("=== GET /admin/users 获取普通用户列表请求开始 ===");
        
            boolean valid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            
            if (!valid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (identity) {
                result.setSuccess(false);
                result.setMessage("权限不足，仅管理员可访问");
                return ResponseEntity.status(403).body(result);
            }

            // 3. 获取所有普通用户列表（isUser=true）
            List<User> users = userService.getAllNormalUsers();
            List<UserManagementResponse> userResponses = users.stream()
                    .map(UserManagementResponse::convertToUserManagementResponse)
                    .collect(java.util.stream.Collectors.toList());
            
            result.setSuccess(true);
            result.setMessage("普通用户列表获取成功");
            result.setData(userResponses);
            
            System.out.println("✅ 普通用户列表获取成功，共 " + users.size() + " 个用户");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.out.println("❌ 获取普通用户列表时发生错误: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 管理员功能：启用或禁用用户
     */
    @PutMapping("/admin/users/user/status")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable Long userId, 
            @RequestParam boolean valid,
            HttpServletRequest request) {
        ApiResponse<String> result = new ApiResponse<>();
        
        try {
            System.out.println("=== PUT /admin/users/" + userId + "/status 更新用户状态请求开始 ===");
            
            // 从请求头中获取用户认证信息（由网关传递）
            Long adminUserId = JwtUtil.getUserId(request.getHeader("X-User-Id"));
            boolean adminValid = JwtUtil.getValid(request.getHeader("X-Valid"));
            boolean identity = JwtUtil.getIdentity(request.getHeader("X-Identity"));
            
            System.out.println("Admin UserId: " + adminUserId);
            System.out.println("Admin Valid: " + adminValid);
            System.out.println("Admin Identity: " + identity);
            
            if (adminUserId == null) {
                result.setSuccess(false);
                result.setMessage("未登录或用户ID无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (!adminValid) {
                result.setSuccess(false);
                result.setMessage("用户状态无效");
                return ResponseEntity.status(401).body(result);
            }
            
            if (identity) {
                result.setSuccess(false);
                result.setMessage("权限不足，仅管理员可访问");
                return ResponseEntity.status(403).body(result);
            }

            // 3. 更新用户状态
            boolean success = userService.updateUserStatus(userId, valid);
            
            if (success) {
                result.setSuccess(true);
                result.setMessage("用户状态更新成功");
                result.setData(valid ? "用户已启用" : "用户已禁用");
                
                System.out.println("✅ 用户状态更新成功: userId=" + userId + ", valid=" + valid);
                return ResponseEntity.ok(result);
            } else {
                result.setSuccess(false);
                result.setMessage("用户不存在或更新失败");
                return ResponseEntity.status(404).body(result);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 更新用户状态时发生错误: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
}
