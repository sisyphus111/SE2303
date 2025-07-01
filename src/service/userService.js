// 用户信息相关API服务
import { get, post, put, del } from './common';

/**
 * 获取用户身份、是否启用信息
 * @returns {Promise<Object>} 用户身份identity(bool类型)、是否启用valid(bool类型)组成的结构体
 */
export const getUserStatus = async () => {
  return get('/user/status');
};


/**
 * 获取用户信息
 * @param {string} userId - 用户ID（可选，不传则获取当前用户）
 * @returns {Promise<Object>} 用户信息
 */
export const getUserInfo = async (userId = null) => {
  const endpoint = userId ? `/user/${userId}` : '/user/profile';
  return get(endpoint);
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
  return put('/user/profile', userData);
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
  return get('/user/settings');
};

/**
 * 更新用户设置
 * @param {Object} settings - 设置数据
 * @returns {Promise<Object>} 更新结果
 */
export const updateUserSettings = async (settings) => {
  return put('/user/settings', settings);
};

/**
 * 用户签到
 * @returns {Promise<Object>} 签到结果
 */
export const userCheckIn = async () => {
  return post('/user/checkin', {});
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