import React from 'react';
import { Avatar, Typography } from 'antd';
import '../css/CommunityPostComment.css';
const { Text } = Typography;

const DEFAULT_AVATAR = 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=1686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';

const PostComment = ({ user, content }) => {
    // 实现用户后，再替换
    // const avatarUrl = user.avatar || DEFAULT_AVATAR;
    const avatarUrl = DEFAULT_AVATAR;
    return (
        <div className="post-comment-card">
            <Avatar src={avatarUrl} className="post-comment-avatar" />
            <div className="post-comment-content">
                <span className="post-comment-username">{user.name}</span>
                <span className="post-comment-text">{content}</span>
            </div>
        </div>
    );
};

export default PostComment;