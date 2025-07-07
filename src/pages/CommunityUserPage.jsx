import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from 'antd';
import { ArrowLeftOutlined, PlusOutlined } from '@ant-design/icons';
import CommunityUserContent from '../components/CommunityUserContent';
import { useUser } from '../lib/UserContext';
import '../css/CommunityUserPage.css';

const CommunityUserPage = () => {
    const navigate = useNavigate();
    const { userProfile, valid, getUserAvatar, getUserDisplayName } = useUser();

    if (!userProfile || !valid) {
        navigate('/login');
        return null;
    }

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
                    <img
                        src={getUserAvatar(userProfile.id)}
                        alt={getUserDisplayName(userProfile.id)}
                        className="user-avatar"
                    />
                    <div className="user-details">
                        <h2>{getUserDisplayName(userProfile.id)}</h2>
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