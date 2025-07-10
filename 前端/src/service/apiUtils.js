// API工具函数 - 基于Cookie认证，不处理token
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

/**
 * 通用请求函数
 * @param {string} endpoint - API端点
 * @param {Object} options - 请求选项
 * @returns {Promise<Object>} 响应数据
 */
export const request = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    credentials: 'include', // 包含cookie进行认证
    ...options,
  };

  try {
    const response = await fetch(url, config);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('API请求失败:', error);
    throw error;
  }
};

/**
 * 处理API错误
 * @param {Error} error - 错误对象
 * @returns {Object} 错误信息
 */
export const handleApiError = (error) => {
  if (error.message.includes('401')) {
    // 未授权，跳转到登录页（由ErrorHandler统一处理）
    console.log('检测到401错误，将由ErrorHandler处理重定向');
  }
  
  return {
    message: error.message || '请求失败',
    status: error.status || 500,
  };
}; 