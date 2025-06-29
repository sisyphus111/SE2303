import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { 
  UserOutlined, 
  LockOutlined, 
  EyeOutlined, 
  EyeInvisibleOutlined,
  LoginOutlined,
  SunOutlined 
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { loginUser } from '../service/authService';
import '../css/Login.css';

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser, isAuthenticated, loading, error, clearError } = useUser();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [formErrors, setFormErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);

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
    if (isAuthenticated && !loading) {
      const redirectPath = getRedirectPath();
      console.log('🔄 用户已登录，重定向到:', redirectPath);
      navigate(redirectPath, { replace: true });
    }
  }, [isAuthenticated, loading, navigate, location]);

  // 清除错误当组件挂载时
  useEffect(() => {
    clearError();
  }, [clearError]);

  // 表单验证
  const validateForm = () => {
    const errors = {};

    // 邮箱验证
    if (!formData.email) {
      errors.email = '请输入邮箱地址';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = '请输入有效的邮箱地址';
    }

    // 密码验证
    if (!formData.password) {
      errors.password = '请输入密码';
    } else if (formData.password.length < 6) {
      errors.password = '密码至少需要6个字符';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // 处理输入变化
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // 清除该字段的错误信息
    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  // 处理表单提交
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    clearError();

    try {
      // 调用认证服务进行登录
      const result = await loginUser(formData.email, formData.password);
      
      if (result.success) {
        // 登录成功，设置用户信息到UserContext
        setUser(result.user, result.preferences);
        
        // 重定向到目标页面
        const redirectPath = getRedirectPath();
        console.log('✅ 登录成功，重定向到:', redirectPath);
        navigate(redirectPath, { replace: true });
      } else {
        // 登录失败，显示错误信息
        setFormErrors({ submit: result.error || '登录失败，请检查邮箱和密码' });
      }
    } catch (error) {
      console.error('登录出错:', error);
      setFormErrors({ submit: '登录出错，请稍后重试' });
    } finally {
      setIsSubmitting(false);
    }
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
          <p className="login-subtitle">欢迎回来，继续您的心理健康之旅</p>
        </div>

        {/* 登录表单 */}
        <form className="login-form" onSubmit={handleSubmit}>
          {/* 全局错误显示 */}
          {(error || formErrors.submit) && (
            <div className="error-message global-error">
              {error || formErrors.submit}
            </div>
          )}

          {/* 邮箱输入 */}
          <div className="form-group">
            <label htmlFor="email" className="form-label">邮箱地址</label>
            <div className="input-wrapper">
              <UserOutlined className="input-icon" />
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                className={`form-input ${formErrors.email ? 'error' : ''}`}
                placeholder="请输入邮箱地址"
                autoComplete="email"
              />
            </div>
            {formErrors.email && (
              <span className="error-message">{formErrors.email}</span>
            )}
          </div>

          {/* 密码输入 */}
          <div className="form-group">
            <label htmlFor="password" className="form-label">密码</label>
            <div className="input-wrapper">
              <LockOutlined className="input-icon" />
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                className={`form-input ${formErrors.password ? 'error' }`}
                placeholder="请输入密码"
                autoComplete="current-password"
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />}
              </button>
            </div>
            {formErrors.password && (
              <span className="error-message">{formErrors.password}</span>
            )}
          </div>

          {/* 记住我选项 */}
          <div className="form-options">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                className="checkbox-input"
              />
              <span className="checkbox-text">记住我</span>
            </label>
          </div>

          {/* 登录按钮 */}
          <button
            type="submit"
            className="login-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <>
                <div className="button-spinner" />
                登录中...
              </>
            ) : (
              <>
                <LoginOutlined />
                登录
              </>
            )}
          </button>
        </form>

        {/* 页面底部 */}
        <div className="login-footer">
          <p className="footer-text">
            还没有账号？{' '}
            <Link to="/register" className="footer-link">
              立即注册
            </Link>
          </p>
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