// 认证相关API服务 - 基于Cookie认证，前端不处理token
import { post } from './common';

/**
 * 用户登录
 * @param {string} email - 邮箱
 * @param {string} password - 密码
 * @returns {Promise<Object>} 登录结果
 */
export const loginUser = async (email, password) => {
  // 临时前端开发模式 - 直接返回本地用户数据
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 临时验证逻辑 - 任何邮箱和密码都能登录
    const mockUser = {
      id: 'user_123',
      username: 'testuser',
      email: email,
      nickname: '测试用户',
      avatar: 'https://via.placeholder.com/100',
      bio: '这是一个测试用户的个人简介',
      createdAt: '2024-01-01T00:00:00Z',
      lastLoginAt: new Date().toISOString()
    };
    
    const mockPreferences = {
      theme: 'light',
      language: 'zh-CN',
      notifications: {
        email: true,
        push: false,
        dailyReminder: true
      },
      privacy: {
        profileVisible: true,
        diaryVisible: false
      }
    };
    
    // 设置本地存储标识（模拟登录状态）
    localStorage.setItem('dev_user_logged_in', 'true');
    
    return {
      success: true,
      user: mockUser,
      preferences: mockPreferences
    };
  } catch (error) {
    console.error('登录请求失败:', error);
    return {
      success: false,
      error: error.message || '登录失败，请检查邮箱和密码'
    };
  }
  
  /* 原始后端API调用代码 - 后端实现后恢复
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
  */
};

/**
 * 用户注册
 * @param {Object} userData - 用户数据
 * @returns {Promise<Object>} 注册结果
 */
export const registerUser = async (userData) => {
  // 临时前端开发模式 - 直接返回本地用户数据
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 创建模拟用户数据
    const mockUser = {
      id: 'user_' + Date.now(),
      username: userData.username,
      email: userData.email,
      nickname: userData.nickname || userData.username,
      avatar: 'https://via.placeholder.com/100',
      bio: '',
      createdAt: new Date().toISOString(),
      lastLoginAt: new Date().toISOString()
    };
    
    const mockPreferences = {
      theme: 'light',
      language: 'zh-CN',
      notifications: {
        email: true,
        push: false,
        dailyReminder: true
      },
      privacy: {
        profileVisible: true,
        diaryVisible: false
      }
    };
    
    // 设置本地存储标识（模拟注册后自动登录）
    localStorage.setItem('dev_user_logged_in', 'true');
    
    return {
      success: true,
      user: mockUser,
      preferences: mockPreferences
    };
  } catch (error) {
    console.error('注册请求失败:', error);
    return {
      success: false,
      error: error.message || '注册失败，请稍后重试'
    };
  }
  
  /* 原始后端API调用代码 - 后端实现后恢复
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
  */
};

/**
 * 用户登出
 * @returns {Promise<Object>} 登出结果
 */
export const logout = async () => {
  // 临时前端开发模式 - 清除本地存储
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 200));
    
    // 清除本地存储标识
    localStorage.removeItem('dev_user_logged_in');
    
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
  
  /* 原始后端API调用代码 - 后端实现后恢复
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
  */
};

/**
 * 验证邮箱是否可用
 * @param {string} email - 邮箱
 * @returns {Promise<Object>} 验证结果
 */
export const checkEmailAvailability = async (email) => {
  // 临时前端开发模式 - 模拟验证
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 临时逻辑：所有邮箱都视为可用
    return {
      success: true,
      available: true
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || '验证失败'
    };
  }
  
  /* 原始后端API调用代码 - 后端实现后恢复
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
  */
};

/**
 * 验证用户名是否可用
 * @param {string} username - 用户名
 * @returns {Promise<Object>} 验证结果
 */
export const checkUsernameAvailability = async (username) => {
  // 临时前端开发模式 - 模拟验证
  // TODO: 后端实现后，取消注释下面的代码，删除临时代码
  
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 临时逻辑：所有用户名都视为可用
    return {
      success: true,
      available: true
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || '验证失败'
    };
  }
  
  /* 原始后端API调用代码 - 后端实现后恢复
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
  */
}; 