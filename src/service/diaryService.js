// 日记相关API服务
import { get, post, put, del } from './common';

/**
 * 创建日记
 * @param {Object} diaryData - 日记数据
 * @param {string} diaryData.id - 日记ID
 * @param {string} diaryData.data - 日记日期
 * @param {string} diaryData.content - 日记内容（根据页面使用content字段）
 * @param {number} diaryData.mood - 心情评分（1-5，根据页面显示）
 * @returns {Promise<Object>} 创建的日记
 */
export const createDiary = async (diaryData) => {
  return post('/mood/diaries', diaryData);
};

/**
 * 获取日记列表
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 * @param {number} params.mood - 心情筛选
 * @returns {Promise<Object>} 日记列表
 */
export const getDiaryList = async (params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return get(`/mood/diaries?${queryString}`);
};

/**
 * 获取日记详情
 * @param {string} diaryId - 日记ID
 * @returns {Promise<Object>} 日记详情
 */
export const getDiaryDetail = async (diaryId) => {
  return get(`/mood/diaries/${diaryId}`);
};

/**
 * 更新日记
 * @param {string} diaryId - 日记ID
 * @param {Object} diaryData - 更新的日记数据
 * @returns {Promise<Object>} 更新结果
 */
export const updateDiary = async (diaryId, diaryData) => {
  return put(`/mood/diaries/${diaryId}`, diaryData);
};

/**
 * 删除日记
 * @param {string} diaryId - 日记ID
 * @returns {Promise<Object>} 删除结果
 */
export const deleteDiary = async (diaryId) => {
  return del(`/mood/diaries/${diaryId}`);
};

/**
 * 获取心情统计
 * @param {string} startDate - 开始日期
 * @param {string} endDate - 结束日期
 * @returns {Promise<Object>} 心情统计数据
 */
export const getMoodStats = async (startDate, endDate) => {
  return get(`/mood/diaries/mood-stats?startDate=${startDate}&endDate=${endDate}`);
};

/**
 * 加载日记条目（模拟数据，用于前端测试）
 * @returns {Array} 日记条目列表
 */
export const loadDiaryEntries = () => {
  return [
    {
      id: 1,
      date: '2024-01-15',
      content: '今天心情不错，工作进展顺利...',
      mood: 4
    },
    {
      id: 2,
      date: '2024-01-14',
      content: '有点压力，但还是坚持下来了...',
      mood: 3
    }
  ];
}; 