import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import { AudioProvider } from './lib/GlobalAudioPlayer';
import ArticleDetail from './pages/ArticleDetail';

function App() {
  return (
    <AudioProvider>
      <Router>
          <Routes>
            <Route path="/" element={<h1>Home Page</h1>} />
            <Route path="/settings" element={<Settings/>}/>
            <Route path="/mentalresource" element={<MentalResource/>}/>
            <Route path="/article/:id" element={<ArticleDetail/>}/>
          </Routes>
      </Router>
    </AudioProvider>

  );
}

export default App;
