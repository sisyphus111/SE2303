import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Home } from './pages/Home';
import { Diary } from './pages/Diary';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';
import { AudioProvider } from './lib/GlobalAudioPlayer';

function App() {
  return (
    <AudioProvider>
      <Router>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/settings" element={<Settings/>}/>
            <Route path="/mentalresource" element={<MentalResource/>}/>
            <Route path="/diary" element={<Diary />} />
          </Routes>
      </Router>
    </AudioProvider>

  );
}

export default App;
