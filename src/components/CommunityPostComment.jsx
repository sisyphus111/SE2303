import React, { useState } from 'react';
import { 
  HeartOutlined, 
  HeartFilled, 
  MessageOutlined, 
  MoreOutlined 
} from '@ant-design/icons';
import '../css/CommunityPostComment.css';

const PostComment = ({ user, content, time }) => {
    const [liked, setLiked] = useState(false);
    const [likeCount, setLikeCount] = useState(Math.floor(Math.random() * 10));

    const handleLike = () => {
        setLiked(!liked);
        setLikeCount(prev => liked ? prev - 1 : prev + 1);
    };

    const formatTimeAgo = () => {
        if (time) {
            return time;
        }
        // 模拟时间
        const minutes = Math.floor(Math.random() * 60) + 1;
        return `${minutes}分钟前`;
    };

    return (
        <div className="comment-card">
            <div className="comment-avatar">
                <img 
                    src={user.avatar || 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=1686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'} 
                    alt={user.name}
                />
            </div>
            <div className="comment-content">
                <div className="comment-header">
                    <div className="comment-user-info">
                        <span className="comment-username">{user.name}</span>
                        <span className="comment-time">{formatTimeAgo()}</span>
                    </div>
                    <button className="comment-menu-btn">
                        <MoreOutlined />
                    </button>
                </div>
                <div className="comment-text">
                    {content}
                </div>
                <div className="comment-actions">
                    <button 
                        className={`comment-action-btn like-btn ${liked ? 'liked' : ''}`}
                        onClick={handleLike}
                    >
                        {liked ? <HeartFilled /> : <HeartOutlined />}
                        <span>{likeCount}</span>
                    </button>
                    <button className="comment-action-btn reply-btn">
                        <MessageOutlined />
                        <span>回复</span>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PostComment;