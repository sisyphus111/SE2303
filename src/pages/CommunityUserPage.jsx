import React, { useState, useEffect } from 'react';
import { Button, Card, Typography, Space, Divider } from 'antd';
import { UserOutlined, TeamOutlined, TagOutlined, PlusOutlined, FilterOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import CommunityUserContent from '../components/CommunityUserContent.jsx';
import '../css/CommunityUserPage.css';

const { Title } = Typography;

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
        <div className="user-page-container">
            {/* 页面头部 */}
            <div className="user-page-header">
                <div className="user-page-header-content">
                    <div className="user-page-title-section">
                        <UserOutlined className="user-page-main-icon" />
                        <Title level={1} className="user-page-main-title">
                            我的帖子
                        </Title>
                        <p className="user-page-subtitle">
                            管理和查看你发布的所有帖子内容
                        </p>
                    </div>
                </div>
            </div>

            {/* 主要内容区域 */}
            <div className="user-page-content">
                <div className="user-page-layout">
                    {/* 左侧边栏 */}
                    <aside className="user-page-sidebar">
                        <Card className="sidebar-card">
                            <div className="sidebar-section">
                                <Title level={4} className="sidebar-title">
                                    <TeamOutlined className="sidebar-icon" />
                                    导航
                                </Title>
                                <Space direction="vertical" className="sidebar-nav">
                                    <Button
                                        type="text"
                                        icon={<TeamOutlined />}
                                        className="nav-btn"
                                        onClick={() => navigate('/community')}
                                    >
                                        社区广场
                                    </Button>
                                    <Button
                                        type="primary"
                                        icon={<UserOutlined />}
                                        className="nav-btn active"
                                        onClick={() => navigate('/community_user_page')}
                                    >
                                        我的帖子
                                    </Button>
                                    <Button
                                        type="text"
                                        icon={<PlusOutlined />}
                                        className="nav-btn"
                                        onClick={() => navigate('/send_community_post')}
                                    >
                                        发布帖子
                                    </Button>
                                </Space>
                            </div>

                            <Divider />

                            <div className="sidebar-section">
                                <Title level={4} className="sidebar-title">
                                    <FilterOutlined className="sidebar-icon" />
                                    标签筛选
                                </Title>
                                <div className="tag-filter-list">
                                    {allTags.map(tag => (
                                        <Button
                                            key={tag}
                                            type={selectedTag === tag ? "primary" : "text"}
                                            size="small"
                                            icon={<TagOutlined />}
                                            className={`tag-filter-btn ${selectedTag === tag ? 'active' : ''}`}
                                            onClick={() => {
                                                setSelectedTag(tag);
                                                const url = tag === '全部' 
                                                    ? '/community_user_page' 
                                                    : `/community_user_page?tag=${encodeURIComponent(tag)}`;
                                                navigate(url);
                                            }}
                                        >
                                            {tag}
                                        </Button>
                                    ))}
                                </div>
                            </div>
                        </Card>
                    </aside>

                    {/* 主内容区域 */}
                    <main className="user-page-main">
                        <Card className="main-content-card">
                            <CommunityUserContent selectedTag={selectedTag} />
                        </Card>
                    </main>
                </div>
            </div>
        </div>
    );
}

export default CommunityUserPage;