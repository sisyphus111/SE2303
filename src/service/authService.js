// 认证相关API服务
import { request } from './apiUtils';


/**
 * 用户登出
 * @returns {Promise<Object>} 登出结果
 */
export const logout = async () => {
  return request('/auth/logout', {
    method: 'POST',
  });
};

/**
 * 刷新token
 * @returns {Promise<Object>} 新的token
 */
export const refreshToken = async () => {
  return request('/auth/refresh', {
    method: 'POST',
  });
};

/**
 * 检查登录状态（模拟函数，用于前端测试）
 * @returns {boolean} 是否已登录
 */
export const checkLoginStatus = () => {
  return localStorage.getItem('isLoggedIn') === 'true';
}; 