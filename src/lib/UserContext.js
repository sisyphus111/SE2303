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

  // 使用useState管理用户状态（个人资料、偏好设置、身份信息、验证状态）
  const [userProfile, setUserProfile] = useState(null);
  const [preferences, setPreferences] = useState(null);
  const [identity, setIdentity] = useState(null);
  const [valid, setValid] = useState(null);


  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 初始化用户信息
  useEffect(() => {
    const initializeUser = async () => {
      try {
        setLoading(true);
        
        // 尝试从后端获取用户个人资料，用户账号类型、状态信息
        const userProfile = await getUserProfile();
        const userStatus = await getUserStatus();
        const userPreferences = await getUserPreferences(); // 这个函数由UserService——yyf来完成
      
        if (userProfile && userStatus && userPreferences) {
          // 有用户信息，设置登录状态
          setUserProfile(userProfile);
          setPreferences(userPreferences);
          setIdentity(userStatus.identity);
          setValid(userStatus.valid);
          setError(null);
        }
        
        setLoading(false);
      } catch (error) {

        console.error('初始化用户信息时出错:', error);
        const isAuth401Error = error.status === 401 || 
                               error.message?.includes('401') || 
                               error.message?.includes('未授权');
        
        if (!isAuth401Error && !isAuthPage()) {
          // 非401错误且不在登录页面，显示错误信息
          setError('获取用户信息失败');
        } else {
          // 401错误或在登录页面，静默处理
          console.log('📝 401错误或登录页面，由ErrorHandler统一处理');
        }
        
        setLoading(false);
      }
    };

    initializeUser();
  }, []);





  // 设置用户信息（登录成功后调用）
  const setUserInfo = useCallback((userData, userPreferences = {}) => {
    setUser(userData);
    setPreferences(prev => ({ ...prev, ...userPreferences }));
    setError(null);
    setLoading(false);
  }, []);

  // 清除用户信息（登出后调用）
  const clearUser = useCallback(() => {
    setUser(null);
    setPreferences({
      theme: 'light',
      language: 'zh-CN',
      notifications: true,
      soundEnabled: true,
    });
    setError(null);
    setLoading(false);
  }, []);

  // 更新用户信息
  const updateUser = useCallback(async (updates) => {
    try {
      // 调用API更新用户信息
      const updatedUser = await updateUserProfile(updates);
      
      // 更新本地状态
      setUser(prev => ({ ...prev, ...updatedUser }));
      
      return { success: true };
    } catch (error) {
      setError('更新用户信息失败');
      return { success: false, error: error.message };
    }
  }, []);

  // 更新用户偏好
  const updateUserPrefs = useCallback(async (newPreferences) => {
    try {
      // 调用API更新偏好设置
      const updatedPreferences = await updateUserPreferences(newPreferences);
      
      // 更新本地状态
      setPreferences(prev => ({ ...prev, ...updatedPreferences }));
      
      return { success: true };
    } catch (error) {
      setError('更新偏好设置失败');
      return { success: false, error: error.message };
    }
  }, []);

  // 清除错误
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // 获取用户头像
  const getUserAvatar = useCallback(() => {
    if (user?.avatar) {
      return user.avatar;
    }
    
    // 默认头像 - 基于用户名生成
    const username = user?.username || user?.email || 'User';
    const avatarChar = username.charAt(0).toUpperCase();
    return avatarChar;
  }, [user]);

  // 获取用户显示名称
  const getUserDisplayName = useCallback(() => {
    return user?.nickname || user?.username || user?.email || '用户';
  }, [user]);

  // 提供给子组件的值
  const value = {
    // 状态
    userProfile,
    preferences,
    identity,
    valid,
    loading,
    error,
    isAuthenticated,
    
    // 方法
    setUser: setUserInfo,
    clearUser,
    updateUser,
    updatePreferences: updateUserPrefs,
    clearError,
    getUserAvatar,
    getUserDisplayName,
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