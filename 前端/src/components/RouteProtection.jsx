import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useUser } from '../lib/UserContext';

// 路由保护组件
const RouteProtection = ({ children, requiredRole = 'user' }) => {
    const { identity, valid, loading, isAuthenticated } = useUser();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // 如果正在加载，不做任何操作
        if (loading) return;

        // 如果用户未认证，重定向到登录页
        if (!isAuthenticated) {
            navigate('/login', { replace: true });
            return;
        }

        // 检查用户身份权限
        const isAdmin = identity === false && valid === true;
        const isUser = identity === true && valid === true;
        
        // 如果需要管理员权限但用户不是管理员
        if (requiredRole === 'admin' && !isAdmin) {
            console.log('🚫 普通用户试图访问管理员页面，重定向到用户首页');
            navigate('/', { replace: true });
            return;
        }

        // 如果需要普通用户权限但用户是管理员
        if (requiredRole === 'user' && !isUser) {
            console.log('🚫 管理员试图访问用户页面，重定向到管理员首页');
            navigate('/admin/users', { replace: true });
            return;
        }

        // 管理员访问根路径时，重定向到用户管理页面
        if (isAdmin && location.pathname === '/') {
            navigate('/admin/users', { replace: true });
            return;
        }

    }, [identity, valid, loading, isAuthenticated, navigate, location.pathname, requiredRole]);

    // 如果正在加载，显示加载状态
    if (loading) {
        return (
            <div style={{ 
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center', 
                height: '100vh',
                fontSize: '16px',
                color: '#666'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ 
                        marginBottom: '16px',
                        fontSize: '24px'
                    }}>
                        ⏳
                    </div>
                    正在验证身份...
                </div>
            </div>
        );
    }

    // 如果用户未认证，显示空白（马上会重定向）
    if (!isAuthenticated) {
        return null;
    }

    // 权限验证通过，渲染子组件
    return children;
};

export default RouteProtection; 