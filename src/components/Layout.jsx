import React from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {useAudio} from '../lib/GlobalAudioPlayer';
import '../css/Layout.css';
import CommunityHeader from "./CommunityHeader";

const Layout = ({children}) => {
    const location = useLocation();
    const navigate = useNavigate();
    const {current} = useAudio();

    const isCommunityPage = location.pathname.startsWith('/community');

    // 导航菜单项 - 添加心理资源项
    const navItems = [
        {path: '/', label: '个人主页', icon: '🏠'},
        {path: '/community', label: '社区首页', icon: '👥'},
        {path: '/ai_chat', label: 'AI对话', icon: '🤖'},
        {path: '/mental_resource', label: '心理资源', icon: '🎵'},
        {path: '/settings', label: '设置', icon: '⚙️'}
    ];

    const handleNavigation = (path) => {
        navigate(path);
    };

    return (
        <div className="layout-container">
            {/* Header - 顶部导航栏 */}
            <header className="layout-header">
                <div className="header-content">
                    <div className="header-logo">
                        <span className="logo-icon">🧠</span>
                        <span className="logo-text">心理健康助手</span>
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
                </div>
            </header>


            {/*/!* 社区专属头部 - 仅在社区页面显示 *!/*/}
            {/*{isCommunityPage && <CommunityHeader/>}*/}

            {/* Main Content Area */}
            <main className="layout-main">
                <div className="layout-content">
                    {children}
                </div>
            </main>

            {/* Footer - 项目信息 */}
            <footer className={`layout-footer ${current ? 'with-audio-player' : ''}`}>
                <div className="footer-content">
                    <div className="footer-info">
                        <div className="footer-section">
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
                                <a href="#" className="footer-link">📧 redacted@example.invalid</a>
                                <a href="#" className="footer-link">📱 400-123-4567</a>
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