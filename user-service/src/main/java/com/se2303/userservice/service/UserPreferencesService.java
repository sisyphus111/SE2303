package com.se2303.userservice.service;

import com.se2303.userservice.entity.UserPreferences;

import java.util.Optional;

public interface UserPreferencesService {
    
    /**
     * 根据用户ID获取用户偏好设置
     * @param userId 用户ID
     * @return 用户偏好设置
     */
    public Optional<UserPreferences> getUserPreferencesByUserId(Long userId);
    
    /**
     * 为新用户创建默认偏好设置
     * @param userId 用户ID
     * @return 默认偏好设置
     */
    public UserPreferences createDefaultPreferences(Long userId);
    
    /**
     * 创建或更新用户偏好设置
     * @param userId 用户ID
     * @param preferences 偏好设置数据
     * @return 保存后的偏好设置
     */
    public Optional<UserPreferences> saveOrUpdateUserPreferencesByUserId(Long userId, UserPreferences preferences);
}
