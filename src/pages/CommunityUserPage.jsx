import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Avatar } from 'antd';
import { ArrowLeftOutlined, PlusOutlined, UserOutlined } from '@ant-design/icons';
import CommunityUserContent from '../components/CommunityUserContent';
import UserDisplayName from '../components/UserDisplayName';
import { useUser } from '../lib/UserContext';
import '../css/CommunityUserPage.css';

const CommunityUserPage = () => {
    const navigate = useNavigate();
    const { userProfile, valid, getUserAvatar, getUserDisplayName } = useUser();
    const [avatarError, setAvatarError] = useState(false);

    if (!userProfile || !valid) {
        navigate('/login');
        return null;
    }

    const handleAvatarError = () => {
        setAvatarError(true);
    };

    return (
        <div className="community-user-page">
            <div className="user-page-header">
                <Button
                    type="text"
                    icon={<ArrowLeftOutlined />}
                    onClick={() => navigate(-1)}
                    className="back-btn"
                >
                    返回
                </Button>
                <div className="user-info">
                    {avatarError ? (
                        <Avatar 
                            size={80} 
                            icon={<UserOutlined />} 
                            className="user-avatar community-user-avatar"
                        />
                    ) : (
                        <img
                            src={getUserAvatar(userProfile.userId)}
                            alt={getUserDisplayName(userProfile.userId)}
                            className="user-avatar community-user-avatar"
                            onError={handleAvatarError}
                        />
                    )}
                    <div className="user-details">
                        <h2>
                            <UserDisplayName userId={userProfile.userId} />
                        </h2>
                        <p>欢迎回来！查看你的帖子、点赞和收藏</p>
                    </div>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => navigate('/send_community_post')}
                    className="create-post-btn"
                >
                    发布新帖子
                </Button>
            </div>
            <CommunityUserContent />
        </div>
    );
};

export default CommunityUserPage;