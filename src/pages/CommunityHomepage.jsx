import React, { useState } from 'react';
import { Card, Button } from "antd";
import { UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import CommunityHeader from '../pages/CommunityHeader.jsx';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import Layout from "../components/Layout";

// API服务导入注释
// import { getPostList, createPost } from '../service/communityService';

function CommunityHomepage() {
    const navigate = useNavigate();
    const [selectedTag, setSelectedTag] = useState('全部');
    
    // 标签列表
    const allTags = ['全部', '心情分享', '压力管理', '心理健康', '资源推荐', '互助交流'];

    return (
        <div className="community-homepage-layout">
            {/* 侧边栏：tag筛选+我的帖子 */}
            <aside className="sidebar custom-sidebar">
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
                            onClick={() => setSelectedTag(tag)}
                        >
                            {tag}
                        </li>
                    ))}
                </ul>
            </aside>
            {/* 主内容区 */}
            <Card className="card-container">
                <CommunityContent />
                <CreatePosts />
            </Card>
        </div>
    );
}

export default CommunityHomepage;