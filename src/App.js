import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Settings from './pages/Settings';
import MentalResource from './pages/MentalResource';

function App() {
  return (
    <Router>
        <Routes>
          <Route path="/" element={<h1>Home Page</h1>} />
          <Route path="/settings" element={<Settings/>}/>
          <Route path="/mentalresource" element={<MentalResource/>}/>
        </Routes>
    </Router>
  );
}

export default App;
