import React, {useMemo, useState, useEffect, useRef} from 'react';
import { HeartOutlined, HeartFilled, MessageOutlined, MoreOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import { post, del } from '../service/common';
import { reportComment } from '../service/community_common';
import ReportModal from './ReportModal';
import '../css/CommunityPostComment.css';

const PostComment = ({ comment }) => {
    const navigate = useNavigate();
    const { userProfile, valid, identity,getUserDisplayName, getUserAvatar } = useUser();
    const [liked, setLiked] = useState(comment.isLiked || false);
    const [likeCount, setLikeCount] = useState(comment.likeCount || 0);
    const [showReportModal, setShowReportModal] = useState(false);
    const [showMenu, setShowMenu] = useState(false);
    const menuRef = useRef(null);
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

    // 处理举报
    const handleReport = async (commentId, reportData) => {
        try {
            await reportComment(commentId, reportData.reason);
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
        setShowMenu(false);
        setShowReportModal(true);
    };

    // 点击外部关闭菜单
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setShowMenu(false);
            }
        };

        if (showMenu) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showMenu]);

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
                    <div className="comment-menu-container" ref={menuRef}>
                        <button 
                            className="comment-menu-btn"
                            onClick={() => setShowMenu(!showMenu)}
                        >
                            <MoreOutlined />
                        </button>
                        {showMenu && (
                            <div className="comment-menu-dropdown">
                                <button 
                                    className="comment-menu-item"
                                    onClick={openReportModal}
                                >
                                    <ExclamationCircleOutlined />
                                    举报评论
                                </button>
                            </div>
                        )}
                    </div>
                </div>
                <div className="comment-text">
                    {comment.content}
                </div>
                <div className="comment-actions">
                    {/* <button
                        className={`comment-action-btn like-btn ${liked ? 'liked' : ''}`}
                        // onClick={handleLike}
                    >
                        {liked ? <HeartFilled /> : <HeartOutlined />}
                        <span>8</span>
                    </button>
                    <button className="comment-action-btn reply-btn">
                        <MessageOutlined />
                        <span>回复</span>
                    </button> */}
                </div>
            </div>

            {/* 举报弹窗 */}
            <ReportModal
                visible={showReportModal}
                onClose={() => setShowReportModal(false)}
                onReport={handleReport}
                contentType="comment"
                contentId={comment.id}
                contentTitle={comment.content.substring(0, 50) + (comment.content.length > 50 ? '...' : '')}
            />
        </div>
    );
};

export default PostComment;