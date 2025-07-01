import React, { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import { logout as authLogout } from '../service/authService';
import { 
  DashboardOutlined, 
  TeamOutlined, 
  FlagOutlined, 
  ExclamationCircleOutlined,
  WarningOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  CrownOutlined,
  LogoutOutlined,
  DownOutlined
} from '@ant-design/icons';
import '../css/Layout.css';
import '../css/AdminLayout.css';

const AdminLayout = ({ children }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const { isAuthenticated, getUserAvatar, getUserDisplayName } = useUser();
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

    // 监听路由变化，自动滚动到顶部
    useEffect(() => {
        const timer = setTimeout(() => {
            const mainElement = document.querySelector('.layout-main');
            if (mainElement) {
                mainElement.scrollIntoView({ behavior: 'auto', block: 'start' });
            }
        }, 50);

        return () => clearTimeout(timer);
    }, [location.pathname]);

    // 管理员导航菜单项
    const adminNavItems = [
        { path: '/admin', label: '管理面板', icon: <DashboardOutlined /> },
        { path: '/admin/users', label: '用户管理', icon: <TeamOutlined /> },
        { path: '/admin/review', label: '内容审核', icon: <FlagOutlined /> },
        { path: '/admin/reports', label: '举报管理', icon: <WarningOutlined /> },
        { path: '/admin/high-risk', label: '危险用户', icon: <ExclamationCircleOutlined /> }
    ];

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleLogout = async () => {
        try {
            const result = await authLogout();
            
            if (result.success) {
                setShowUserMenu(false);
                navigate('/login');
            } else {
                console.error('登出失败:', result.error);
                // 即使API调用失败，也要清除本地状态并跳转
                setShowUserMenu(false);
                navigate('/login');
            }
        } catch (error) {
            console.error('登出失败:', error);
            setShowUserMenu(false);
            navigate('/login');
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
        <div className="layout-container admin-layout">
            {/* Header - 管理员顶部导航栏 */}
            <header className="layout-header admin-header">
                <div className="header-content">
                    <div className="header-logo">
                        <span className="logo-icon">
                            <SafetyCertificateOutlined />
                        </span>
                        <span className="logo-text artistic-logo">心晴校园管理</span>
                    </div>

                    <nav className="header-nav">
                        {adminNavItems.map((item) => (
                            <button
                                key={item.path}
                                className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
                                onClick={() => handleNavigation(item.path)}
                            >
                                <span className="nav-icon">{item.icon}</span>
                                <span className="nav-label">{item.label}</span>
                            </button>
                        ))}
                    </nav>

                    {/* 用户区域 */}
                    <div className="header-user">
                        {isAuthenticated && (
                            <div className="user-dropdown" ref={userMenuRef}>
                                <button 
                                    className="user-button"
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                >
                                    <div className="user-avatar">
                                        <CrownOutlined />
                                    </div>
                                    <span className="user-name">{getUserDisplayName()}</span>
                                    <span className="user-role">(管理员)</span>
                                    <DownOutlined className="dropdown-icon" />
                                </button>
                                
                                {showUserMenu && (
                                    <div className="user-menu">
                                        <button 
                                            className="menu-item logout"
                                            onClick={() => handleUserMenuClick('logout')}
                                        >
                                            <LogoutOutlined />
                                            登出
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </header>

            {/* Main Content Area */}
            <main className="layout-main admin-main">
                <div className="layout-content">
                    {children}
                </div>
            </main>

            {/* Footer - 管理员版本 */}
            <footer className="layout-footer admin-footer">
                <div className="footer-content">
                    <div className="footer-info">
                        <div className="footer-section">
                            <h4>心晴校园管理系统</h4>
                            <p>负责校园心理健康社区的管理与维护</p>
                        </div>

                        <div className="footer-section">
                            <h4>管理功能</h4>
                            <ul>
                                <li>用户管理与监控</li>
                                <li>内容审核与管理</li>
                                <li>危险用户干预</li>
                                <li>系统数据统计</li>
                            </ul>
                        </div>
                    </div>

                    <div className="footer-bottom">
                        <p>&copy; 2024 心晴校园管理系统. 保留所有权利.</p>
                        <p>专业管理，用心服务</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default AdminLayout; 