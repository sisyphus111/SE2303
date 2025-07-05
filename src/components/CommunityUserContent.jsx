import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tabs, message } from 'antd';
import { EditOutlined, HeartOutlined, StarOutlined } from '@ant-design/icons';
import { getPostList, MyPost, collectPost, delCollectedPost } from '../service/communityService';
import { useUser } from '../lib/UserContext';
import '../css/CommunityUserContent.css';

const CommunityUserContent = () => {
    const navigate = useNavigate();
    const { userProfile, valid, identity, getUserDisplayName } = useUser();
    const [activeTab, setActiveTab] = useState('myPosts');
    const [userPosts, setUserPosts] = useState([]);
    const [likedPosts, setLikedPosts] = useState([]);
    const [collectedPosts, setCollectedPosts] = useState([]);
    const [loading, setLoading] = useState(false);

    const auth = { userId: userProfile?.id, valid, identity };

    useEffect(() => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            navigate('/login');
            return;
        }

        const fetchData = async () => {
            setLoading(true);
            if (activeTab === 'myPosts') {
                const response = await MyPost(auth);
                if (response.success) {
                    setUserPosts(response.data);
                } else {
                    message.error(response.error);
                }
            } else if (activeTab === 'liked') {
                const response = await getPostList({ filter: 'liked' }, auth);
                if (response.success) {
                    setLikedPosts(response.data.content);
                } else {
                    message.error(response.error);
                }
            } else if (activeTab === 'collected') {
                const response = await getPostList({ filter: 'collected' }, auth);
                if (response.success) {
                    setCollectedPosts(response.data.content);
                } else {
                    message.error(response.error);
                }
            }
            setLoading(false);
        };

        fetchData();
    }, [activeTab, auth, userProfile, valid, navigate]);

    const handleTabChange = (key) => {
        setActiveTab(key);
    };

    const handleToggleCollect = async (postId, isCollected) => {
        try {
            const response = isCollected
                ? await delCollectedPost(postId, auth)
                : await collectPost(postId, auth);
            if (response.success) {
                setCollectedPosts(prev =>
                    isCollected
                        ? prev.filter(post => post.id !== postId)
                        : [...prev, { id: postId, ...response.data }]
                );
                message.success(isCollected ? '取消收藏成功' : '收藏成功');
            } else {
                message.error(response.error);
            }
        } catch (error) {
            message.error('操作失败：' + error.message);
        }
    };

    const renderPostList = (posts) => {
        if (loading) {
            return <div className="loading">加载中...</div>;
        }
        return posts.length === 0 ? (
            <div className="no-posts">暂无帖子</div>
        ) : (
            <div className="post-list">
                {posts.map(post => (
                    <div key={post.id} className="post-item" onClick={() => navigate(`/post/${post.id}`)}>
                        <h4 className="post-title">{post.title}</h4>
                        <div className="post-meta">
                            <span className="post-author">{getUserDisplayName(post.userId)}</span>
                            <span className="post-time">{new Date(post.createdAt).toLocaleDateString()}</span>
                            <div className="post-stats">
                                <span><HeartOutlined /> {post.postMetrics?.likeCount || 0}</span>
                                <span><StarOutlined onClick={(e) => { e.stopPropagation(); handleToggleCollect(post.id, collectedPosts.some(p => p.id === post.id)); }} />
                                    {collectedPosts.some(p => p.id === post.id) ? '已收藏' : '收藏'}
                                </span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        );
    };

    const tabItems = [
        {
            key: 'myPosts',
            label: (
                <span>
                    <EditOutlined />
                    我的帖子
                </span>
            ),
            children: renderPostList(userPosts),
        },
        {
            key: 'liked',
            label: (
                <span>
                    <HeartOutlined />
                    我的点赞
                </span>
            ),
            children: renderPostList(likedPosts),
        },
        {
            key: 'collected',
            label: (
                <span>
                    <StarOutlined />
                    我的收藏
                </span>
            ),
            children: renderPostList(collectedPosts),
        },
    ];

    return (
        <div className="community-user-content">
            <Tabs activeKey={activeTab} onChange={handleTabChange} items={tabItems} />
        </div>
    );
};

export default CommunityUserContent;