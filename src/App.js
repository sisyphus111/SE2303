import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import AIChat from './pages/AIChat';
import Login from './pages/Login';
import Profile from './pages/Profile';
import Diary from './pages/Diary';
import Community from './pages/Community';
import PostDetail from './pages/PostDetail';
import NewPost from './pages/NewPost';
import HomePage from './pages/HomePage';
import './App.css';


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/ai-chat" element={<AIChat />} />
        <Route path="/login" element={<Login />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/diary" element={<Diary />} />
        <Route path="/community" element={<Community />} />
        <Route path="/post/:id" element={<PostDetail />} />
        <Route path="/new-post" element={<NewPost />} />
      </Routes>
    </Router>
  );
}

export default App;
