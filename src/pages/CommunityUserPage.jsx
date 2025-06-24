import React from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityUserContent from '../components/CommunityUserContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityUserPage.css';

function CommunityUserPage() {
    return (
        <div className="main-content">
            <CommunityHeader />
            <CommunityUserContent />
            <CreatePosts />
        </div>
    );
}

export default CommunityUserPage;