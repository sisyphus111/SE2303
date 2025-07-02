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
 * 用户签到
 * @returns {Promise<Object>} 签到结果
 */
export const userCheckIn = async () => {
  return post('/user/checkin', {});
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
    
    // 后端返回的结构是 { success, message, data: UserProfileResponse }
    // 直接返回data字段，这就是用户资料对象
    return response.data || response;
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
export const apiUpdateUserProfile = async (updates) => {
  try {
    const response = await put('/user/profile', updates);
    // 后端返回的结构是 { success, message, data: UserProfileResponse }
    return response.data || response;
  } catch (error) {
    console.error('更新用户资料失败:', error);
    throw error;
  }
};

/**
 * 获取用户偏好设置
 * @returns {Promise<Object>} 用户偏好设置
 */
export const getUserPreferences = async () => {
  try {
    const response = await get('/user/preferences');
    console.log('🔍 userService: 后端返回的preferences响应:', response);
    // 注意：后端返回的结构是 { preferences: {...} }，需要访问data.preferences
    return response.preferences || response.data?.preferences;
  } catch (error) {
    console.error('获取用户偏好设置失败:', error);
    throw error;
  }
};

/**
 * 更新用户偏好设置
 * @param {Object} preferences - 偏好设置
 * @returns {Promise<Object>} 更新后的偏好设置
 */
export const apiUpdateUserPreferences = async (preferences) => {
  try {
    console.log('🚀 userService.updateUserPreferences被调用，参数:', preferences);
    console.log('📤 发送POST请求到 /user/preferences...');
    
    const response = await post('/user/preferences', preferences);
    console.log('� userService: 后端返回的原始响应:', response);
    
    // 注意：后端返回的结构是 { data: { preferences: {...} } }
    const result = response.preferences || response.data?.preferences;
    console.log('🔍 userService: 提取的preferences数据:', result);
    
    return result;
  } catch (error) {
    console.error('❌ userService.updateUserPreferences失败:', error);
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