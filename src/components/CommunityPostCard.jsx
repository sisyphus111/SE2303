import React from 'react';
import { Space, Typography, Tag } from 'antd';
import { HeartOutlined, EyeOutlined, MessageOutlined, CalendarOutlined } from '@ant-design/icons';
import { Link } from "react-router-dom";
import '../css/PostCard.css';

const { Text } = Typography;

function PostCard({ post, showStats = false }) {
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffTime = Math.abs(now - date);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays === 1) return '今天';
        if (diffDays === 2) return '昨天';
        if (diffDays <= 7) return `${diffDays - 1}天前`;
        return date.toLocaleDateString('zh-CN');
    };

    return (
        <div className="post-card modern-card">
            <div className="post-card-content">
                <div className="post-card-left">
                    <Link to={`/post/${post.id}`} className="post-title-link">
                        <h3 className="post-title">{post.summary}</h3>
                    </Link>
                    
                    <div className="post-meta">
                        <div className="post-tags">
                            {post.tags.map(tag => (
                                <Tag key={tag} className="post-tag">
                                    {tag}
                                </Tag>
                            ))}
                        </div>
                        
                        {showStats && post.createdAt && (
                            <div className="post-date">
                                <CalendarOutlined className="date-icon" />
                                <Text className="date-text">{formatDate(post.createdAt)}</Text>
                            </div>
                        )}
                    </div>

                    {showStats && (
                        <div className="post-stats">
                            <Space size="large">
                                <div className="stat-item">
                                    <HeartOutlined className="stat-icon likes" />
                                    <Text className="stat-text">{post.likes || 0}</Text>
                                </div>
                                <div className="stat-item">
                                    <EyeOutlined className="stat-icon views" />
                                    <Text className="stat-text">{post.views || 0}</Text>
                                </div>
                                <div className="stat-item">
                                    <MessageOutlined className="stat-icon comments" />
                                    <Text className="stat-text">{post.comments || 0}</Text>
                                </div>
                            </Space>
                        </div>
                    )}
                </div>

                <div className="post-card-right">
                    <div className="post-author">
                        <img className="post-avatar" src={post.avatar} alt="avatar"/>
                        <Text className="post-username">{post.username}</Text>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PostCard;
