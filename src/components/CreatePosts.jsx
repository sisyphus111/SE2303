import React from 'react';
import '../css/CreatePosts.css';
import { useNavigate } from 'react-router-dom';

function CreatePosts() {
    const navigate = useNavigate();
    return (
        <button
            className="floating-create-post-btn"
            onClick={() => navigate('/send_community_post')}
        >
            发个帖子吧
        </button>
    );
}

export default CreatePosts;