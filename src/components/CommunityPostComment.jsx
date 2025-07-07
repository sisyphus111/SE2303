import React, { useState } from 'react';
import { HeartOutlined, HeartFilled, MessageOutlined, MoreOutlined } from '@ant-design/icons';
import { message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import { post, del } from '../service/common';
import '../css/CommunityPostComment.css';

const PostComment = ({ comment }) => {
    const navigate = useNavigate();
    const { userProfile, valid, getUserDisplayName, getUserAvatar } = useUser();
    const [liked, setLiked] = useState(comment.isLiked || false);
    const [likeCount, setLikeCount] = useState(comment.likeCount || 0);
    const auth = { userId: userProfile?.id, valid };

    const handleLike = async () => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            navigate('/login');
            return;
        }
        try {
            let response;
            if (liked) {
                response = await del(`/posts/unlike_comment/${comment.id}`, {}, {}, auth);
                if (response.success) {
                    setLiked(false);
                    setLikeCount(prev => prev - 1);
                    message.success('取消点赞成功');
                } else {
                    message.error(response.message || '取消点赞失败');
                }
            } else {
                response = await post(`/posts/like_comment/${comment.id}`, {}, {}, auth);
                if (response.success) {
                    setLiked(true);
                    setLikeCount(prev => prev + 1);
                    message.success('点赞成功');
                } else {
                    message.error(response.message || '点赞失败');
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
        <div className="comment-card">
            <div className="comment-avatar">
                <img
                    src={getUserAvatar(comment.userId)}
                    alt={getUserDisplayName(comment.userId)}
                />
            </div>
            <div className="comment-content">
                <div className="comment-header">
                    <div className="comment-user-info">
                        <span className="comment-username">{getUserDisplayName(comment.userId)}</span>
                        <span className="comment-time">{formatDate(comment.createdAt)}</span>
                    </div>
                    <button className="comment-menu-btn">
                        <MoreOutlined />
                    </button>
                </div>
                <div className="comment-text">
                    {comment.content}
                </div>
                <div className="comment-actions">
                    <button
                        className={`comment-action-btn like-btn ${liked ? 'liked' : ''}`}
                        // onClick={handleLike}
                    >
                        {liked ? <HeartFilled /> : <HeartOutlined />}
                        <span>8</span>
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