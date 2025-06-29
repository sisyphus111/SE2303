// 用户信息相关API服务
import { request } from './apiUtils';
import { get, post, put } from './common';

/**
 * 获取用户信息
 * @param {string} userId - 用户ID（可选，不传则获取当前用户）
 * @returns {Promise<Object>} 用户信息
 */
export const getUserInfo = async (userId = null) => {
  const endpoint = userId ? `/users/user : '/users/user';
  return request(endpoint);
};

/**
 * 更新用户信息
 * @param {Object} userData - 用户数据
 * @param {string} userData.nickname - 昵称
 * @param {string} userData.avatar - 头像URL
 * @param {string} userData.bio - 个人简介
 * @param {string} userData.email - 邮箱
 * @returns {Promise<Object>} 更新结果
 */
export const updateUserInfo = async (userData) => {
  return request('/users/user', {
    method: 'PUT',
    body: JSON.stringify(userData),
  });
};

/**
 * 获取用户统计数据
 * @returns {Promise<Object>} 统计数据
 */
export const getUserStats = async () => {
  try {
    const response = await get('/user/stats');
    return response;
  } catch (error) {
    console.error('获取用户统计信息失败:', error);
    throw error;
  }
};

/**
 * 获取用户设置
 * @returns {Promise<Object>} 用户设置
 */
export const getUserSettings = async () => {
  return request('/users/user');
};

/**
 * 更新用户设置
 * @param {Object} settings - 设置数据
 * @returns {Promise<Object>} 更新结果
 */
export const updateUserSettings = async (settings) => {
  return request('/users/user', {
    method: 'PUT',
    body: JSON.stringify(settings),
  });
};

/**
 * 用户签到
 * @returns {Promise<Object>} 签到结果
 */
export const userCheckIn = async () => {
  return request('/users/user', {
    method: 'POST',
  });
};

/**
 * 获取用户设置数据（模拟函数，用于前端测试）
 * @returns {Promise<Object>} 用户设置数据
 */
export const getSettingsData = async () => {
  // 模拟API调用延迟
  await new Promise(resolve => setTimeout(resolve, 300));
  
  return {
    profile: {
      nickname: '用户昵称',
      avatar: 'https://via.placeholder.com/100',
      bio: '这是我的个人简介',
      email: 'user@example.com'
    },
    settings: {
      notifications: {
        email: true,
        push: false,
        dailyReminder: true
      },
      privacy: {
        profileVisible: true,
        diaryVisible: false
      },
      theme: 'light',
      language: 'zh-CN'
    }
  };
};

/**
 * 获取用户资料
 * @returns {Promise<Object>} 用户资料
 */
export const getUserProfile = async () => {
  // 临时前端开发模式 - 直接返回本地用户数据
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 检查是否有本地存储的用户信息（模拟登录状态）
    const hasUserSession = localStorage.getItem('dev_user_logged_in') === 'true';
    
    if (!hasUserSession) {
      // 模拟401未登录状态
      return null;
    }
    
    // 返回模拟用户数据
    const mockUser = {
      id: 'user_123',
      username: 'testuser',
      email: 'test@example.com',
      nickname: '测试用户',
      avatar: 'https://via.placeholder.com/100',
      bio: '这是一个测试用户的个人简介',
      createdAt: '2024-01-01T00:00:00Z',
      lastLoginAt: new Date().toISOString()
    };
    
    const mockPreferences = {
      theme: 'light',
      language: 'zh-CN',
      notifications: {
        email: true,
        push: false,
        dailyReminder: true
      },
      privacy: {
        profileVisible: true,
        diaryVisible: false
      }
    };
    
    return {
      user: mockUser,
      preferences: mockPreferences
    };
  } catch (error) {
    console.error('获取用户资料失败:', error);
    throw error;
  }
  
  /* 原始后端API调用代码 - 后端实现后恢复
  try {
    // 调用后端API获取用户资料
    // 如果cookie中有有效token，后端会返回用户信息
    const response = await get('/user/profile');
    
    return {
      user: response.user,
      preferences: response.preferences || {}
    };
  } catch (error) {
    // 如果是401错误，说明未登录
    if (error.status === 401) {
      return null;
    }
    throw error;
  }
  */
};

/**
 * 更新用户资料
 * @param {Object} updates - 要更新的用户信息
 * @returns {Promise<Object>} 更新后的用户信息
 */
export const updateUserProfile = async (updates) => {
  try {
    const response = await put('/user/profile', updates);
    return response.user;
  } catch (error) {
    console.error('更新用户资料失败:', error);
    throw error;
  }
};

/**
 * 更新用户偏好设置
 * @param {Object} preferences - 偏好设置
 * @returns {Promise<Object>} 更新后的偏好设置
 */
export const updateUserPreferences = async (preferences) => {
  try {
    const response = await put('/user/preferences', preferences);
    return response.preferences;
  } catch (error) {
    console.error('更新偏好设置失败:', error);
    throw error;
  }
};

/**
 * 上传用户头像
 * @param {File} avatarFile - 头像文件
 * @returns {Promise<Object>} 上传结果
 */
export const uploadUserAvatar = async (avatarFile) => {
  try {
    const formData = new FormData();
    formData.append('avatar', avatarFile);
    
    const response = await fetch('/api/user/avatar', {
      method: 'POST',
      body: formData,
      credentials: 'include' // 包含cookie
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    return result.avatarUrl;
  } catch (error) {
    console.error('上传头像失败:', error);
    throw error;
  }
}; 