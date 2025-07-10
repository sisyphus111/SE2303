import React, {useState, useEffect, useMemo} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Tabs, message } from 'antd';
import { EditOutlined, HeartOutlined, StarOutlined } from '@ant-design/icons';
import { getPostList, MyPost } from '../service/communityService';
import { useUser } from '../lib/UserContext';
import '../css/CommunityUserContent.css';

const CommunityUserContent = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { userProfile, valid, identity, getUserDisplayName } = useUser();
    const [activeTab, setActiveTab] = useState('myPosts');
    const [userPosts, setUserPosts] = useState([]);
    const [likedPosts, setLikedPosts] = useState([]);
    const [loading, setLoading] = useState(false);

    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    // 读取URL参数并设置activeTab
    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const tab = searchParams.get('tab');
        if (tab && ['myPosts', 'liked'].includes(tab)) {
            setActiveTab(tab);
        }
    }, [location.search]);

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
            }
            setLoading(false);
        };

        fetchData();
    }, [activeTab, auth, userProfile, valid, navigate]);

    const handleTabChange = (key) => {
        setActiveTab(key);
        // 更新URL参数，但不刷新页面
        const searchParams = new URLSearchParams(location.search);
        searchParams.set('tab', key);
        navigate(`${location.pathname}?${searchParams.toString()}`, { replace: true });
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

    ];

    return (
        <div className="community-user-content">
            <Tabs activeKey={activeTab} onChange={handleTabChange} items={tabItems} />
        </div>
    );
};

export default CommunityUserContent;