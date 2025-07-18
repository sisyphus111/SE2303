// 用户信息相关API服务
import { get, post, put, del, BASEURL } from './common';


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
    // 后端返回的结构是 { success: true, data: {...}, message: '...' }
    // data字段直接包含偏好设置数据
    return response.data || response;
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
    console.log('📥 userService: 后端返回的原始响应:', response);
    
    // 后端返回的结构是 { success: true, data: {...}, message: '...' }
    // data字段直接包含偏好设置数据
    const result = response.data || response;
    console.log('🔍 userService: 提取的preferences数据:', result);
    
    return result;
  } catch (error) {
    console.error('❌ userService.updateUserPreferences失败:', error);
    throw error;
  }
};

/**
 * 上传用户头像到腾讯云COS
 * 
 * 整个流程：
 * 1. 创建FormData对象，将文件添加到其中
 * 2. 发送POST请求到后端/api/user/avatar接口
 * 3. 后端验证用户身份和文件格式
 * 4. 后端上传文件到腾讯云COS获得URL
 * 5. 后端更新数据库中的用户头像字段
 * 6. 返回新的头像URL给前端
 * 
 * @param {File} avatarFile - 头像文件对象
 * @returns {Promise<string>} 上传后的头像URL
 * @throws {Error} 上传失败时抛出错误
 */
export const uploadUserAvatar = async (avatarFile) => {
  try {
    console.log('🚀 开始上传头像文件:', avatarFile.name, '大小:', avatarFile.size, 'bytes');
    
    // 1. 创建FormData，这是处理文件上传的标准方式
    const formData = new FormData();
    formData.append('avatar', avatarFile);
    
    // 2. 发送POST请求到后端头像上传接口
    // 将localhost:8080改为BASEURL
    const response = await fetch(`${BASEURL}/api/user/avatar`, {
      method: 'POST',
      body: formData,
      credentials: 'include' // 包含cookie中的JWT token用于身份验证
    });
    
    // 3. 检查HTTP状态码
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const errorMessage = errorData.message || `HTTP错误! 状态码: ${response.status}`;
      throw new Error(errorMessage);
    }
    
    // 4. 解析后端返回的JSON响应
    const result = await response.json();
    console.log('📥 后端响应:', result);
    
    // 5. 检查业务逻辑是否成功
    if (!result.success) {
      throw new Error(result.message || '上传失败');
    }
    
    // 6. 返回新的头像URL
    const avatarUrl = result.data?.avatarUrl;
    if (!avatarUrl) {
      throw new Error('后端返回的头像URL为空');
    }
    
    console.log('✅ 头像上传成功，新URL:', avatarUrl);
    return avatarUrl;
    
  } catch (error) {
    console.error('❌ 上传头像失败:', error);
    throw error;
  }
};

// 获取用户总个数
export const getTotalUserCount = async () => {
  try {
    const response = await get('/user/count');
    return response.data || response;
  } catch (error) {
    console.error('获取用户总个数失败:', error);
    throw error;
  }
};

/**
 * 获取用户在社区中的基本信息（头像、昵称、用户名等）
 * @param {string} userId - 用户ID
 * @returns {Promise<Object>} 用户社区信息
 */
export const getUserCommunityInfo = async (userId) => {
  try {
    const response = await get(`/user/community_info/${userId}`);
    return response.data || response;
  } catch (error) {
    console.error('获取用户社区信息失败:', error);
    throw error;
  }
};

/**
 * 获取用户在社区中的统计数据（被点赞数、评论数等）
 * @param {string} userId - 用户ID
 * @returns {Promise<Object>} 用户统计数据
 */
export const getUserStatistics = async (userId) => {
  try {
    const response = await get(`/community/posts/user_statistics/${userId}`);
    return response.data || response;
  } catch (error) {
    console.error('获取用户统计数据失败:', error);
    throw error;
  }
};

/**
 * 获取用户信息
 * @param {string} userId - 用户ID（可选，不传则获取当前用户）
 * @returns {Promise<Object>} 用户信息
 */
export const getUserAdminInfo = async (userId = null) => {
  const endpoint = `/user/admin/users/user 
  return get(endpoint);
};