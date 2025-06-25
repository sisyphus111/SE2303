import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
import {Home} from './pages/Home';
import {Diary} from './pages/Diary';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import AIChat from './pages/AIChat';
import {AudioProvider} from './lib/GlobalAudioPlayer';
import ArticleDetail from './pages/ArticleDetail';
import PostDetailPage from "./pages/CommunityPost";
import SendCommunityPostPage from "./pages/SendCommunityPost";
import CommunityUserPage from "./pages/CommunityUserPage";

function App() {
    return (

        <AudioProvider>
            <Router>
                <Layout>
                    <Routes>
                        <Route path="/" element={<Home/>}/>
                        <Route path="/home" element={<Home/>}/>
                        <Route path="/settings" element={<Settings/>}/>
                        <Route path="/mental_resource" element={<MentalResource/>}/>
                        <Route path="/article/:id" element={<ArticleDetail/>}/>
                        <Route path="/diary" element={<Diary/>}/>
                        <Route path="/post/:id" element={<PostDetailPage/>}/>
                        <Route path="/send_community_post" element={<SendCommunityPostPage/>}/>
                        <Route path="/community" element={<CommunityHomepage/>}/>
                        <Route path="/community_user_page" element={<CommunityUserPage/>}/>
                        <Route path="/ai_chat" element={<AIChat/>}/>
                    </Routes>
                </Layout>
            </Router>
        </AudioProvider>
    );
    }

    export default App;