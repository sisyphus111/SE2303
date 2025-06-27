import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useUser } from '../lib/UserContext';

const ProtectedRoute = ({ children, fallback = null }) => {
  const { isAuthenticated, loading } = useUser();
  const location = useLocation();

  // 如果正在加载，显示加载状态
  if (loading) {
    return (
      <div className="loading-container" style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '50vh',
        flexDirection: 'column',
        gap: '16px'
      }}>
        <div className="loading-spinner" style={{
          width: '40px',
          height: '40px',
          border: '3px solid #f3f3f3',
          borderTop: '3px solid var(--primary-green, #2d5016)',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }} />
        <p style={{ color: 'var(--text-gray, #666)', fontSize: '14px' }}>
          正在验证登录状态...
        </p>
        <style>{`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  // 如果已登录，渲染受保护的内容
  if (isAuthenticated) {
    return children;
  }

  // 如果有自定义的fallback组件，使用它
  if (fallback) {
    return fallback;
  }

  // 如果未登录，重定向到登录页面，并保存当前路径用于登录后重定向
  const redirectPath = location.pathname + location.search;
  return <Navigate to="/login" state={{ from: redirectPath }} replace />;
};

export default ProtectedRoute; 