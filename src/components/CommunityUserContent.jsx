import React, { useState } from 'react';
import { Typography, Input, Space, Empty, Statistic, Row, Col, Badge } from 'antd';
import { SearchOutlined, FileTextOutlined, HeartOutlined, EyeOutlined } from '@ant-design/icons';
import PostCard from './CommunityPostCard.jsx';
import '../css/CommunityUserContent.css';

const { Title, Text } = Typography;

// 假数据：个人帖子（增加更多信息）
const userPosts = [
    {
        id: 1,
        tags: ['生活', '成长'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '今天的心情不错！分享一些生活中的小确幸和成长感悟',
        likes: 24,
        views: 156,
        comments: 8,
        createdAt: '2024-01-15',
    },
    {
        id: 2,
        tags: ['学习', '健康'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: 'React真的很有趣，学习前端开发的心得体会',
        likes: 18,
        views: 89,
        comments: 5,
        createdAt: '2024-01-12',
    },
    {
        id: 3,
        tags: ['情感', '生活'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '周末去爬山啦！大自然真的是最好的治愈师',
        likes: 32,
        views: 203,
        comments: 12,
        createdAt: '2024-01-10',
    },
    {
        id: 4,
        tags: ['健康', '成长'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '坚持运动一个月的变化，身心都更健康了',
        likes: 45,
        views: 287,
        comments: 15,
        createdAt: '2024-01-08',
    },
];

function CommunityUserContent({ selectedTag }) {
    const [searchKeyword, setSearchKeyword] = useState('');

    // 筛选帖子
    const filteredPosts = userPosts.filter(post => {
        const tagMatch = !selectedTag || selectedTag === '全部' || post.tags.includes(selectedTag);
        const keywordMatch = !searchKeyword || 
            post.summary.toLowerCase().includes(searchKeyword.toLowerCase()) ||
            post.tags.some(tag => tag.toLowerCase().includes(searchKeyword.toLowerCase()));
        return tagMatch && keywordMatch;
    });

    // 计算统计数据
    const totalPosts = userPosts.length;
    const totalLikes = userPosts.reduce((sum, post) => sum + post.likes, 0);
    const totalViews = userPosts.reduce((sum, post) => sum + post.views, 0);

    return (
        <div className="user-content">
            {/* 页面标题和搜索 */}
            <div className="user-content-header">
                <div className="title-section">
                    <Title level={2} className="content-title">
                        <FileTextOutlined className="title-icon" />
                        我的帖子
                        <Badge count={totalPosts} className="post-count-badge" />
                    </Title>
                    <Text className="content-description">
                        管理你的所有帖子，追踪互动数据
                    </Text>
                </div>
                
                <div className="search-section">
                    <Input
                        placeholder="搜索我的帖子..."
                        prefix={<SearchOutlined />}
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                        className="post-search-input"
                        allowClear
                        size="large"
                    />
                </div>
            </div>

            {/* 统计信息 */}
            <div className="stats-section">
                <Row gutter={16}>
                    <Col xs={8} sm={8}>
                        <div className="stat-card">
                            <Statistic
                                title="总帖子数"
                                value={totalPosts}
                                prefix={<FileTextOutlined />}
                                valueStyle={{ color: 'var(--primary-green)' }}
                            />
                        </div>
                    </Col>
                    <Col xs={8} sm={8}>
                        <div className="stat-card">
                            <Statistic
                                title="总点赞数"
                                value={totalLikes}
                                prefix={<HeartOutlined />}
                                valueStyle={{ color: 'var(--accent-green)' }}
                            />
                        </div>
                    </Col>
                    <Col xs={8} sm={8}>
                        <div className="stat-card">
                            <Statistic
                                title="总浏览数"
                                value={totalViews}
                                prefix={<EyeOutlined />}
                                valueStyle={{ color: 'var(--primary-green)' }}
                            />
                        </div>
                    </Col>
                </Row>
            </div>

            {/* 帖子列表 */}
            <div className="posts-section">
                {filteredPosts.length === 0 ? (
                    <div className="empty-state">
                        <Empty
                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                            description={
                                searchKeyword || selectedTag !== '全部' ? 
                                "没有找到匹配的帖子" : 
                                "还没有发布任何帖子"
                            }
                        >
                            {!searchKeyword && selectedTag === '全部' && (
                                <Text className="empty-tip">
                                    快去<a href="/send_community_post">发布你的第一个帖子</a>吧！
                                </Text>
                            )}
                        </Empty>
                    </div>
                ) : (
                    <div className="user-posts-list">
                        {filteredPosts.map(post => (
                            <PostCard post={post} key={post.id} showStats={true} />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default CommunityUserContent;