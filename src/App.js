import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import {Home} from './pages/Home';
import {Diary} from './pages/Diary';
import Login from './pages/Login';
import Register from './pages/Register';
import Profile from './pages/Profile';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import AIChat from './pages/AIChat';
import {AudioProvider} from './lib/GlobalAudioPlayer';
import {UserProvider} from './lib/UserContext';
import ArticleDetail from './pages/ArticleDetail';
import PostDetailPage from "./pages/CommunityPost";
import SendCommunityPostPage from "./pages/SendCommunityPost";
import CommunityUserPage from "./pages/CommunityUserPage";

function App() {
    return (
        <UserProvider>
            <AudioProvider>
                <Router>
                    <Routes>
                        {/* 公开路由 - 登录和注册页面 */}
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        
                        {/* 受保护的路由 - 需要登录才能访问 */}
                        <Route path="/*" element={
                            <Layout>
                                <Routes>
                                    <Route path="/" element={
                                        <ProtectedRoute>
                                            <Home/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/home" element={
                                        <ProtectedRoute>
                                            <Home/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/profile" element={
                                        <ProtectedRoute>
                                            <Profile/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/settings" element={
                                        <ProtectedRoute>
                                            <Settings/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/mental_resource" element={
                                        <ProtectedRoute>
                                            <MentalResource/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/article/:id" element={
                                        <ProtectedRoute>
                                            <ArticleDetail/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/diary" element={
                                        <ProtectedRoute>
                                            <Diary/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/post/:id" element={
                                        <ProtectedRoute>
                                            <PostDetailPage/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/send_community_post" element={
                                        <ProtectedRoute>
                                            <SendCommunityPostPage/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/community" element={
                                        <ProtectedRoute>
                                            <CommunityHomepage/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/community_user_page" element={
                                        <ProtectedRoute>
                                            <CommunityUserPage/>
                                        </ProtectedRoute>
                                    }/>
                                    <Route path="/ai_chat" element={
                                        <ProtectedRoute>
                                            <AIChat/>
                                        </ProtectedRoute>
                                    }/>
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