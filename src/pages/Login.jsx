import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Login.css';

const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // 清除错误信息
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.username.trim() || !formData.password.trim()) {
      setError('请填写完整的登录信息');
      return;
    }

    setIsLoading(true);
    setError('');

    // 模拟登录请求
    try {
      // 这里可以替换为真实的API调用
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // 模拟登录成功
      if (formData.username === 'admin' && formData.password === '123456') {
        // 存储登录状态
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', formData.username);
        navigate('/profile');
      } else {
        setError('用户名或密码错误，请重试');
      }
    } catch (err) {
      setError('登录失败，请稍后重试');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="logo">
            <span>💙</span>
          </div>
          <h1>欢迎回来</h1>
          <p>登录您的心理健康社区账户</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <div className="input-group">
            <label htmlFor="username">用户名</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleInputChange}
              placeholder="请输入用户名"
              disabled={isLoading}
              required
            />
          </div>

          <div className="input-group">
            <label htmlFor="password">密码</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleInputChange}
              placeholder="请输入密码"
              disabled={isLoading}
              required
            />
          </div>

          <div className="form-options">
            <label className="checkbox-label">
              <input type="checkbox" />
              <span>记住我</span>
            </label>
            <a href="#" className="forgot-password">忘记密码？</a>
          </div>

          <button 
            type="submit" 
            className="login-button"
            disabled={isLoading}
          >
            {isLoading ? (
              <div className="loading-spinner">
                <span></span>
                <span></span>
                <span></span>
              </div>
            ) : (
              '登录'
            )}
          </button>
        </form>

        <div className="login-footer">
          <p>还没有账户？</p>
          <button 
            className="register-link"
            onClick={() => navigate('/register')}
          >
            立即注册
          </button>
        </div>

        <div className="demo-info">
          <p>演示账户：</p>
          <p>用户名：admin | 密码：123456</p>
        </div>
      </div>
    </div>
  );
};

export default Login; 