// 用户信息相关API服务
import { request } from './apiUtils';

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
  return request('/users/user');
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