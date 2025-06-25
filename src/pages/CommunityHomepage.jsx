import React, { useState } from 'react';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import { Card, Button, Input } from "antd";
import { useNavigate } from 'react-router-dom';
import { SearchOutlined, UserOutlined } from '@ant-design/icons';

const allTags = ['全部', '情感', '学习', '生活', '健康', '成长'];

function CommunityHomepage() {
    const [selectedTag, setSelectedTag] = useState('全部');
    const [searchValue, setSearchValue] = useState('');
    const navigate = useNavigate();

    // 简单搜索功能（仅前端过滤）
    const handleSearch = (e) => {
        setSearchValue(e.target.value);
    };

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
                <div className="main-content-header">
                    <h1 className="main-content-title">今日心有灵犀</h1>
                </div>
                <div className="main-content-search-bar">
                    <Input
                        placeholder="搜索帖子..."
                        prefix={<SearchOutlined />}
                        value={searchValue}
                        onChange={handleSearch}
                        style={{ borderRadius: 12, margin: '0 auto 24px auto', width: 320, display: 'block' }}
                        allowClear
                    />
                </div>
                <CommunityContent selectedTag={selectedTag} searchValue={searchValue} />
                <CreatePosts />
            </Card>
        </div>
    );
}

export default CommunityHomepage;