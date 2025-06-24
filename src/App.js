import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import AIChat from './pages/AIChat';
import Login from './pages/Login';
import Profile from './pages/Profile';
import Diary from './pages/Diary';
import Community from './pages/Community';
import PostDetail from './pages/PostDetail';
import NewPost from './pages/NewPost';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/ai-chat" element={<AIChat />} />
          <Route path="/login" element={<Login />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/diary" element={<Diary />} />
          <Route path="/community" element={<Community />} />
          <Route path="/community/post/:id" element={<PostDetail />} />
          <Route path="/community/new-post" element={<NewPost />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
