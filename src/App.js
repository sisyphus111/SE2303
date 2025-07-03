import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
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
import {UserProvider} from './lib/UserContext';
import ArticleDetail from './pages/ArticleDetail';
import {DiaryDetail} from './pages/DiaryDetail';
import PostDetailPage from "./pages/CommunityPost";
import SendCommunityPostPage from "./pages/SendCommunityPost";
import CommunityUserPage from "./pages/CommunityUserPage";

function App() {
    return (
        <UserProvider>
            <AudioProvider>
                <Router>
                    <Routes>
                        {/* 公开路由 - Jaccount登录相关页面 */}
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/auth/jaccount/callback" element={<JaccountCallback/>}/>
                        
                        {/* 应用路由 - 依赖401状态码进行认证控制 */}
                        <Route path="/*" element={
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
                        }/>
                    </Routes>
                </Router>
            </AudioProvider>
        </UserProvider>
    );
}

export default App;