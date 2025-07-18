package com.se2303.userservice.service.impl;

import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;

import com.se2303.userservice.repository.UserPreferencesRepository;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.repository.UserRepository;

import com.se2303.userservice.service.UserPreferencesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserPreferencesServiceImpl implements UserPreferencesService {
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private AuthRepository authRepository;
    
    /**
     * 根据用户ID获取用户偏好设置
     * @param userId 用户ID
     * @return 用户偏好设置
     */
    public Optional<UserPreferences> getUserPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findById(userId);
    }
    
    /**
     * 为新用户创建默认偏好设置
     * @param userId 用户ID
     * @return 默认偏好设置
     */
    public UserPreferences createDefaultPreferences(Long userId){
        UserPreferences preferences = new UserPreferences();
        preferences.setAuth(authRepository.findById(userId).get());
        return userPreferencesRepository.save(preferences);
    }

    /**
     * 创建或更新用户偏好设置
     * @param userId 用户ID
     * @param preferences 偏好设置数据
     * @return 保存后的偏好设置
     */
    public Optional<UserPreferences> saveOrUpdateUserPreferencesByUserId(Long userId, UserPreferences preferences) {
        Optional<UserPreferences> existingOpt = userPreferencesRepository.findById(userId);
        
        if (existingOpt.isPresent()) {
            // 更新现有偏好设置
            UserPreferences existing = existingOpt.get();

            existing.updatePreferences(preferences);

            return Optional.of(userPreferencesRepository.save(existing));
        } else {
            // 创建新的偏好设置，需要先获取Auth实体
            Optional<Auth> authOpt = authRepository.findById(userId);
            if (authOpt.isPresent()) {
                preferences.setAuth(authOpt.get());
                return Optional.of(userPreferencesRepository.save(preferences));
            } else {
                throw new RuntimeException("用户不存在: " + userId);
            }
        }
    }


}
