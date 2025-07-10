import { get, post, put, del } from './common';

// 工具函数：对象转 query string
function toQueryString(params = {}) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '' && value !== 'all') {
      searchParams.append(key, value);
    }
  });
  return searchParams.toString();
}

/**
 * 分页查询警报列表（管理员查看所有警报）
 * @param {Object} params 查询参数
 * @param {string} params.userId 用户ID（可选）
 * @param {string} params.status 警报状态（可选）
 * @param {string} params.alertType 警报类型（可选）
 * @param {string} params.counselorId 心理咨询师ID（可选）
 * @param {number} params.page 页码，默认0
 * @param {number} params.size 每页大小，默认10
 * @returns {Promise<Object>} 分页的警报列表
 */
export const getAlertList = async (params = {}) => {
  const queryString = toQueryString(params);
  const url = queryString ? `/alert/search?${queryString}` : '/alert/search';
  return await get(url);
};

/**
 * 获取待处理警报列表
 * @returns {Promise<Object>} 待处理警报列表
 */
export const getPendingAlerts = async () => {
  return await get('/alert/pending');
};

/**
 * 根据ID获取警报详情
 * @param {number} id 警报ID
 * @returns {Promise<Object>} 警报详情
 */
export const getAlertById = async (id) => {
  return await get(`/alert/${id}`);
};

/**
 * 根据用户ID获取警报列表
 * @param {string} userId 用户ID
 * @returns {Promise<Object>} 用户警报列表
 */
export const getAlertsByUserId = async (userId) => {
  return await get(`/alert/user/${userId}`);
};

/**
 * 根据状态获取警报列表
 * @param {string} status 警报状态
 * @returns {Promise<Object>} 状态警报列表
 */
export const getAlertsByStatus = async (status) => {
  return await get(`/alert/status/${status}`);
};

/**
 * 分配警报给心理咨询师
 * @param {number} alertId 警报ID
 * @param {string} counselorId 心理咨询师ID
 * @returns {Promise<Object>}
 */
export const assignAlert = async (alertId, counselorId) => {
  return await post(`/alert/${alertId}/assign?counselorId=${counselorId}`);
};

/**
 * 更新警报状态
 * @param {number} alertId 警报ID
 * @param {string} status 新状态
 * @returns {Promise<Object>}
 */
export const updateAlertStatus = async (alertId, status) => {
  return await put(`/alert/${alertId}/status?status=${status}`);
};

/**
 * 添加回访备注
 * @param {number} alertId 警报ID
 * @param {string} notes 回访备注
 * @returns {Promise<Object>}
 */
export const addFollowUpNotes = async (alertId, notes) => {
  return await post(`/alert/${alertId}/follow-up?notes=${encodeURIComponent(notes)}`);
};

/**
 * 根据心理咨询师ID获取分配的警报
 * @param {string} counselorId 心理咨询师ID
 * @returns {Promise<Object>} 心理咨询师警报列表
 */
export const getAlertsByCounselorId = async (counselorId) => {
  return await get(`/alert/counselor/${counselorId}`);
};

/**
 * 获取警报统计信息
 * @returns {Promise<Object>} 警报统计
 */
export const getAlertStatistics = async () => {
  return await get('/alert/statistics');
};

/**
 * 删除警报
 * @param {number} alertId 警报ID
 * @returns {Promise<Object>}
 */
export const deleteAlert = async (alertId) => {
  return await del(`/alert/${alertId}`);
};

/**
 * 获取所有警报类型
 * @returns {Promise<Object>} 警报类型列表
 */
export const getAlertTypes = async () => {
  return await get('/alert/types');
};

/**
 * 获取所有警报级别
 * @returns {Promise<Object>} 警报级别列表
 */
export const getAlertLevels = async () => {
  return await get('/alert/levels');
};

/**
 * 获取所有警报状态
 * @returns {Promise<Object>} 警报状态列表
 */
export const getAlertStatuses = async () => {
  return await get('/alert/statuses');
}; 