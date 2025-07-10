import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { 
  LoadingOutlined, 
  CheckCircleOutlined, 
  CloseCircleOutlined,
  SunOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { post } from '../service/common';
import '../css/Login.css';

const JaccountCallback = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { loginSuccess } = useUser();
  
  const [status, setStatus] = useState('loading'); // loading, success, error
  const [message, setMessage] = useState('正在处理登录信息...');

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // 获取Jaccount返回的参数
        const code = searchParams.get('code');
        const error = searchParams.get('error');
        const state = searchParams.get('state');

        console.log('🔍 Jaccount回调参数:', { code: code?.substring(0, 10) + '...', error, state });

        if (error) {
          setStatus('error');
          setMessage('登录被取消或出现错误');
          return;
        }

        if (!code) {
          setStatus('error');
          setMessage('未收到授权码，请重试');
          return;
        }

        setMessage('正在验证授权信息...');

        // 使用common.js的post函数发送请求
        const result = await post('/auth/jaccount/callback', {
          code: code,
          redirectUri: window.location.origin + '/auth/jaccount/callback',
          state: state
        });

        
          setStatus('success');
          setMessage('登录成功，正在跳转...');

          // 调用UserContext的loginSuccess方法设置用户信息
          await loginSuccess(result);
          
          // 延迟跳转，让用户看到成功信息
          setTimeout(() => {
            // 从localStorage获取原始重定向路径
            const redirectPath = localStorage.getItem('loginRedirect') || '/';
            localStorage.removeItem('loginRedirect');
            
            console.log('✅ 登录成功，重定向到:', redirectPath);
            navigate(redirectPath, { replace: true });
          }, 1500);
        
      } catch (error) {
        console.error('❌ Jaccount回调处理出错:', error);
        setStatus('error');
        setMessage(error.message || '网络错误，请重试');
      }
    };

    handleCallback();
  }, [searchParams, loginSuccess, navigate]);

  // 重试登录
  const handleRetry = () => {
    navigate('/login', { replace: true });
  };

  // 根据状态渲染图标
  const renderStatusIcon = () => {
    switch (status) {
      case 'loading':
        return <LoadingOutlined spin className="logo-icon" />;
      case 'success':
        return <CheckCircleOutlined className="logo-icon" style={{ color: '#52c41a' }} />;
      case 'error':
        return <CloseCircleOutlined className="logo-icon" style={{ color: '#ff4d4f' }} />;
      default:
        return <SunOutlined className="logo-icon" />;
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        {/* 页面头部 */}
        <div className="login-header">
          <div className="login-logo">
            {renderStatusIcon()}
            <h1 className="logo-text">心晴校园</h1>
          </div>
          <p className="login-subtitle">{message}</p>
        </div>

        {/* 状态显示区域 */}
        <div className="jaccount-login-section">
          {status === 'loading' && (
            <div className="login-processing">
              <p className="processing-text">
                正在验证您的身份信息，请稍候...
              </p>
            </div>
          )}

          {status === 'success' && (
            <div className="login-success">
              <p className="success-text">
                登录成功！正在为您跳转...
              </p>
            </div>
          )}

          {status === 'error' && (
            <div className="login-error">
              <p className="error-text" style={{ marginBottom: '16px', color: '#ff4d4f' }}>
                {message}
              </p>
              <button
                type="button"
                className="login-button"
                onClick={handleRetry}
              >
                重新登录
              </button>
            </div>
          )}
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

export default JaccountCallback; 