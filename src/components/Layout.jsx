import React, { useState, useEffect, useRef } from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {useAudio} from '../lib/GlobalAudioPlayer';
import { useUser } from '../lib/UserContext';
import { 
  HomeOutlined, 
  TeamOutlined, 
  RobotOutlined, 
  HeartOutlined, 
  SettingOutlined,
  SunOutlined,
  MailOutlined,
  PhoneOutlined,
  UserOutlined,
  LogoutOutlined,
  LoginOutlined,
  DownOutlined
} from '@ant-design/icons';
import '../css/Layout.css';
// import CommunityHeader from "./CommunityHeader";

const Layout = ({children}) => {
    const location = useLocation();
    const navigate = useNavigate();
    const {current} = useAudio();
    const { isAuthenticated, user, getUserAvatar, getUserDisplayName, logout } = useUser();
    const [showUserMenu, setShowUserMenu] = useState(false);
    const userMenuRef = useRef(null);

    const isCommunityPage = location.pathname.startsWith('/community');

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

    // 导航菜单项 - 使用 Ant Design 图标
    const navItems = [
        {path: '/', label: '个人主页', icon: <HomeOutlined />},
        {path: '/community', label: '社区首页', icon: <TeamOutlined />},
        {path: '/ai_chat', label: 'AI对话', icon: <RobotOutlined />},
        {path: '/mental_resource', label: '心理资源', icon: <HeartOutlined />}
    ];

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleLogout = async () => {
        try {
            await logout();
            setShowUserMenu(false);
            navigate('/login');
        } catch (error) {
            console.error('登出失败:', error);
        }
    };

    const handleUserMenuClick = (action) => {
        setShowUserMenu(false);
        switch (action) {
            case 'profile':
                navigate('/profile');
                break;
            case 'settings':
                navigate('/settings');
                break;
            case 'logout':
                handleLogout();
                break;
            default:
                break;
        }
    };

    return (
        <div className="layout-container">
            {/* Header - 顶部导航栏 */}
            <header className="layout-header">
                <div className="header-content">
                    <div className="header-logo">
                        <span className="logo-icon">
                            <SunOutlined />
                        </span>
                        <span className="logo-text artistic-logo">心晴校园</span>
                    </div>

                    <nav className="header-nav">
                        {navItems.map((item) => (
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
                        {isAuthenticated ? (
                            <div className="user-dropdown" ref={userMenuRef}>
                                <button 
                                    className="user-button"
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                >
                                    <div className="user-avatar">
                                        {typeof getUserAvatar() === 'string' && getUserAvatar().length === 1 ? (
                                            <span className="avatar-text">{getUserAvatar()}</span>
                                        ) : (
                                            <UserOutlined />
                                        )}
                                    </div>
                                    <span className="user-name">{getUserDisplayName()}</span>
                                    <DownOutlined className="dropdown-icon" />
                                </button>
                                
                                {showUserMenu && (
                                    <div className="user-menu">
                                        <button 
                                            className="menu-item"
                                            onClick={() => handleUserMenuClick('profile')}
                                        >
                                            <UserOutlined />
                                            个人资料
                                        </button>
                                        <button 
                                            className="menu-item"
                                            onClick={() => handleUserMenuClick('settings')}
                                        >
                                            <SettingOutlined />
                                            设置
                                        </button>
                                        <div className="menu-divider"></div>
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
                        ) : (
                            <div className="auth-buttons">
                                <button 
                                    className="auth-btn login-btn"
                                    onClick={() => navigate('/login')}
                                >
                                    <LoginOutlined />
                                    登录
                                </button>
                                <button 
                                    className="auth-btn register-btn"
                                    onClick={() => navigate('/register')}
                                >
                                    <UserOutlined />
                                    注册
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </header>


            {/*/!* 社区专属头部 - 仅在社区页面显示 *!/*/}
            {/*{isCommunityPage && <CommunityHeader/>}*/}

            {/* Main Content Area */}
            <main className="layout-main">
                <div className="layout-content leaf-decoration">
                    {children}
                </div>
            </main>

            {/* Footer - 项目信息 */}
            <footer className={`layout-footer ${current ? 'with-audio-player' : ''}`}>
                <div className="footer-content">
                    <div className="footer-info">
                        <div className="footer-section flower-decoration">
                            <h4>心理健康助手</h4>
                            <p>关注您的心理健康，提供专业的情绪管理和心理支持服务</p>
                        </div>

                        <div className="footer-section">
                            <h4>功能特色</h4>
                            <ul>
                                <li>个人情绪管理</li>
                                <li>社区互助支持</li>
                                <li>AI智能对话</li>
                                <li>心理健康资源</li>
                                <li>个性化设置</li>
                            </ul>
                        </div>

                        <div className="footer-section">
                            <h4>技术支持</h4>
                            <ul>
                                <li>React 18</li>
                                <li>现代化UI设计</li>
                                <li>响应式布局</li>
                                <li>PWA支持</li>
                            </ul>
                        </div>

                        <div className="footer-section">
                            <h4>联系我们</h4>
                            <p>如有问题或建议，请联系开发团队</p>
                            <div className="footer-links">
                                <a href="#" className="footer-link">
                                    <MailOutlined /> redacted@example.invalid
                                </a>
                                <a href="#" className="footer-link">
                                    <PhoneOutlined /> 400-123-4567
                                </a>
                            </div>
                        </div>
                    </div>

                    <div className="footer-bottom">
                        <p>&copy; 2024 心理健康助手. 保留所有权利.</p>
                        <p>专注心理健康，关爱每一个人</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Layout;