import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  LoginOutlined,
  SunOutlined 
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import '../css/Login.css';

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, loading } = useUser();

  // 获取重定向路径 - 支持多种来源
  const getRedirectPath = () => {
    // 1. URL查询参数 ?redirect=/profile
    const urlParams = new URLSearchParams(location.search);
    const redirectParam = urlParams.get('redirect');
    
    // 2. React Router state (从ProtectedRoute或其他地方传来)
    const stateFrom = location.state?.from;
    
    // 3. 默认首页
    return redirectParam || stateFrom || '/';
  };

  // 如果已经登录，重定向到目标页面
  useEffect(() => {
    // 添加小延迟，避免在登出过程中的状态清除时出现闪烁
    const timer = setTimeout(() => {
      if (isAuthenticated && !loading) {
        const redirectPath = getRedirectPath();
        console.log('🔄 用户已登录，重定向到:', redirectPath);
        navigate(redirectPath, { replace: true });
      }
    }, 100); // 100ms延迟，足够状态更新

    return () => clearTimeout(timer);
  }, [isAuthenticated, loading, navigate, location]);

  // 处理Jaccount登录
  const handleJaccountLogin = () => {
    console.log('🚀 Jaccount登录按钮被点击');

    // 保存当前要重定向的路径到localStorage，以便回调页面使用
    const redirectPath = getRedirectPath();
    if (redirectPath !== '/') {
      localStorage.setItem('loginRedirect', redirectPath);
      console.log('💾 保存重定向路径到localStorage:', redirectPath);
    }

    // 构建Jaccount OAuth URL，redirect_uri指向专门的回调页面
    const jaccountConfig = {
      response_type: 'code',
      scope: 'profile', // 经测试，选用'basic'或'essential'会报错invalid scope
      client_id: 'REDACTED',
      redirect_uri: window.location.origin + '/auth/jaccount/callback',
      state: Math.random().toString(36).substring(2, 15) // 防CSRF攻击
    };

    const authUrl = new URL('http://jaccount.sjtu.edu.cn/oauth2/authorize');
    Object.entries(jaccountConfig).forEach(([key, value]) => {
      authUrl.searchParams.append(key, value);
    });

    console.log('🔗 跳转到Jaccount授权页面:', authUrl.toString());
    window.location.href = authUrl.toString();
  };

  return (
    <div className="login-container">
      <div className="login-card">
        {/* 页面头部 */}
        <div className="login-header">
          <div className="login-logo">
            <SunOutlined className="logo-icon" />
            <h1 className="logo-text">心晴校园</h1>
          </div>
          <p className="login-subtitle">欢迎来到心晴校园，请使用Jaccount登录</p>
        </div>

        {/* Jaccount登录区域 */}
        <div className="jaccount-login-section">
          <div className="jaccount-info">
            <p className="jaccount-description">
              使用上海交通大学统一身份认证系统登录
            </p>
          </div>

          {/* Jaccount登录按钮 */}
          <button
            type="button"
            className="login-button jaccount-button"
            onClick={handleJaccountLogin}
          >
            <LoginOutlined />
            Jaccount登录
          </button>

          <div className="login-notice">
            <p className="notice-text">
              首次登录将自动为您创建账号
            </p>
          </div>
        </div>
      </div>

      {/* 背景装饰 */}
      <div className="login-background">
        <div className="bg-shape shape-1"></div>
        <div className="bg-shape shape-2"></div>
        <div className="bg-shape shape-3"></div>
      </div>
    </div>
  );
};

export default Login; 