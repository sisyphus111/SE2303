import React, {useMemo, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {
    HeartOutlined,
    HeartFilled,
    ShareAltOutlined,
    EyeOutlined,
    ClockCircleOutlined,
    ExclamationCircleOutlined,
    UserOutlined
} from "@ant-design/icons";
import { LikePost, UnlikePost } from '../service/communityService';
import { reportPost } from '../service/community_common';
import { message, Avatar } from 'antd';
import { useUser } from '../lib/UserContext';
import UserDisplayName from './UserDisplayName';
import UserInfoCard from './UserInfoCard';
import ReportModal from './ReportModal';
import '../css/CommunityPostDetail.css';

const PostDetail = ({ postData }) => {
    // 打印postData的liked
    console.log("postData.liked:", postData.liked);


    const navigate = useNavigate();
    const { userProfile, valid, identity, getUserAvatar, loading: contextLoading, getUserDisplayName } = useUser();
    const [liked, setLiked] = useState(postData.liked || false);
    const [likeCount, setLikeCount] = useState(postData.likeCount || 0);
    const [loading, setLoading] = useState(false);
    const [showReportModal, setShowReportModal] = useState(false);
    const [authorAvatarError, setAuthorAvatarError] = useState(false);
    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    const handleLike = async () => {

        console.log("like 按钮被点击");
        if (!userProfile || !valid) {
            message.error('请先登录');
            navigate('/login');
            return;
        }
        // 日志输出liked和postData.id
        console.log("当前liked状态:", liked);
        console.log("当前postData.id:", postData.id);
        try {
            let response;
            if (liked) {

                response = await UnlikePost(postData.id);
                if (response.success) {
                    setLiked(false);
                    setLikeCount(prev => prev - 1);
                    message.success('取消点赞成功');
                } else {
                    message.error(response.error);
                }
            } else {
                console.log("开始点赞操作");
                response = await LikePost(postData.id);
                console.log("点赞操作完成", response);
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

    // 处理举报
    const handleReport = async (postId, reportData) => {
        try {
            await reportPost(postId, reportData);
            return { success: true };
        } catch (error) {
            throw new Error(error.message || '举报失败');
        }
    };

    // 打开举报弹窗
    const openReportModal = () => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            navigate('/login');
            return;
        }
        setShowReportModal(true);
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

    const handleAuthorAvatarError = () => {
        setAuthorAvatarError(true);
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
                <UserInfoCard 
                    userId={postData.userId} 
                    showStats={true}
                    className="post-detail-author-card"
                />
                <div className="post-stats">
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
                
                <button
                    className="action-btn report-btn"
                    onClick={openReportModal}
                >
                    <ExclamationCircleOutlined />
                    <span>举报</span>
                </button>
            </div>

            {/* 举报弹窗 */}
            <ReportModal
                visible={showReportModal}
                onClose={() => setShowReportModal(false)}
                onReport={handleReport}
                contentType="post"
                contentId={postData.id}
                contentTitle={postData.title}
            />
        </div>
    );
};

export default PostDetail;