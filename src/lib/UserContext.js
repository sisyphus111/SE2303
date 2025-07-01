import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getUserStatus, getUserProfile, updateUserProfile, updateUserPreferences } from '../service/userService';

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
  const [preferences, setPreferences] = useState({
    theme: 'light',
    language: 'zh-CN',
    notifications: true,
    soundEnabled: true,
  }); // 这里的初始默认数据需要修改
  const [preferencesLoading, setPreferencesLoading] = useState(false);
  const [preferencesError, setPreferencesError] = useState(null);

  // ===== 全局加载和错误状态 =====
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // ===== 计算属性 =====
  const isAuthenticated = !!(userProfile && identity);

  // ===== 初始化用户信息 =====
  useEffect(() => {
    const initializeUser = async () => {
      try {
        setLoading(true);
        
        // 并行获取用户数据
        const [userProfileData, userStatusData, userPreferencesData] = await Promise.allSettled([
          getUserProfile(),
          getUserStatus(),
          // getUserPreferences() // 等待yyf完成此函数
        ]);

        // 处理用户个人资料
        if (userProfileData.status === 'fulfilled' && userProfileData.value) {
          setUserProfile(userProfileData.value);
          setProfileError(null);
        } else if (userProfileData.status === 'rejected') {
          console.error('获取用户个人资料失败:', userProfileData.reason);
          setProfileError('获取用户个人资料失败');
        }

        // 处理用户状态
        if (userStatusData.status === 'fulfilled' && userStatusData.value) {
          setIdentity(userStatusData.value.identity);
          setValid(userStatusData.value.valid);
          setStatusError(null);
        } else if (userStatusData.status === 'rejected') {
          console.error('获取用户状态失败:', userStatusData.reason);
          setStatusError('获取用户状态失败');
        }

        // 处理用户偏好设置
        // if (userPreferencesData.status === 'fulfilled' && userPreferencesData.value) {
        //   setPreferences(prev => ({ ...prev, ...userPreferencesData.value }));
        //   setPreferencesError(null);
        // } else if (userPreferencesData.status === 'rejected') {
        //   console.error('获取用户偏好设置失败:', userPreferencesData.reason);
        //   setPreferencesError('获取用户偏好设置失败');
        // }

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
  const updateUserProfileData = useCallback(async (updates) => {
    try {
      setProfileLoading(true);
      setProfileError(null);
      
      const updatedProfile = await updateUserProfile(updates);
      setUserProfile(prev => ({ ...prev, ...updatedProfile }));
      
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
  const updateUserPrefs = useCallback(async (newPreferences) => {
    try {
      setPreferencesLoading(true);
      setPreferencesError(null);
      
      const updatedPreferences = await updateUserPreferences(newPreferences);
      setPreferences(prev => ({ ...prev, ...updatedPreferences }));
      
      return { success: true, data: updatedPreferences };
    } catch (error) {
      console.error('更新偏好设置失败:', error);
      setPreferencesError('更新偏好设置失败');
      return { success: false, error: error.message };
    } finally {
      setPreferencesLoading(false);
    }
  }, []);

  const resetUserPreferences = useCallback(() => {
    setPreferences({
      theme: 'light',
      language: 'zh-CN',
      notifications: true,
      soundEnabled: true,
    }); // 这里的默认数据需要修改
    setPreferencesError(null);
  }, []);

  // ===== 全局用户管理方法 =====
  const setUserData = useCallback((profileData, statusData, preferencesData) => {
    if (profileData) {
      setUserProfile(profileData);
      setProfileError(null);
    }
    
    if (statusData) {
      setIdentity(statusData.identity);
      setValid(statusData.valid);
      setStatusError(null);
    }
    
    if (preferencesData) {
      setPreferences(prev => ({ ...prev, ...preferencesData }));
      setPreferencesError(null);
    }
    
    setError(null);
    setLoading(false);
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
    updateUserProfile: updateUserProfileData,
    getUserAvatar,
    getUserDisplayName,
    
    // 用户偏好设置相关方法
    updateUserPrefs,
    resetUserPreferences,
    
    // 全局用户管理方法
    setUserData,
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