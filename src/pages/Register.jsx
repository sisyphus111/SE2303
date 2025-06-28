import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { 
  UserOutlined, 
  MailOutlined,
  LockOutlined, 
  EyeOutlined, 
  EyeInvisibleOutlined,
  UserAddOutlined,
  SunOutlined,
  CheckCircleOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { registerUser } from '../service/authService';
import '../css/Login.css';

const Register = () => {
  const navigate = useNavigate();
  const { setUser, isAuthenticated, loading, error, clearError } = useUser();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    nickname: '',
  });
  const [formErrors, setFormErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [agreementChecked, setAgreementChecked] = useState(false);

  // 获取重定向路径 - 与Login组件保持一致
  const getRedirectPath = () => {
    // 1. URL查询参数 ?redirect=/profile
    const urlParams = new URLSearchParams(window.location.search);
    const redirectParam = urlParams.get('redirect');
    
    // 2. 默认首页
    return redirectParam || '/';
  };

  // 如果已经登录，重定向到首页
  useEffect(() => {
    if (isAuthenticated && !loading) {
      const redirectPath = getRedirectPath();
      console.log('🔄 用户已登录，重定向到:', redirectPath);
      navigate(redirectPath, { replace: true });
    }
  }, [isAuthenticated, loading, navigate]);

  // 清除错误当组件挂载时
  useEffect(() => {
    clearError();
  }, [clearError]);

  // 表单验证
  const validateForm = () => {
    const errors = {};

    // 用户名验证
    if (!formData.username) {
      errors.username = '请输入用户名';
    } else if (formData.username.length < 3) {
      errors.username = '用户名至少需要3个字符';
    } else if (formData.username.length > 20) {
      errors.username = '用户名不能超过20个字符';
    } else if (!/^[a-zA-Z0-9_]+$/.test(formData.username)) {
      errors.username = '用户名只能包含字母、数字和下划线';
    }

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
    } else if (formData.password.length > 50) {
      errors.password = '密码不能超过50个字符';
    } else if (!/^(?=.*[a-zA-Z])(?=.*\d)/.test(formData.password)) {
      errors.password = '密码必须包含至少一个字母和一个数字';
    }

    // 确认密码验证
    if (!formData.confirmPassword) {
      errors.confirmPassword = '请确认密码';
    } else if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = '两次输入的密码不一致';
    }

    // 昵称验证（可选）
    if (formData.nickname && formData.nickname.length > 30) {
      errors.nickname = '昵称不能超过30个字符';
    }

    // 协议验证
    if (!agreementChecked) {
      errors.agreement = '请阅读并同意用户协议和隐私政策';
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
      // 调用注册API
      const result = await registerUser({
        virtualId: formData.email,
        username: formData.username,
        email: formData.email,
        password: formData.password,
        telephone: "138000000",
        avatar: "1a32d13213"
        // nickname: formData.nickname || formData.username,
      });
      
      if (result.success) {
        // 注册成功，设置用户信息到UserContext
        setUser(result.user, result.preferences);
        
        // 重定向到目标页面
        const redirectPath = getRedirectPath();
        console.log('✅ 注册成功，重定向到:', redirectPath);
        navigate(redirectPath, { replace: true });
      } else {
        // 注册失败，显示错误信息
        setFormErrors({ submit: result.error || '注册失败，请稍后重试' });
      }
    } catch (error) {
      console.error('注册出错:', error);
      setFormErrors({ submit: '注册出错，请稍后重试' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card register-card">
        {/* 页面头部 */}
        <div className="login-header">
          <div className="login-logo">
            <SunOutlined className="logo-icon" />
            <h1 className="logo-text">心晴校园</h1>
          </div>
          <p className="login-subtitle">创建账号，开始您的心理健康之旅</p>
        </div>

        {/* 注册表单 */}
        <form className="login-form" onSubmit={handleSubmit}>
          {/* 全局错误显示 */}
          {(error || formErrors.submit) && (
            <div className="error-message global-error">
              {error || formErrors.submit}
            </div>
          )}

          {/* 用户名输入 */}
          <div className="form-group">
            <label htmlFor="username" className="form-label">用户名 *</label>
            <div className="input-wrapper">
              <UserOutlined className="input-icon" />
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
                className={`form-input ${formErrors.username ? 'error' : ''}`}
                placeholder="请输入用户名（3-20个字符）"
                autoComplete="username"
              />
            </div>
            {formErrors.username && (
              <span className="error-message">{formErrors.username}</span>
            )}
          </div>

          {/* 邮箱输入 */}
          <div className="form-group">
            <label htmlFor="email" className="form-label">邮箱地址 *</label>
            <div className="input-wrapper">
              <MailOutlined className="input-icon" />
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

          {/* 昵称输入 */}
          <div className="form-group">
            <label htmlFor="nickname" className="form-label">昵称（可选）</label>
            <div className="input-wrapper">
              <UserOutlined className="input-icon" />
              <input
                type="text"
                id="nickname"
                name="nickname"
                value={formData.nickname}
                onChange={handleInputChange}
                className={`form-input ${formErrors.nickname ? 'error' : ''}`}
                placeholder="请输入昵称（不超过30个字符）"
                autoComplete="nickname"
              />
            </div>
            {formErrors.nickname && (
              <span className="error-message">{formErrors.nickname}</span>
            )}
          </div>

          {/* 密码输入 */}
          <div className="form-group">
            <label htmlFor="password" className="form-label">密码 *</label>
            <div className="input-wrapper">
              <LockOutlined className="input-icon" />
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                className={`form-input ${formErrors.password ? 'error' }`}
                placeholder="请输入密码（至少6个字符，包含字母和数字）"
                autoComplete="new-password"
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

          {/* 确认密码输入 */}
          <div className="form-group">
            <label htmlFor="confirmPassword" className="form-label">确认密码 *</label>
            <div className="input-wrapper">
              <LockOutlined className="input-icon" />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                className={`form-input ${formErrors.confirmPassword ? 'error' }`}
                placeholder="请再次输入密码"
                autoComplete="new-password"
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />}
              </button>
            </div>
            {formErrors.confirmPassword && (
              <span className="error-message">{formErrors.confirmPassword}</span>
            )}
          </div>

          {/* 协议选项 */}
          <div className="form-options">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={agreementChecked}
                onChange={(e) => {
                  setAgreementChecked(e.target.checked);
                  if (formErrors.agreement) {
                    setFormErrors(prev => ({ ...prev, agreement: '' }));
                  }
                }}
                className="checkbox-input"
              />
              <span className="checkbox-text">
                我已阅读并同意{' '}
                <a href="#" className="agreement-link">用户协议</a>
                {' '}和{' '}
                <a href="#" className="agreement-link">隐私政策</a>
              </span>
            </label>
            {formErrors.agreement && (
              <span className="error-message">{formErrors.agreement}</span>
            )}
          </div>

          {/* 注册按钮 */}
          <button
            type="submit"
            className="login-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <>
                <div className="button-spinner" />
                注册中...
              </>
            ) : (
              <>
                <UserAddOutlined />
                立即注册
              </>
            )}
          </button>
        </form>

        {/* 页面底部 */}
        <div className="login-footer">
          <p className="footer-text">
            已有账号？{' '}
            <Link to="/login" className="footer-link">
              立即登录
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

export default Register; 