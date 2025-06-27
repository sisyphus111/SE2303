// 认证相关API服务
import { request } from './apiUtils';

/**
 * 用户登录
 * @param {string} email - 邮箱
 * @param {string} password - 密码
 * @returns {Promise<Object>} 登录结果
 */
export const loginUser = async (email, password) => {
  try {
    // 在实际项目中，这里应该调用真实的API
    // const response = await request('/auth/login', {
    //   method: 'POST',
    //   body: JSON.stringify({ email, password }),
    // });

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 模拟登录验证
    const mockUsers = [
      {
        email: 'demo@example.com',
        password: '123456',
        userData: {
          id: 'demo-user',
          email: 'demo@example.com',
          username: 'demo_user',
          nickname: '演示用户',
          avatar: null,
          createdAt: new Date().toISOString(),
        }
      },
      {
        email: 'test@example.com',
        password: '123456',
        userData: {
          id: 'test-user',
          email: 'test@example.com',
          username: 'test_user',
          nickname: '测试用户',
          avatar: null,
          createdAt: new Date().toISOString(),
        }
      }
    ];

    const user = mockUsers.find(u => u.email === email && u.password === password);
    
    if (user) {
      return {
        success: true,
        user: user.userData,
        preferences: {
          theme: 'light',
          language: 'zh-CN',
          notifications: true,
          soundEnabled: true,
        },
        token: 'REDACTED'
      };
    } else {
      return {
        success: false,
        error: '邮箱或密码错误'
      };
    }
  } catch (error) {
    console.error('登录请求失败:', error);
    return {
      success: false,
      error: '登录请求失败，请稍后重试'
    };
  }
};

/**
 * 用户注册
 * @param {Object} userData - 用户数据
 * @param {string} userData.username - 用户名
 * @param {string} userData.email - 邮箱
 * @param {string} userData.password - 密码
 * @param {string} userData.nickname - 昵称
 * @returns {Promise<Object>} 注册结果
 */
export const registerUser = async (userData) => {
  try {
    // 在实际项目中，这里应该调用真实的API
    // const response = await request('/auth/register', {
    //   method: 'POST',
    //   body: JSON.stringify(userData),
    // });

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1200));

    // 模拟注册验证
    const existingEmails = ['demo@example.com', 'test@example.com'];
    const existingUsernames = ['demo_user', 'test_user'];

    if (existingEmails.includes(userData.email)) {
      return {
        success: false,
        error: '该邮箱已被注册'
      };
    }

    if (existingUsernames.includes(userData.username)) {
      return {
        success: false,
        error: '该用户名已被占用'
      };
    }

    // 模拟成功注册
    const newUser = {
      id: `user-${Date.now()}`,
      email: userData.email,
      username: userData.username,
      nickname: userData.nickname,
      avatar: null,
      createdAt: new Date().toISOString(),
    };

    return {
      success: true,
      user: newUser,
      preferences: {
        theme: 'light',
        language: 'zh-CN',
        notifications: true,
        soundEnabled: true,
      },
      token: 'REDACTED'
    };
  } catch (error) {
    console.error('注册请求失败:', error);
    return {
      success: false,
      error: '注册请求失败，请稍后重试'
    };
  }
};

/**
 * 用户登出
 * @returns {Promise<Object>} 登出结果
 */
export const logout = async () => {
  try {
    // 在实际项目中，这里应该调用真实的API
    // return request('/auth/logout', {
    //   method: 'POST',
    // });

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));

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
 * 刷新token
 * @returns {Promise<Object>} 新的token
 */
export const refreshToken = async () => {
  try {
    // 在实际项目中，这里应该调用真实的API
    // return request('/auth/refresh', {
    //   method: 'POST',
    // });

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 300));

    return {
      success: true,
      token: 'REDACTED'
    };
  } catch (error) {
    console.error('刷新token失败:', error);
    return {
      success: false,
      error: '刷新token失败'
    };
  }
};

/**
 * 检查登录状态（模拟函数，用于前端测试）
 * @returns {boolean} 是否已登录
 */
export const checkLoginStatus = () => {
  return localStorage.getItem('isLoggedIn') === 'true';
};

/**
 * 验证邮箱是否可用
 * @param {string} email - 邮箱
 * @returns {Promise<Object>} 验证结果
 */
export const checkEmailAvailability = async (email) => {
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));

    const existingEmails = ['demo@example.com', 'test@example.com'];
    
    return {
      success: true,
      available: !existingEmails.includes(email)
    };
  } catch (error) {
    return {
      success: false,
      error: '验证失败'
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
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));

    const existingUsernames = ['demo_user', 'test_user'];
    
    return {
      success: true,
      available: !existingUsernames.includes(username)
    };
  } catch (error) {
    return {
      success: false,
      error: '验证失败'
    };
  }
}; 