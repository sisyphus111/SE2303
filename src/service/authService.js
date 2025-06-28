// 认证相关API服务 - 基于Cookie认证，前端不处理token
import { post } from './common';

/**
 * 用户登录
 * @param {string} email - 邮箱
 * @param {string} password - 密码
 * @returns {Promise<Object>} 登录结果
 */
export const loginUser = async (email, password) => {
  try {
    // 调用后端登录API
    // 后端会在登录成功后自动设置HttpOnly Cookie
    const response = await post('/auth/login', { email, password });
    
    return {
      success: true,
      user: response.user,
      preferences: response.preferences || {}
    };
  } catch (error) {
    console.error('登录请求失败:', error);
    return {
      success: false,
      error: error.message || '登录失败，请检查邮箱和密码'
    };
  }
};

/**
 * 用户注册
 * @param {Object} userData - 用户数据
 * @returns {Promise<Object>} 注册结果
 */
export const registerUser = async (userData) => {
  try {
    // 调用后端注册API
    const response = await post('/auth/register', userData);
    
    return {
      success: true,
      user: response.user,
      preferences: response.preferences || {}
    };
  } catch (error) {
    console.error('注册请求失败:', error);
    return {
      success: false,
      error: error.message || '注册失败，请稍后重试'
    };
  }
};

/**
 * 用户登出
 * @returns {Promise<Object>} 登出结果
 */
export const logout = async () => {
  try {
    // 调用后端登出API
    // 后端会清除HttpOnly Cookie
    await post('/auth/logout', {});
    
    return {
      success: true,
      message: '登出成功'
    };
  } catch (error) {
    console.error('登出请求失败:', error);
    return {
      success: false,
      error: '登出失败'
    };
  }
};

/**
 * 验证邮箱是否可用
 * @param {string} email - 邮箱
 * @returns {Promise<Object>} 验证结果
 */
export const checkEmailAvailability = async (email) => {
  try {
    const response = await post('/auth/check-email', { email });
    return {
      success: true,
      available: response.available
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || '验证失败'
    };
  }
};

/**
 * 验证用户名是否可用
 * @param {string} username - 用户名
 * @returns {Promise<Object>} 验证结果
 */
export const checkUsernameAvailability = async (username) => {
  try {
    const response = await post('/auth/check-username', { username });
    return {
      success: true,
      available: response.available
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || '验证失败'
    };
  }
}; 