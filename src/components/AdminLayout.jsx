import React, { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import { logout as authLogout } from '../service/authService';
import { 
  UserOutlined, 
  TeamOutlined, 
  FileTextOutlined, 
  ExclamationCircleOutlined,
  WarningOutlined,
  LogoutOutlined,
  DownOutlined,
  SettingOutlined
} from '@ant-design/icons';
import '../css/AdminLayout.css';

const AdminLayout = ({ children }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const { userProfile, identity, valid, getUserAvatar, getUserDisplayName, clearUser } = useUser();
    const [showUserMenu, setShowUserMenu] = useState(false);
    const userMenuRef = useRef(null);

    // 点击外部关闭用户菜单
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
                setShowUserMenu(false);
            }
        };

        if (showUserMenu) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showUserMenu]);

    // 管理员导航菜单项
    const adminNavItems = [
        { path: '/admin/users', label: '用户管理', icon: <UserOutlined /> },
        { path: '/admin/reports', label: '举报处理', icon: <ExclamationCircleOutlined /> },
        { path: '/admin/review', label: '内容审核', icon: <FileTextOutlined /> },
        { path: '/admin/crisis', label: '危险用户', icon: <WarningOutlined /> }
    ];

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleLogout = async () => {
        try {
            setShowUserMenu(false);
            
            // 立即清除本地用户状态
            clearUser();
            
            // 调用认证服务的登出API
            const result = await authLogout();
            
            if (result.success) {
                console.log('✅ 登出成功');
            } else {
                console.warn('⚠️ 后端登出失败，但前端状态已清除:', result.error);
            }
            
            // 重定向到登录页
            navigate('/login', { replace: true });
            
        } catch (error) {
            console.error('❌ 登出API调用失败:', error);
            navigate('/login', { replace: true });
        }
    };

    const handleUserMenuClick = (action) => {
        setShowUserMenu(false);
        switch (action) {
            case 'logout':
                handleLogout();
                break;
            default:
                break;
        }
    };

    return (
        <div className="admin-layout-container">
            {/* 管理员专用Header */}
            <header className="admin-header">
                <div className="admin-header-content">
                    <div className="admin-logo">
                        <span className="admin-logo-icon">
                            <SettingOutlined />
                        </span>
                        <span className="admin-logo-text">心晴校园 - 管理后台</span>
                    </div>

                    <nav className="admin-nav">
                        {adminNavItems.map((item) => (
                            <button
                                key={item.path}
                                className={`admin-nav-item ${location.pathname === item.path ? 'active' : ''}`}
                                onClick={() => handleNavigation(item.path)}
                            >
                                <span className="admin-nav-icon">{item.icon}</span>
                                <span className="admin-nav-label">{item.label}</span>
                            </button>
                        ))}
                    </nav>

                    {/* 管理员用户区域 */}
                    <div className="admin-user-area">
                        {userProfile && identity === false && valid ? (
                            <div className="admin-user-dropdown" ref={userMenuRef}>
                                <button 
                                    className="admin-user-button"
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                >
                                    <div className="admin-user-avatar">
                                        <UserOutlined />
                                    </div>
                                    <span className="admin-user-name">管理员</span>
                                    <DownOutlined className="admin-dropdown-icon" />
                                </button>
                                
                                {showUserMenu && (
                                    <div className="admin-user-menu">
                                        <button 
                                            className="admin-menu-item admin-logout"
                                            onClick={() => handleUserMenuClick('logout')}
                                        >
                                            <LogoutOutlined />
                                            登出
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div className="admin-auth-error">
                                <span>请重新登录</span>
                            </div>
                        )}
                    </div>
                </div>
            </header>

            {/* 管理员内容区域 */}
            <main className="admin-main">
                <div className="admin-content">
                    {children}
                </div>
            </main>

            {/* 管理员Footer */}
            <footer className="admin-footer">
                <div className="admin-footer-content">
                    <p>&copy; 2024 心晴校园管理后台. 保留所有权利.</p>
                    <p>管理员专用系统</p>
                </div>
            </footer>
        </div>
    );
};

export default AdminLayout; 