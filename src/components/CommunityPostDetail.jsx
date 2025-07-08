import React, {useMemo, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {
    HeartOutlined,
    HeartFilled,
    ShareAltOutlined,
    EyeOutlined,
    ClockCircleOutlined
} from "@ant-design/icons";
import { LikePost, UnlikePost } from '../service/communityService';
import { message } from 'antd';
import { useUser } from '../lib/UserContext';
import '../css/CommunityPostDetail.css';

const PostDetail = ({ postData }) => {
    const navigate = useNavigate();
    const { userProfile, valid, identity, getUserAvatar, loading: contextLoading, getUserDisplayName } = useUser();
    const [liked, setLiked] = useState(postData.isLiked || false);
    const [likeCount, setLikeCount] = useState(postData.postMetrics?.likeCount || 0);
    const [loading, setLoading] = useState(false);
    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    const handleLike = async () => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            navigate('/login');
            return;
        }
        try {
            let response;
            if (liked) {

                response = await UnlikePost(postData.id, auth);
                if (response.success) {
                    setLiked(false);
                    setLikeCount(prev => prev - 1);
                    message.success('取消点赞成功');
                } else {
                    message.error(response.error);
                }
            } else {

                response = await LikePost(postData.id, auth);
                if (response.success) {
                    setLiked(true);
                    setLikeCount(prev => prev + 1);
                    message.success('点赞成功');
                } else {
                    message.error(response.error);
                }
            }
        } catch (error) {
            message.error('操作失败：' + error.message);
        }
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
                        <span>{formatDate(postData.createdAt)}</span>
                    </div>
                </div>
            </div>

            <div className="post-author-section">
                <div className="author-info">
                    <img
                        src={getUserAvatar(postData.userId)}
                        alt={getUserDisplayName(postData.userId)}
                        className="author-avatar"
                    />
                    <div className="author-details">
                        <div className="author-name">{getUserDisplayName(postData.userId)}</div>
                        <div className="author-badge">活跃作者</div>
                    </div>
                </div>
                <div className="post-stats">
                    <div className="stat-item">
                        <EyeOutlined />
                        <span>{postData.postMetrics?.viewCount || 0}</span>
                    </div>
                    <div className="stat-item">
                        <HeartOutlined />
                        <span>{likeCount}</span>
                    </div>
                </div>
            </div>

            <div className="post-content">
                <p>{postData.content}</p>
            </div>

            <div className="post-actions">
                <button
                    className={`action-btn like-btn ${liked ? 'liked' : ''}`}
                    onClick={handleLike}
                >
                    {liked ? <HeartFilled /> : <HeartOutlined />}
                    <span>{liked ? '已点赞' : '点赞'}</span>
                    <span className="like-count">({likeCount})</span>
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