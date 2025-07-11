// 日记相关API服务
import { get, post, put, del } from './common';

export class ContentInspectionClient {
    constructor() {
        // 使用网关端口8080，通过API路径访问
        this.inspectUrl = '/inspect';
    }
    
    async validateContent(content) {
        try {
            // 使用 common.js 中的 post 方法
            const response = await post(this.inspectUrl, {
                content: content
            });
            
            return response.isSafe;
            
        } catch (error) {
            console.error('调用审查服务失败:', error.message);
            return true; // 默认认为安全
        }
    }
    
    /**
     * 创建内容审查警报
     * @param {string} userId - 用户ID
     * @param {string} content - 不当内容
     * @param {string} contentType - 内容类型（title/content）
     * @returns {Promise<Object>} 警报创建结果
     */
    async createContentAlert(userId, content, contentType) {
        try {
            const alertData = {
                userId: userId,
                alertType: 'OTHER', // 使用现有的枚举值
                alertLevel: 'HIGH',
                description: `用户${userId}的日记${contentType}包含不当内容: ${content.substring(0, 100)}${content.length > 100 ? '...' : ''}`,
                operationDetails: JSON.stringify({
                    contentType: contentType,
                    originalContent: content,
                    timestamp: new Date().toISOString(),
                    source: 'DIARY_CONTENT_INSPECTION'
                })
            };
            
            const response = await post('/alert/create', alertData);
            return response;
            
        } catch (error) {
            throw error;
        }
    }
}

/**
 * 创建日记
 * @param {Object} diaryData - 日记数据
 * @param {string} diaryData.diaryTitle - 日记标题
 * @param {string} diaryData.diaryDescription - 日记内容
 * @param {string} diaryData.weather - 天气
 * @param {string} diaryData.date - 日期
 * @param {string} userId - 用户ID
 * @param {boolean} identity - 用户身份
 * @returns {Promise<Object>} 创建的日记
 */
export const createDiary = async (diaryData, userId, identity) => {
  const headers = {
    'X-User-Id': userId,
    'X-Identity': identity || false
  };
  return post('/mood/diary-only', diaryData, { headers });
};

/**
 * 获取日记列表（根据用户ID）
 * @param {string} userId - 用户ID
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 * @returns {Promise<Object>} 日记列表
 */
export const getDiaryList = async (userId, params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return get(`/mood/user/${userId}?${queryString}`);
};

/**
 * 获取日记详情
 * @param {string} diaryId - 日记ID
 * @returns {Promise<Object>} 日记详情
 */
export const getDiaryDetail = async (diaryId) => {
  return get(`/mood/${diaryId}`);
};

/**
 * 更新日记
 * @param {string} diaryId - 日记ID
 * @param {Object} diaryData - 更新的日记数据
 * @param {string} diaryData.date - 日期
 * @param {number} diaryData.score - 心情评分 (1-5)
 * @param {Array} diaryData.moodLabels - 心情标签
 * @param {string} diaryData.diaryTitle - 日记标题
 * @param {string} diaryData.diaryDescription - 日记内容
 * @param {string} diaryData.weather - 天气
 * @returns {Promise<Object>} 更新结果
 */
export const updateDiary = async (diaryId, diaryData) => {
  return put(`/mood/${diaryId}`, diaryData);
};

/**
 * 删除日记
 * @param {string} diaryId - 日记ID
 * @returns {Promise<Object>} 删除结果
 */
export const deleteDiary = async (diaryId) => {
  return del(`/mood/${diaryId}`);
};

/**
 * 删除指定日期的日记
 * @param {string} userId - 用户ID
 * @param {string} date - 日期 (yyyy-MM-dd)
 * @returns {Promise<Object>} 删除结果
 */
export const deleteDiaryByDate = async (userId, date) => {
  return del(`/mood/user/${userId}/diary/${date}`);
};

/**
 * 获取心情统计
 * @param {string} userId - 用户ID
 * @param {string} startDate - 开始日期
 * @param {string} endDate - 结束日期
 * @returns {Promise<Object>} 心情统计数据
 */
export const getMoodStats = async (userId, startDate, endDate) => {
  return get(`/mood/user/${userId}/stats/range?startDate=${startDate}&endDate=${endDate}`);
};

/**
 * 获取用户统计信息
 * @param {string} userId - 用户ID
 * @returns {Promise<Object>} 用户统计信息
 */
export const getUserStats = async (userId) => {
  return get(`/mood/user/${userId}/stats`);
};

/**
 * 检查用户是否已写日记
 * @param {string} userId - 用户ID
 * @param {string} date - 日期
 * @returns {Promise<Object>} 日记状态
 */
export const checkDiaryStatus = async (userId, date) => {
  return get(`/mood/user/${userId}/diary-status/${date}`);
};

/**
 * 加载日记条目（模拟数据，用于前端测试）
 * @returns {Array} 日记条目列表
 */
export const loadDiaryEntries = () => {
  return [
    {
      id: 1,
      title: '充实的一天',
      date: '2024-01-15',
      content: '今天工作进展顺利，完成了几个重要的任务。下午和朋友一起喝咖啡，聊了很多有趣的话题。晚上看了一部电影，感觉很放松。',
      weather: 'sunny'
    },
    {
      id: 2,
      title: '挑战与成长',
      date: '2024-01-14',
      content: '今天遇到了一些工作上的挑战，虽然有点压力，但还是坚持下来了。通过这次经历学到了很多新东西，感觉自己在成长。',
      weather: 'cloudy'
    },
    {
      id: 3,
      title: '雨中思考',
      date: '2024-01-13',
      content: '今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。',
      weather: 'rainy'
    }
  ];
}; 