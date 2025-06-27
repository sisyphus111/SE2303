import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  HeartOutlined, 
  HeartFilled, 
  ShareAltOutlined,
  EyeOutlined,
  ClockCircleOutlined
} from "@ant-design/icons";
import '../css/CommunityPostDetail.css';

const PostDetail = ({ postData }) => {
    const navigate = useNavigate();
    const [liked, setLiked] = useState(false);

    const handleLike = () => {
        setLiked(l => !l);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <div className="post-detail-container">
            {/* 帖子头部 */}
            <div className="post-header">
                <h1 className="post-title">{postData.title}</h1>
                <div className="post-meta">
                    <div className="post-tags">
                            {postData.tags.map(tag => (
                            <span key={tag} className="post-tag">
                                        {tag}
                            </span>
                        ))}
                    </div>
                    <div className="post-time">
                        <ClockCircleOutlined />
                        <span>{formatDate(postData.time)}</span>
                    </div>
                </div>
            </div>

            {/* 作者信息 */}
            <div className="post-author-section">
                <div className="author-info">
                    <img 
                        src={postData.user.avatar} 
                        alt={postData.user.name}
                        className="author-avatar"
                    />
                    <div className="author-details">
                        <div className="author-name">{postData.user.name}</div>
                        <div className="author-badge">活跃作者</div>
                    </div>
                </div>
                <div className="post-stats">
                    <div className="stat-item">
                        <EyeOutlined />
                        <span>{postData.views}</span>
                    </div>
                    <div className="stat-item">
                        <HeartOutlined />
                        <span>{postData.likes + (liked ? 1 : 0)}</span>
                    </div>
                </div>
            </div>

            {/* 帖子内容 */}
            <div className="post-content">
                <p>{postData.content}</p>
                      </div>

            {/* 互动区域 */}
            <div className="post-actions">
                <button 
                    className={`action-btn like-btn ${liked ? 'liked' : ''}`}
                                onClick={handleLike}
                            >
                    {liked ? <HeartFilled /> : <HeartOutlined />}
                    <span>{liked ? '已点赞' : '点赞'}</span>
                    <span className="like-count">({postData.likes + (liked ? 1 : 0)})</span>
                </button>
                <button className="action-btn share-btn">
                    <ShareAltOutlined />
                    <span>分享</span>
                </button>
            </div>
        </div>
    );
};

export default PostDetail;