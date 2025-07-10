import React, {useState, useEffect, useMemo} from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    ArrowLeftOutlined,
    UserOutlined,
    TagOutlined,
    FireOutlined,
    ClockCircleOutlined
} from '@ant-design/icons';
import { message, Avatar } from 'antd';
import PostDetail from '../components/CommunityPostDetail';
import CommentInput from '../components/CommentIinput';
import PostCommentList from '../components/CommunityPostCommentList';
import { getPostDetail, getCommentOfPost, sendCommentToPost } from '../service/communityService';
import { useUser } from '../lib/UserContext';
import "../css/CommunityPostDetail.css";

const CommunityPostPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { userProfile, valid, identity, loading: contextLoading, getUserAvatar, getUserDisplayName } = useUser();
    const [postData, setPostData] = useState(null);
    const [comments, setComments] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [loading, setLoading] = useState(false);
    const [sidebarAvatarError, setSidebarAvatarError] = useState(false);

    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    useEffect(() => {
        const fetchPost = async () => {
            setLoading(true);
            if (!userProfile || !valid) {
                message.error('请先登录');
                navigate('/login');
                return;
            }
            const response = await getPostDetail(id, auth);
            if (response.success) {
                setPostData(response.data);
            } else {
                message.error(response.error);
                setPostData(null);
            }
            setLoading(false);
        };

        const fetchComments = async () => {
            setLoading(true);
            const response = await getCommentOfPost(id, currentPage, pageSize, auth);
            console.log("fetchComments:",response);
            if (response.success) {
                setComments(response.data.content);
            } else {
                message.error(response.error);
            }
            setLoading(false);
        };

        // 只有在UserContext加载完成后才进行认证检查
        if (contextLoading) {
            console.log('🔄 UserContext正在加载，等待初始化完成...');
            return;
        }

        if (valid && userProfile) {
            fetchPost();
            fetchComments();
        } else if (!valid || !userProfile) {
            console.log('❌ 用户未认证，重定向到登录页面');
            navigate('/login');
        }
    }, [id, currentPage, pageSize, auth, userProfile, valid, contextLoading, navigate]);

    const handleNewComment = async (content) => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            return;
        }
        const commentData = { content };
        const response = await sendCommentToPost(id, commentData, auth);
        if (response.success) {
            setComments(prev => [...prev, response.data]);
            message.success('评论发表成功');
        } else {
            message.error(response.error);
        }
    };

    // 在UserContext加载期间显示加载状态
    if (contextLoading) {
        return (
            <div className="loading" style={{
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center', 
                height: '100vh',
                fontSize: '16px',
                color: '#666'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ marginBottom: '16px', fontSize: '24px' }}>⏳</div>
                    正在初始化用户信息...
                </div>
            </div>
        );
    }

    if (loading) {
        return <div className="loading">加载中...</div>;
    }

    if (!postData) {
        return (
            <div className="post-not-found">
                <h2>帖子不存在</h2>
                <button onClick={() => navigate('/community')}>返回社区</button>
            </div>
        );
    }

    const handleSidebarAvatarError = () => {
        setSidebarAvatarError(true);
    };

    return (
        <div className="community-post-page">
            <div className="post-back-section">
                <button
                    className="back-btn"
                    onClick={() => navigate(-1)}
                >
                    <ArrowLeftOutlined /> 返回
                </button>
            </div>

            <div className="post-layout">
                <div className="post-main-content">
                    <PostDetail postData={postData} />

                    <div className="comments-section">
                        <div className="comments-header">
                            <h3>评论区</h3>
                            <span className="comments-count">
                                {comments.length} 条评论
                            </span>
                        </div>
                        <CommentInput onSubmit={handleNewComment} postId={id} />
                        <PostCommentList comments={comments} />
                    </div>
                </div>

                <div className="post-sidebar">
                    <div className="sidebar-card author-card">
                        <div className="sidebar-header">
                            <UserOutlined className="sidebar-icon" />
                            <h4>作者信息</h4>
                        </div>
                        <div className="author-info">
                            {sidebarAvatarError ? (
                                <Avatar 
                                    size={50} 
                                    icon={<UserOutlined />} 
                                    className="author-avatar"
                                />
                            ) : (
                                <img
                                    src={getUserAvatar(postData.userId)}
                                    alt={getUserDisplayName(postData.userId)}
                                    className="author-avatar"
                                    onError={handleSidebarAvatarError}
                                />
                            )}
                            <div className="author-details">
                                <div className="author-name">{getUserDisplayName(postData.userId)}</div>
                                <div className="author-stats">
                                    <span>帖子: {postData.postCount || 0}</span>
                                    <span>获赞: {postData.likeCount || 0}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="sidebar-card related-posts">
                        <div className="sidebar-header">
                            <FireOutlined className="sidebar-icon" />
                            <h4>相关帖子</h4>
                        </div>
                        <div className="related-posts-list">
                            {/* 相关帖子需后端支持，暂时保留模拟逻辑 */}
                        </div>
                    </div>

                    <div className="sidebar-card quick-actions">
                        <div className="sidebar-header">
                            <TagOutlined className="sidebar-icon" />
                            <h4>快捷操作</h4>
                        </div>
                        <div className="action-buttons">
                            <button
                                className="action-btn"
                                onClick={() => navigate('/community')}
                            >
                                浏览更多帖子
                            </button>
                            <button
                                className="action-btn primary"
                                onClick={() => navigate('/send_community_post')}
                            >
                                发布新帖子
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CommunityPostPage;