import React, { createContext, useContext, useReducer, useEffect, useCallback } from 'react';
import { getUserProfile, updateUserProfile, updateUserPreferences } from '../service/userService';

// 初始状态 - 仅关注用户信息
const initialState = {
  user: null,
  preferences: {
    theme: 'light',
    language: 'zh-CN',
    notifications: true,
    soundEnabled: true,
  },
  loading: true,
  error: null,
};

// Action types - 移除登录相关actions
const actionTypes = {
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  SET_USER: 'SET_USER',
  CLEAR_USER: 'CLEAR_USER',
  UPDATE_USER: 'UPDATE_USER',
  UPDATE_PREFERENCES: 'UPDATE_PREFERENCES',
  CLEAR_ERROR: 'CLEAR_ERROR',
};

// Reducer - 简化状态管理
const userReducer = (state, action) => {
  switch (action.type) {
    case actionTypes.SET_LOADING:
      return {
        ...state,
        loading: action.payload,
      };
    case actionTypes.SET_ERROR:
      return {
        ...state,
        error: action.payload,
        loading: false,
      };
    case actionTypes.SET_USER:
      return {
        ...state,
        user: action.payload.user,
        preferences: { ...state.preferences, ...action.payload.preferences },
        loading: false,
        error: null,
      };
    case actionTypes.CLEAR_USER:
      return {
        ...initialState,
        loading: false,
      };
    case actionTypes.UPDATE_USER:
      return {
        ...state,
        user: { ...state.user, ...action.payload },
      };
    case actionTypes.UPDATE_PREFERENCES:
      return {
        ...state,
        preferences: { ...state.preferences, ...action.payload },
      };
    case actionTypes.CLEAR_ERROR:
      return {
        ...state,
        error: null,
      };
    default:
      return state;
  }
};

// Context
const UserContext = createContext();

// Provider Component
export const UserProvider = ({ children }) => {
  const [state, dispatch] = useReducer(userReducer, initialState);

  // 初始化用户信息 - 通过API获取，不依赖localStorage
  useEffect(() => {
    const initializeUser = async () => {
      try {
        dispatch({ type: actionTypes.SET_LOADING, payload: true });
        
        // 尝试从后端获取用户信息
        // 如果cookie中有有效token，后端会返回用户信息
        const userProfile = await getUserProfile();
        
        if (userProfile) {
          dispatch({
            type: actionTypes.SET_USER,
            payload: { 
              user: userProfile.user, 
              preferences: userProfile.preferences || {} 
            },
          });
        } else {
          // 没有用户信息，设置为未登录状态
          dispatch({ type: actionTypes.SET_LOADING, payload: false });
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        // 如果是401错误，说明未登录，这是正常情况
        if (error.status === 401) {
          dispatch({ type: actionTypes.SET_LOADING, payload: false });
        } else {
          dispatch({ type: actionTypes.SET_ERROR, payload: '获取用户信息失败' });
        }
      }
    };

    initializeUser();
  }, []);

  // 设置用户信息（登录成功后调用）
  const setUser = useCallback((userData, preferences = {}) => {
    dispatch({
      type: actionTypes.SET_USER,
      payload: { user: userData, preferences },
    });
  }, []);

  // 清除用户信息（登出后调用）
  const clearUser = useCallback(() => {
    dispatch({ type: actionTypes.CLEAR_USER });
  }, []);

  // 更新用户信息
  const updateUser = useCallback(async (updates) => {
    try {
      // 调用API更新用户信息
      const updatedUser = await updateUserProfile(updates);
      
      dispatch({ type: actionTypes.UPDATE_USER, payload: updatedUser });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '更新用户信息失败' });
      return { success: false, error: error.message };
    }
  }, []);

  // 更新用户偏好
  const updatePreferences = useCallback(async (preferences) => {
    try {
      // 调用API更新偏好设置
      const updatedPreferences = await updateUserPreferences(preferences);
      
      dispatch({ type: actionTypes.UPDATE_PREFERENCES, payload: updatedPreferences });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '更新偏好设置失败' });
      return { success: false, error: error.message };
    }
  }, []);

  // 清除错误 - 使用useCallback优化
  const clearError = useCallback(() => {
    dispatch({ type: actionTypes.CLEAR_ERROR });
  }, []);

  // 获取用户头像
  const getUserAvatar = useCallback(() => {
    if (state.user?.avatar) {
      return state.user.avatar;
    }
    
    // 默认头像 - 基于用户名生成
    const username = state.user?.username || state.user?.email || 'User';
    const avatarChar = username.charAt(0).toUpperCase();
    return avatarChar;
  }, [state.user]);

  // 获取用户显示名称
  const getUserDisplayName = useCallback(() => {
    return state.user?.nickname || state.user?.username || state.user?.email || '用户';
  }, [state.user]);

  // 检查是否已登录 - 基于是否有用户信息
  const isAuthenticated = !!state.user;

  const value = {
    // 状态
    user: state.user,
    preferences: state.preferences,
    loading: state.loading,
    error: state.error,
    isAuthenticated,
    
    // 方法 - 仅保留用户信息管理相关方法
    setUser,
    clearUser,
    updateUser,
    updatePreferences,
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