import { get, post, put, del } from './common';

/**
 * 分页按id正序获取音乐列表
 * @param {Object} params { page, size }
 * @returns {Promise<Object>} { list, total }
 */
export const getMusicListByIdAsc = async (params = { page: 1, size: 20 }) => {
  const query = new URLSearchParams(params).toString();
  return await get(`/notification/music/listByIdAsc?${query}`);
};

/**
 * 删除音乐
 * @param {number|string} musicId
 * @returns {Promise<Object>}
 */
export const deleteMusic = async (musicId) => {
  return await del(`/notification/music/${musicId}`);
};

/**
 * 更新音乐
 * @param {number|string} musicId
 * @param {Object} data
 * @returns {Promise<Object>}
 */
export const updateMusic = async (musicId, data) => {
  return await put(`/notification/music/${musicId}`, data);
};

/**
 * 创建音乐
 * @param {Object} data
 * @returns {Promise<Object>}
 */
export const createMusic = async (data) => {
  return await post('/notification/music/create', data);
}; 