import React, { useState, useEffect } from 'react';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import { Card, Button, Input } from "antd";
import { useNavigate, useLocation } from 'react-router-dom';
import { SearchOutlined, UserOutlined } from '@ant-design/icons';

// API服务导入注释
// import { getPostList, createPost } from '../service/communityService';

function CommunityHomepage() {
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedTag, setSelectedTag] = useState('全部');
    const [searchValue, setSearchValue] = useState('');

    // 监听url变化，自动切换标签
    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const tag = params.get('tag');
        if (tag && allTags.includes(tag)) {
            setSelectedTag(tag);
        } else {
            setSelectedTag('全部');
        }
    }, [location.search]);

    // 简单搜索功能（仅前端过滤）
    const handleSearch = (e) => {
        setSearchValue(e.target.value);
    };

    return (
        <div className="community-homepage-layout">
            {/* 侧边栏：广场+tag筛选+我的帖子 */}
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
            {/* 主内容区 */}
            <Card className="card-container">
                <CommunityContent />
                <CreatePosts />
            </Card>
        </div>
    );
}

export default CommunityHomepage;