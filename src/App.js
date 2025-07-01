import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
import AdminLayout from './components/AdminLayout';
import {Home} from './pages/Home';
import {Diary} from './pages/Diary';
import Login from './pages/Login';
import Register from './pages/Register';
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
import AdminDashboard from "./pages/AdminDashboard";
import UserManagement from "./pages/UserManagement";
import ContentReview from "./pages/ContentReview";
import ReportManagement from "./pages/ReportManagement";
import HighRiskUsers from "./pages/HighRiskUsers";
import DevIdentitySwitcher from "./components/DevIdentitySwitcher";

// 内部路由组件，根据用户身份显示不同布局
const AppRoutes = () => {
    const { identity } = useUser();
    
    // 管理员界面
    if (identity === 'admin' || identity === 'school') {
        return (
            <AdminLayout>
                <Routes>
                    <Route path="/admin" element={<AdminDashboard/>}/>
                    <Route path="/admin/users" element={<UserManagement/>}/>
                    <Route path="/admin/review" element={<ContentReview/>}/>
                    <Route path="/admin/reports" element={<ReportManagement/>}/>
                    <Route path="/admin/high-risk" element={<HighRiskUsers/>}/>
                    {/* 默认重定向到管理面板 */}
                    <Route path="/" element={<AdminDashboard/>}/>
                    <Route path="/*" element={<AdminDashboard/>}/>
                </Routes>
            </AdminLayout>
        );
    }
    
    // 普通用户界面
    return (
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
            </Routes>
        </Layout>
    );
};

function App() {
    return (
        <UserProvider>
            <AudioProvider>
                <Router>
                    <Routes>
                        {/* 公开路由 - 登录和注册页面 */}
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        
                        {/* 应用路由 - 根据用户身份显示不同界面 */}
                        <Route path="/*" element={<AppRoutes/>}/>
                    </Routes>
                    {/* 开发环境身份切换工具 */}
                    <DevIdentitySwitcher />
                </Router>
            </AudioProvider>
        </UserProvider>
    );
}

export default App;