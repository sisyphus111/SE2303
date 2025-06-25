// API工具函数
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
    ...options,
  };

  // 添加认证token
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

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
    // 未授权，跳转到登录页
    localStorage.removeItem('authToken');
    window.location.href = '/login';
  }
  
  return {
    message: error.message || '请求失败',
    status: error.status || 500,
  };
}; 