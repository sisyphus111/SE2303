import React from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';

function CommunityHomepage() {
    return (
        <div className="main-content">
            <CommunityHeader />
            <CommunityContent />
            <CreatePosts />
        </div>
    );
}

export default CommunityHomepage;