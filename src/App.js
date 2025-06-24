import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
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
        <Layout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/settings" element={<Settings/>}/>
            <Route path="/mentalresource" element={<MentalResource/>}/>
            <Route path="/diary" element={<Diary />} />
          </Routes>
        </Layout>
      </Router>
    </AudioProvider>

  );
}

export default App;
