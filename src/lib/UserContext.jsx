import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { checkLoginStatus } from '../service/authService';

// 初始状态
const initialState = {
  isAuthenticated: false,
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

// Action types
const actionTypes = {
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  LOGIN_SUCCESS: 'LOGIN_SUCCESS',
  LOGOUT: 'LOGOUT',
  UPDATE_USER: 'UPDATE_USER',
  UPDATE_PREFERENCES: 'UPDATE_PREFERENCES',
  CLEAR_ERROR: 'CLEAR_ERROR',
};

// Reducer
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
    case actionTypes.LOGIN_SUCCESS:
      return {
        ...state,
        isAuthenticated: true,
        user: action.payload.user,
        preferences: { ...state.preferences, ...action.payload.preferences },
        loading: false,
        error: null,
      };
    case actionTypes.LOGOUT:
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

  // 初始化用户状态
  useEffect(() => {
    const initializeUser = async () => {
      try {
        dispatch({ type: actionTypes.SET_LOADING, payload: true });
        
        // 检查本地存储的登录状态
        const isLoggedIn = checkLoginStatus();
        
        if (isLoggedIn) {
          // 从本地存储获取用户信息
          const userData = localStorage.getItem('userData');
          const userPreferences = localStorage.getItem('userPreferences');
          
          if (userData) {
            const user = JSON.parse(userData);
            const preferences = userPreferences ? JSON.parse(userPreferences) : {};
            
            dispatch({
              type: actionTypes.LOGIN_SUCCESS,
              payload: { user, preferences },
            });
          } else {
            // 如果没有用户数据但有登录状态，清除登录状态
            localStorage.removeItem('isLoggedIn');
            dispatch({ type: actionTypes.LOGOUT });
          }
        } else {
          dispatch({ type: actionTypes.SET_LOADING, payload: false });
        }
      } catch (error) {
        console.error('初始化用户状态失败:', error);
        dispatch({ type: actionTypes.SET_ERROR, payload: '初始化用户状态失败' });
      }
    };

    initializeUser();
  }, []);

  // 登录
  const login = async (userData, preferences = {}) => {
    try {
      dispatch({ type: actionTypes.SET_LOADING, payload: true });
      
      // 保存到本地存储
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('userData', JSON.stringify(userData));
      localStorage.setItem('userPreferences', JSON.stringify(preferences));
      
      dispatch({
        type: actionTypes.LOGIN_SUCCESS,
        payload: { user: userData, preferences },
      });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '登录失败' });
      return { success: false, error: error.message };
    }
  };

  // 登出
  const logout = async () => {
    try {
      // 清除本地存储
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('userData');
      localStorage.removeItem('userPreferences');
      
      dispatch({ type: actionTypes.LOGOUT });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '登出失败' });
      return { success: false, error: error.message };
    }
  };

  // 更新用户信息
  const updateUser = async (updates) => {
    try {
      const updatedUser = { ...state.user, ...updates };
      
      // 更新本地存储
      localStorage.setItem('userData', JSON.stringify(updatedUser));
      
      dispatch({ type: actionTypes.UPDATE_USER, payload: updates });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '更新用户信息失败' });
      return { success: false, error: error.message };
    }
  };

  // 更新用户偏好
  const updatePreferences = async (preferences) => {
    try {
      const updatedPreferences = { ...state.preferences, ...preferences };
      
      // 更新本地存储
      localStorage.setItem('userPreferences', JSON.stringify(updatedPreferences));
      
      dispatch({ type: actionTypes.UPDATE_PREFERENCES, payload: preferences });
      
      return { success: true };
    } catch (error) {
      dispatch({ type: actionTypes.SET_ERROR, payload: '更新偏好设置失败' });
      return { success: false, error: error.message };
    }
  };

  // 清除错误
  const clearError = () => {
    dispatch({ type: actionTypes.CLEAR_ERROR });
  };

  // 获取用户头像
  const getUserAvatar = () => {
    if (state.user?.avatar) {
      return state.user.avatar;
    }
    
    // 默认头像 - 基于用户名生成
    const username = state.user?.username || state.user?.email || 'User';
    const avatarChar = username.charAt(0).toUpperCase();
    return avatarChar;
  };

  // 获取用户显示名称
  const getUserDisplayName = () => {
    return state.user?.nickname || state.user?.username || state.user?.email || '用户';
  };

  const value = {
    // 状态
    ...state,
    
    // 方法
    login,
    logout,
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