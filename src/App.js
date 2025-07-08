import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
import AdminLayout from './components/AdminLayout';
import RouteProtection from './components/RouteProtection';
import {Home} from './pages/Home';
import {Diary} from './pages/Diary';
import Login from './pages/Login';
import JaccountCallback from './pages/JaccountCallback';
import Profile from './pages/Profile';
import './App.css';
import 'antd/dist/reset.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import AIChat from './pages/AIChat';
import {AudioProvider} from './lib/GlobalAudioPlayer';
import {UserProvider, useUser} from './lib/UserContext';
import ArticleDetail from './pages/ArticleDetail';
import {DiaryDetail} from './pages/DiaryDetail';
import PostDetailPage from "./pages/CommunityPost";
import SendCommunityPostPage from "./pages/SendCommunityPost";
import CommunityUserPage from "./pages/CommunityUserPage";
import AdminUsers from "./pages/AdminUsers";
import AdminPosts from "./pages/AdminPosts";
import AdminCheckins from "./pages/AdminCheckins";
import AdminReports from "./pages/AdminReports";
import AdminReview from "./pages/AdminReview";
import AdminCrisis from "./pages/AdminCrisis";
import AdminMentalResource from "./pages/AdminMentalResource";
import NotificationPage from "./pages/NotificationPage";
import TestNotificationDropdown from "./pages/TestNotificationDropdown";
import NotificationTester from "./pages/NotificationTester";

// 管理员路由组件
const AdminRoutes = () => {
    return (
        <RouteProtection requiredRole="admin">
            <AdminLayout>
                <Routes>
                    <Route path="/" element={<AdminUsers/>}/>
                    <Route path="/admin/users" element={<AdminUsers/>}/>
                    <Route path="/admin/posts" element={<AdminPosts/>}/>
                    <Route path="/admin/checkins" element={<AdminCheckins/>}/>
                    <Route path="/admin/reports" element={<AdminReports/>}/>
                    <Route path="/admin/review" element={<AdminReview/>}/>
                    <Route path="/admin/crisis" element={<AdminCrisis/>}/>
                    <Route path="/admin/mental-resource" element={<AdminMentalResource/>}/>
                    {/* 重定向所有其他路径到用户管理 */}
                    <Route path="*" element={<AdminUsers/>}/>
                </Routes>
            </AdminLayout>
        </RouteProtection>
    );
};

// 普通用户路由组件
const UserRoutes = () => {
    return (
        <RouteProtection requiredRole="user">
            <Layout>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/home" element={<Home/>}/>
                    <Route path="/profile" element={<Profile/>}/>
                    <Route path="/settings" element={<Settings/>}/>
                    <Route path="/mental_resource" element={<MentalResource/>}/>
                    <Route path="/article/:id" element={<ArticleDetail/>}/>
                    <Route path="/diary" element={<Diary/>}/>
                    <Route path="/diary/:id" element={<DiaryDetail/>}/>
                    <Route path="/post/:id" element={<PostDetailPage/>}/>
                    <Route path="/send_community_post" element={<SendCommunityPostPage/>}/>
                    <Route path="/community" element={<CommunityHomepage/>}/>
                    <Route path="/community_user_page" element={<CommunityUserPage/>}/>
                    <Route path="/ai_chat" element={<AIChat/>}/>
                    {/* 管理员路径对普通用户不可访问 */}
                    <Route path="/admin/*" element={<Home/>}/>
                </Routes>
            </Layout>
        </RouteProtection>
    );
};

// 根据用户身份渲染不同的路由
const AppRoutes = () => {
    const { identity, valid, loading } = useUser();
    
    // 加载中状态
    if (loading) {
        return (
            <div style={{ 
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center', 
                height: '100vh',
                fontSize: '16px',
                background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ 
                        marginBottom: '16px',
                        fontSize: '32px'
                    }}>
                        🌟
                    </div>
                    <div style={{ color: '#333', fontWeight: '500' }}>
                        正在加载心晴校园...
                    </div>
                </div>
            </div>
        );
    }
    
    // 管理员界面 (identity === false)
    if (identity === false && valid) {
        return <AdminRoutes />;
    }
    
    // 普通用户界面 (identity === true)
    if (identity === true && valid) {
        return <UserRoutes />;
    }
    
    // 未授权或无效用户，重定向到登录页
    return (
        <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            height: '100vh',
            fontSize: '16px',
            background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
        }}>
            <div style={{ textAlign: 'center' }}>
                <div style={{ 
                    marginBottom: '16px',
                    fontSize: '32px'
                }}>
                    🔐
                </div>
                <div style={{ color: '#333', fontWeight: '500' }}>
                    用户身份验证失败，请重新登录
                </div>
            </div>
        </div>
    );
};

function App() {
    return (
        <UserProvider>
            <AudioProvider>
                <Router>
                    <Routes>
                        {/* 公开路由 - Jaccount登录相关页面 */}
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/auth/jaccount/callback" element={<JaccountCallback/>}/>
                        
                        {/* 应用路由 - 根据用户身份显示不同界面 */}
                        <Route path="/*" element={<AppRoutes/>}/>
                    </Routes>
                </Router>
            </AudioProvider>
        </UserProvider>
    );
}

export default App;