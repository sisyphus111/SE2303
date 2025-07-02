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

  // 获取重定向路径 - 支持三种来源
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
    // 这里的逻辑由用户处理
    console.log('Jaccount登录按钮被点击');
    // 用户将在这里实现Jaccount登录逻辑
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