/**
 * 用户服务扩展 - 支持聊天功能的用户信息获取
 */

import { get } from './common.js';

/**
 * 批量获取用户信息（用于聊天界面显示）
 * @param {Array<string|number>} userIds - 用户ID数组
 * @returns {Promise<Object>} 用户信息映射
 */
export async function getUsersInfo(userIds) {
  if (!userIds || userIds.length === 0) {
    return { success: true, data: {} };
  }

  try {
    const params = {
      userIds: userIds.join(',')
    };

    const response = await get('/user/batch-info', { params });
    
    if (response.success) {
      // 转换为以用户ID为key的映射对象
      const userMap = {};
      response.data.forEach(user => {
        userMap[user.id] = {
          id: user.id,
          username: user.username || user.nickname || `用户${user.id}`,
          avatar: user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.id}`,
          nickname: user.nickname
        };
      });
      
      return {
        success: true,
        data: userMap
      };
    } else {
      console.error('❌ 批量获取用户信息失败:', response.message);
      return {
        success: false,
        message: response.message,
        data: {}
      };
    }
  } catch (error) {
    console.error('❌ 批量获取用户信息异常:', error);
    return {
      success: false,
      message: '网络错误，请稍后重试',
      data: {}
    };
  }
}

/**
 * 获取单个用户信息
 * @param {string|number} userId - 用户ID
 * @returns {Promise<Object>} 用户信息
 */
export async function getUserInfo(userId) {
  if (!userId) {
    return {
      success: false,
      message: '用户ID不能为空'
    };
  }

  try {
    const response = await get(`/user/${userId}/info`);
    
    if (response.success) {
      return {
        success: true,
        data: {
          id: response.data.id,
          username: response.data.username || response.data.nickname || `用户${response.data.id}`,
          avatar: response.data.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${response.data.id}`,
          nickname: response.data.nickname
        }
      };
    } else {
      console.error('❌ 获取用户信息失败:', response.message);
      return {
        success: false,
        message: response.message
      };
    }
  } catch (error) {
    console.error('❌ 获取用户信息异常:', error);
    return {
      success: false,
      message: '网络错误，请稍后重试'
    };
  }
}

/**
 * 获取当前登录用户信息
 * @returns {Promise<Object>} 当前用户信息
 */
export async function getCurrentUserInfo() {
  try {
    const response = await get('/user/current');
    
    if (response.success) {
      return {
        success: true,
        data: {
          id: response.data.id,
          username: response.data.username || response.data.nickname || '我',
          avatar: response.data.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${response.data.id}`,
          nickname: response.data.nickname,
          email: response.data.email
        }
      };
    } else {
      console.error('❌ 获取当前用户信息失败:', response.message);
      return {
        success: false,
        message: response.message
      };
    }
  } catch (error) {
    console.error('❌ 获取当前用户信息异常:', error);
    return {
      success: false,
      message: '网络错误，请稍后重试'
    };
  }
}
