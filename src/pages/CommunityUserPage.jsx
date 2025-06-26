import React, { useState, useEffect } from 'react';
import CommunityUserContent from '../components/CommunityUserContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityUserPage.css';
import { Button, Card } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const allTags = ['全部', '情感', '学习', '生活', '健康', '成长'];

// API服务导入注释
// import { getUserInfo } from '../service/userService';
// import { getPostList, createPost } from '../service/communityService';

function CommunityUserPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedTag, setSelectedTag] = useState('全部');

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const tag = params.get('tag');
        if (tag && allTags.includes(tag)) {
            setSelectedTag(tag);
        } else {
            setSelectedTag('全部');
        }
    }, [location.search]);

    return (
        <div className="community-homepage-layout">
            <aside className="sidebar custom-sidebar">
                <Button
                    type="text"
                    style={{ width: '100%', marginBottom: 18, borderRadius: 12, fontWeight: 700, fontSize: 20, color: '#4e9a9a', background: 'none', letterSpacing: 2 }}
                    onClick={() => navigate('/community')}
                >
                    广场
                </Button>
                <Button
                    icon={<UserOutlined />}
                    type="primary"
                    style={{ width: '100%', marginBottom: 24, borderRadius: 12 }}
                    onClick={() => navigate('/community_user_page')}
                >
                    我的帖子
                </Button>
                <div className="sidebar-title">按标签筛选</div>
                <ul className="sidebar-tag-list">
                    {allTags.map(tag => (
                        <li
                            key={tag}
                            className={`sidebar-tag-item${selectedTag === tag ? ' active' : ''}`}
                            onClick={() => {
                                navigate(`/community?tag=${encodeURIComponent(tag)}`);
                            }}
                        >
                            {tag}
                        </li>
                    ))}
                </ul>
            </aside>
            <Card className="card-container">
                <CommunityUserContent selectedTag={selectedTag} />
                <CreatePosts />
            </Card>
        </div>
    );
}

export default CommunityUserPage;