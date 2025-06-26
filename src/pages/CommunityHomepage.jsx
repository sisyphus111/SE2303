import React from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import Layout from "../components/Layout";
import {Card} from "antd";

function CommunityHomepage() {
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