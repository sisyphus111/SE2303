import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import CommunityHomepage from './pages/CommunityHomepage.jsx';
import CommunityUserPage from './pages/CommunityUserPage.jsx';

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/" element={<Navigate to="/CommunityHomepage" replace />} />
                    <Route path="/CommunityHomepage" element={<CommunityHomepage/>}/>
                    <Route path="/CommunityUserpage" element={<CommunityUserPage/>}/>
                </Routes>
          </div>
        </Router>
    );
}

export default App;
