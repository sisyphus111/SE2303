import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getUserStatus, getUserProfile, apiUpdateUserProfile, getUserPreferences, apiUpdateUserPreferences } from '../service/userService';

// Context
const UserContext = createContext();

// 检查是否为登录/注册页面
const isAuthPage = () => {
  const pathname = window.location.pathname;
  return pathname === '/login' || pathname === '/register';
};

// Provider Component
export const UserProvider = ({ children }) => {

  // ===== 用户个人资料相关状态 =====
  const [userProfile, setUserProfile] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState(null);

  // ===== 用户状态相关状态 =====
  const [identity, setIdentity] = useState(null);
  const [valid, setValid] = useState(null);
  const [statusLoading, setStatusLoading] = useState(false);
  const [statusError, setStatusError] = useState(null);

  // ===== 用户偏好设置相关状态 =====
  const [preferences, setPreferences] = useState({}); // 空对象，等待从后端加载
  const [preferencesLoading, setPreferencesLoading] = useState(false);
  const [preferencesError, setPreferencesError] = useState(null);

  // ===== 全局加载和错误状态 =====
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // ===== 计算属性 =====
  const isAuthenticated = !!(userProfile && identity !== null && valid !== null);

  // ===== 初始化用户信息 =====
  useEffect(() => {
    const initializeUser = async () => {
      try {
        setLoading(true);
        
        // 并行获取用户数据
        const [userProfileData, userStatusData, userPreferencesData] = await Promise.allSettled([
          getUserProfile(),
          getUserStatus(),
          getUserPreferences()
        ]);

        // 处理用户个人资料
        if (userProfileData.status === 'fulfilled' && userProfileData.value) {
          setUserProfile(userProfileData.value);
          console.log('🔍 UserContext: 原始后端返回的用户资料:', userProfileData.value);
          setProfileError(null);
        } else if (userProfileData.status === 'rejected') {
          console.error('获取用户个人资料失败:', userProfileData.reason);
          // 如果是401错误，不设置为error状态，让ErrorHandler处理重定向
          if (userProfileData.reason?.errorCode !== 401) {
            setProfileError('获取用户个人资料失败');
          }
          setUserProfile(null);
        }

        // 处理用户状态
        if (userStatusData.status === 'fulfilled' && userStatusData.value) {
          setIdentity(userStatusData.value.identity);
          setValid(userStatusData.value.valid);
          console.log('🔍 UserContext: 原始后端返回的用户状态:', userStatusData.value);
          setStatusError(null);
        } else if (userStatusData.status === 'rejected') {
          console.error('获取用户状态失败:', userStatusData.reason);
          // 如果是401错误，不设置为error状态，让ErrorHandler处理重定向
          if (userStatusData.reason?.errorCode !== 401) {
            setStatusError('获取用户状态失败');
          }
          setIdentity(null);
          setValid(null);
        }

        // 处理用户偏好设置
        if (userPreferencesData.status === 'fulfilled' && userPreferencesData.value) {
          console.log('🔍 UserContext: 原始后端返回的偏好数据:', userPreferencesData.value);
          setPreferences(userPreferencesData.value);
          setPreferencesError(null);
          console.log('✅ 用户偏好设置加载成功:', userPreferencesData.value);
        } else if (userPreferencesData.status === 'rejected') {
          console.error('获取用户偏好设置失败:', userPreferencesData.reason);
          if (userPreferencesData.reason?.errorCode !== 401) {
            setPreferencesError('获取用户偏好设置失败');
          }
          // 设置默认偏好
          setPreferences({
            pushNotifications: true,
            emailNotifications: true,
            commentNotifications: true,
            likeNotifications: false,
            systemNotifications: true,
            soundEnabled: true,
            showPostStats: true,
            enablePostRecommendation: true,
            blockSensitiveContent: true,
            crisisInterventionEnabled: true,
            moodTrackingEnabled: true,
            anonymousPosting: false,
            mentalHealthDataSharing: false
          });
        }

        setError(null);
        setLoading(false);
      } catch (error) {
        console.error('初始化用户信息时出错:', error);
        const isAuth401Error = error.status === 401 || 
                               error.message?.includes('401') || 
                               error.message?.includes('未授权');
        
        if (!isAuth401Error && !isAuthPage()) {
          setError('获取用户信息失败');
        } else {
          console.log('📝 401错误或登录页面，由ErrorHandler统一处理');
        }
        
        setLoading(false);
      }
    };

    initializeUser();
  }, []);

  // ===== 用户个人资料相关方法 =====
  const updateUserProfile = useCallback(async (updates) => {
    try {
      setProfileLoading(true);
      setProfileError(null);
      
      const updatedProfile = await apiUpdateUserProfile(updates);
      // updateUserProfile返回的是用户对象，直接使用
      setUserProfile(prev => ({ ...prev, ...updatedProfile }));
      
      console.log('✅ 用户资料更新成功:', updatedProfile);
      return { success: true, data: updatedProfile };
    } catch (error) {
      console.error('更新用户个人资料失败:', error);
      setProfileError('更新用户个人资料失败');
      return { success: false, error: error.message };
    } finally {
      setProfileLoading(false);
    }
  }, []);

  

  const getUserAvatar = useCallback(() => {
    if (userProfile?.avatar) {
      return userProfile.avatar;
    }
    
    // 默认头像 - 基于用户名生成
    const username = userProfile?.username || userProfile?.email || 'User';
    const avatarChar = username.charAt(0).toUpperCase();
    return avatarChar;
  }, [userProfile]);

  const getUserDisplayName = useCallback(() => {
    return userProfile?.nickname || userProfile?.username || userProfile?.email || '用户';
  }, [userProfile]);


  // ===== 用户偏好设置相关方法 =====
  const updateUserPreferences = useCallback(async (newPreferences, onlyUpdateSpecifiedFields = true) => {
    try {
      console.log('🔄 UserContext.updateUserPrefs被调用，参数:', newPreferences);
      console.log('🔄 onlyUpdateSpecifiedFields:', onlyUpdateSpecifiedFields);
      setPreferencesLoading(true);
      setPreferencesError(null);
      
      console.log('🚀 调用updateUserPreferences service...');
      const updatedPreferences = await apiUpdateUserPreferences(newPreferences);
      console.log('📥 updateUserPreferences service返回:', updatedPreferences);
      
      if (onlyUpdateSpecifiedFields) {
        // 只更新发送的字段，避免覆盖其他字段
        const fieldsToUpdate = {};
        Object.keys(newPreferences).forEach(key => {
          if (updatedPreferences.hasOwnProperty(key)) {
            fieldsToUpdate[key] = updatedPreferences[key];
          }
        });
        console.log('🔄 UserContext: 只更新指定字段:', fieldsToUpdate);
        setPreferences(prev => ({ ...prev, ...fieldsToUpdate }));
        
        const result = { success: true, data: updatedPreferences };
        console.log('✅ UserContext.updateUserPrefs返回结果:', result);
        return result;
      } else {
        // 使用完整数据更新（向后兼容）
        setPreferences(prev => ({ ...prev, ...updatedPreferences }));
        
        const result = { success: true, data: updatedPreferences };
        console.log('✅ UserContext.updateUserPrefs返回结果:', result);
        return result;
      }
    } catch (error) {
      console.error('❌ UserContext.updateUserPrefs失败:', error);
      setPreferencesError('更新偏好设置失败');
      return { success: false, error: error.message };
    } finally {
      setPreferencesLoading(false);
    }
  }, []);

  // ===== 登录成功处理方法 =====
  const loginSuccess = useCallback(async (loginResult) => {
    try {
      console.log('🎯 登录成功，开始获取完整用户信息...', loginResult);
      setLoading(true);
      setError(null);
      
      // 从登录结果中获取基础用户信息
      if (loginResult && loginResult.user) {
        setUserProfile(loginResult.user);
        console.log('✅ 设置基础用户信息:', loginResult.user);
      }
      
      // 设置认证状态
      setIdentity(true);
      setValid(true);
      
      // 并行获取完整的用户数据
      const [userProfileData, userPreferencesData] = await Promise.allSettled([
        getUserProfile(),
        getUserPreferences()
      ]);

      // 处理用户个人资料
      if (userProfileData.status === 'fulfilled' && userProfileData.value) {
        setUserProfile(userProfileData.value);
        setProfileError(null);
        console.log('✅ 完整用户资料获取成功:', userProfileData.value);
      }

      // 处理用户偏好设置
      if (userPreferencesData.status === 'fulfilled' && userPreferencesData.value) {
        setPreferences(userPreferencesData.value);
        setPreferencesError(null);
        console.log('✅ 用户偏好设置获取成功:', userPreferencesData.value);
      } else {
        console.warn('获取用户偏好设置失败，使用默认值');
        // 设置默认偏好
        setPreferences({
          pushNotifications: true,
          emailNotifications: true,
          commentNotifications: true,
          likeNotifications: false,
          systemNotifications: true,
          soundEnabled: true,
          showPostStats: true,
          enablePostRecommendation: true,
          blockSensitiveContent: true,
          crisisInterventionEnabled: true,
          moodTrackingEnabled: true,
          anonymousPosting: false,
          mentalHealthDataSharing: false
        });
      }

      console.log('🎉 登录后用户信息设置完成');
      return { success: true };
      
    } catch (error) {
      console.error('❌ 登录后获取用户信息失败:', error);
      setError('登录成功，但获取用户信息时出现问题');
      return { success: false, error: error.message };
    } finally {
      setLoading(false);
    }
  }, []);

  // ===== 全局用户管理方法 =====
  const clearUser = useCallback(() => {
    setUserProfile(null);
    setIdentity(null);
    setValid(null);
    setPreferences({});
    setError(null);
    setProfileError(null);
    setStatusError(null);
    setPreferencesError(null);
    setLoading(false);
    console.log('🧹 用户数据已清除');
  }, []);

  const refreshUserInfo = useCallback(async () => {
    try {
      console.log('🔄 刷新用户信息...');
      setLoading(true);
      
      const [userProfileData, userStatusData, userPreferencesData] = await Promise.allSettled([
        getUserProfile(),
        getUserStatus(),
        getUserPreferences(),
      ]);

      // 处理用户个人资料
      if (userProfileData.status === 'fulfilled' && userProfileData.value) {
        setUserProfile(userProfileData.value);
        setProfileError(null);
      }

      // 处理用户状态
      if (userStatusData.status === 'fulfilled' && userStatusData.value) {
        setIdentity(userStatusData.value.identity);
        setValid(userStatusData.value.valid);
        setStatusError(null);
      }

      // 处理用户偏好设置
      if (userPreferencesData.status === 'fulfilled' && userPreferencesData.value) {
        setPreferences(prev => ({ ...prev, ...userPreferencesData.value }));
        setPreferencesError(null);
      }

      setError(null);
    } catch (error) {
      console.error('刷新用户信息时出错:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // ===== 提供给子组件的值 =====
  const value = {
    // 状态数据
    userProfile,
    identity,
    valid,
    preferences,
    isAuthenticated,
    
    // 加载状态
    loading,
    profileLoading,
    statusLoading,
    preferencesLoading,
    
    // 错误状态
    error,
    profileError,
    statusError,
    preferencesError,
    
    // 用户个人资料相关方法
    updateUserProfile,
    getUserAvatar,
    getUserDisplayName,
    
    // 用户偏好设置相关方法
    updateUserPreferences,
    
    // 全局用户管理方法
    loginSuccess,
    clearUser,
    refreshUserInfo,
  };

  return (
    <UserContext.Provider value={value}>
      {children}
    </UserContext.Provider>
  );
};

// Hook for using UserContext
export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
};

export default UserContext;