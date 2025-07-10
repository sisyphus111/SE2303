// 打卡相关API服务
import { get, post } from './common';

/**
 * 提交打卡数据
 * @param {Object} checkInData - 打卡数据
 * @param {number} checkInData.rating - 心情评分 (1-5)
 * @param {Array} checkInData.tags - 情绪标签数组
 * @param {string} userId - 用户ID
 * @returns {Promise<Object>} 打卡结果
 */
export const submitCheckIn = async (checkInData, userId = 'default-user') => {
  try {
    const requestData = {
      date: new Date().toISOString().split('T')[0], // 格式化为 YYYY-MM-DD
      score: checkInData.rating,
      moodLabels: checkInData.tags
    };

    // 添加认证头
    const options = {
      headers: {
        'X-User-Id': userId,
        'X-Identity': 'true' // 假设用户已认证
      }
    };

    const response = await post('/mood/check-in-only', requestData, options);
    return response;
  } catch (error) {
    console.error('提交打卡失败:', error);
    throw error;
  }
};

/**
 * 检查用户今日是否已打卡
 * @param {string} userId - 用户ID
 * @param {string} date - 日期 (YYYY-MM-DD)
 * @returns {Promise<boolean>} 是否已打卡
 */
export const checkTodayStatus = async (userId, date = null) => {
  try {
    const checkDate = date || new Date().toISOString().split('T')[0];
    const response = await get(`/mood/user/${userId}/check-status/${checkDate}`);
    return response.hasCheckedIn || false;
  } catch (error) {
    console.error('检查打卡状态失败:', error);
    return false;
  }
};

/**
 * 获取用户今日心情记录
 * @param {string} userId - 用户ID
 * @param {string} date - 日期 (YYYY-MM-DD)
 * @returns {Promise<Object>} 心情记录
 */
export const getTodayMood = async (userId, date = null) => {
  try {
    const checkDate = date || new Date().toISOString().split('T')[0];
    const response = await get(`/mood/user/${userId}/date/${checkDate}`);
    return response;
  } catch (error) {
    console.error('获取今日心情记录失败:', error);
    return null;
  }
};

/**
 * 获取用户心情记录列表
 * @param {string} userId - 用户ID
 * @param {string} startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} endDate - 结束日期 (YYYY-MM-DD)
 * @returns {Promise<Array>} 心情记录列表
 */
export const getMoodHistory = async (userId, startDate = null, endDate = null) => {
  try {
    if (startDate && endDate) {
      const response = await get(`/mood/user/${userId}/range?startDate=${startDate}&endDate=${endDate}`);
      return response;
    } else {
      const response = await get(`/mood/user/${userId}`);
      return response;
    }
  } catch (error) {
    console.error('获取心情记录历史失败:', error);
    return [];
  }
};

/**
 * 获取可用的情绪标签
 * @returns {Promise<Array>} 情绪标签列表
 */
export const getAvailableLabels = async () => {
  try {
    const response = await get('/mood/labels');
    
    // 后端返回格式: { positive: [...], neutral: [...], negative: [...] }
    if (response && typeof response === 'object') {
      const allLabels = [];
      
      // 合并所有分类的标签，并转换为字符串
      if (response.positive && Array.isArray(response.positive)) {
        allLabels.push(...response.positive.map(item => item.label || item));
      }
      if (response.neutral && Array.isArray(response.neutral)) {
        allLabels.push(...response.neutral.map(item => item.label || item));
      }
      if (response.negative && Array.isArray(response.negative)) {
        allLabels.push(...response.negative.map(item => item.label || item));
      }
      
      if (allLabels.length > 0) {
        return allLabels;
      }
    }
    
    // 如果后端数据格式不正确或为空，返回默认标签
    console.warn('后端返回的标签数据格式不正确或为空:', response);
    return [
      '开心', '焦虑', '压力', '放松', '疲惫', 
      '兴奋', '平静', '担心', '满足', '困惑'
    ];
  } catch (error) {
    console.error('获取情绪标签失败:', error);
    // 返回默认标签
    return [
      '开心', '焦虑', '压力', '放松', '疲惫', 
      '兴奋', '平静', '担心', '满足', '困惑'
    ];
  }
};
