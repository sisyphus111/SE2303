import CommunityHomepage from "./pages/CommunityHomepage";
import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Layout from './components/Layout';
import {Home} from './pages/Home';
import {Diary} from './pages/Diary';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import {AudioProvider} from './lib/GlobalAudioPlayer';
import ArticleDetail from './pages/ArticleDetail';
import PostDetailPage from "./pages/post";
import SendPostPage from "./pages/send_post";
import CommunityUserPage from "./pages/CommunityUserPage";

function App() {
    return (

        <AudioProvider>
            <Router>
                <Layout>
                    <Routes>
                        <Route path="/" element={<Home/>}/>
                        <Route path="/settings" element={<Settings/>}/>
                        <Route path="/mental_resource" element={<MentalResource/>}/>
                        <Route path="/article/:id" element={<ArticleDetail/>}/>
                        <Route path="/diary" element={<Diary/>}/>
                        <Route path="/post/:id" element={<PostDetailPage/>}/>
                        <Route path="/send_post" element={<SendPostPage/>}/>
                        <Route path="/community_home_page" element={<CommunityHomepage/>}/>
                        <Route path="/community_user_page" element={<CommunityUserPage/>}/>
                    </Routes>
                </Layout>
            </Router>
        </AudioProvider>
    );
    }

    export default App;