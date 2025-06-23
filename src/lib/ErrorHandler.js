import { message } from 'antd';

/**
 * 自定义API错误类，包含错误代码
 */
export class ApiError extends Error {
  constructor(message, errorCode) {
    super(message);
    this.name = 'ApiError';
    this.errorCode = errorCode;
  }
}

/**
 * 处理API错误并显示消息通知
 * @param {Error|ApiError} error - 错误对象
 * @param {string} defaultMessage - 默认错误消息
 * @param {Function} callback - 错误处理后的回调函数
 */
export const handleApiError = (error, defaultMessage = '操作失败', callback) => {
  let errorMessage = defaultMessage;
  let errorCode = null;

  // 判断是否为ApiError类型
  if (error instanceof ApiError) {
    errorMessage = error.message || defaultMessage;
    errorCode = error.errorCode;
  } else if (error instanceof Error) {
    errorMessage = error.message || defaultMessage;
  }

  // 显示错误消息
  if (errorCode) {
    message.error(`${errorMessage} (错误代码: ${errorCode})`);
  } else {
    message.error(errorMessage);
  }

  // 如果提供了回调函数，则执行
  if (typeof callback === 'function') {
    callback(error);
  }

  // 可选：记录错误到控制台或错误追踪服务
  console.error('API错误:', {
    message: errorMessage,
    code: errorCode,
    originalError: error
  });
  
};

/**
 * 处理API响应结果
 * @param {Object} response - API响应对象
 * @returns {Object} 响应数据
 * @throws {ApiError} 如果响应不成功
 */
export const handleApiResponse = (response) => {
  if (response.success) {
    return response.data;
  }
  if (response.errorCode === 401) { // 401状态码表示未登录
    // 清除本地认证状态
    window.location.href = '/login';
    return Promise.reject(new Error('未授权访问'));
  }
  throw new ApiError(
    response.message || '请求失败', 
    response.errorCode
  );
};
