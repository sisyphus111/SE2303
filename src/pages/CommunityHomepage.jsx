import React, {useState, useEffect, useMemo} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Pagination, message } from 'antd';
import {
    EditOutlined,
    SearchOutlined,
    UserOutlined,
    FireOutlined,
    ClockCircleOutlined,
    TrophyOutlined,
    HeartOutlined,
    TeamOutlined,
    TagOutlined,
    ArrowRightOutlined,
    PlusOutlined,
    EyeOutlined,
    CommentOutlined
} from '@ant-design/icons';
import { getPostList, getAllTags } from '../service/communityService';
import { useUser } from '../lib/UserContext';
import '../css/CommunityHomepage.css';

const CommunityHomepage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { userProfile, valid, identity, loading: contextLoading, getUserDisplayName} = useUser();
    const [selectedTag, setSelectedTag] = useState('全部');
    const [searchValue, setSearchValue] = useState('');
    const [searchKeyword, setSearchKeyword] = useState(''); // 新增：实际用于搜索的关键词
    const [posts, setPosts] = useState({ content: [], totalElements: 0 });
    const [sortBy, setSortBy] = useState('latest');
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [tags, setTags] = useState(['全部']);
    const [loading, setLoading] = useState(false);

    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    const communityStats = {
        totalPosts: 1248,
        totalUsers: 356,
        todayPosts: 12,
        onlineUsers: 28
    };

    useEffect(() => {
        const fetchTags = async () => {
            if (!userProfile || !valid || identity === null) {
                console.log('Waiting for user context to initialize');
                return;
            }

            setLoading(true);
            const response = await getAllTags(auth);
            if (response.success) {
                console.log('tags返回数据：', response.data);
                setTags(['全部', ...response.data]);
            } else {
                message.error('获取标签失败');
            }
            setLoading(false);
        };

        // 只有在UserContext加载完成后才进行认证检查
        if (contextLoading) {
            console.log('🔄 UserContext正在加载，等待初始化完成...');
            return;
        }

        if (valid && userProfile) {
            fetchTags();
        } else if (!valid || !userProfile) {
            console.log('❌ 用户未认证，重定向到登录页面');
            navigate('/login');
        }
    }, [valid, userProfile, identity, contextLoading, navigate]);

    useEffect(() => {
        const fetchPosts = async () => {
            if (!userProfile || !valid || identity === null) {
                console.log('Waiting for user context to initialize');
                return;
            }
            setLoading(true);
            const params = {
                page: currentPage,
                size: pageSize,
                sortBy,
                category: selectedTag !== '全部' ? selectedTag : undefined,
                keyword: searchKeyword || undefined, // 修改：使用防抖后的关键词
            };
            console.log('🚀 发送请求参数:', JSON.stringify(params, null, 2));
            const response = await getPostList(params, auth);
            console.log('📥 帖子响应:', JSON.stringify(response, null, 2));
            if (response.success) {
                console.log('posts返回数据：', response.data);
                setPosts(response.data);
            } else {
                message.error(response.error);
            }
            setLoading(false);
        };

        // 只有在UserContext加载完成且用户已认证时才获取帖子
        if (!contextLoading && valid && userProfile) {
            fetchPosts();
        }
    }, [currentPage, pageSize, sortBy, selectedTag, searchKeyword, valid, userProfile, identity, contextLoading, navigate]); // 修改：依赖searchKeyword而不是searchValue


    const handleSearch = (e) => {
        setSearchValue(e.target.value); // 只更新输入框的值，不触发搜索
    };

    // 处理回车键搜索
    const handleSearchKeyPress = (e) => {
        if (e.key === 'Enter') {
            setSearchKeyword(searchValue.trim());
            setCurrentPage(1);
        }
    };

    const handleTagClick = (tag) => {
        setSelectedTag(tag);
        setCurrentPage(1);
        if (tag === '全部') {
            navigate('/community');
        } else {
            navigate(`/community?tag=${encodeURIComponent(tag)}`);
        }
    };

    const handleSortChange = (sort) => {
        setSortBy(sort);
        setCurrentPage(1);
    };

    const handlePageChange = (page, size) => {
        setCurrentPage(page);
        if (size !== pageSize) {
            setPageSize(size);
        }
        document.querySelector('.posts-section')?.scrollIntoView({ behavior: 'smooth' });
    };

    const formatTime = (timeStr) => {
        const now = new Date();
        const postTime = new Date(timeStr);
        const diffInHours = Math.floor((now - postTime) / (1000 * 60 * 60));

        if (diffInHours < 1) return '刚刚';
        if (diffInHours < 24) return `${diffInHours}小时前`;
        return `${Math.floor(diffInHours / 24)}天前`;
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

    return (
        <div className="community-homepage">
            <div className="welcome-section">
                <div className="welcome-content">
                    <div className="welcome-text">
                        <h1>心晴社区</h1>
                        <p>分享心情，互相支持，一起成长</p>
                    </div>
                    <div className="quick-actions">
                        {userProfile &&  identity && valid ? (
                            <button
                                className="action-btn primary"
                                onClick={() => navigate('/send_community_post')}
                            >
                                <PlusOutlined /> 发布帖子
                            </button>
                        ) : (
                            <button
                                className="action-btn secondary"
                                onClick={() => navigate('/login')}
                            >
                                <UserOutlined /> 登录参与
                            </button>
                        )}
                        <div className="search-container">
                            <SearchOutlined className="search-icon" />
                            <input
                                type="text"
                                placeholder="搜索感兴趣的话题... (按回车键搜索)"
                                value={searchValue}
                                onChange={handleSearch}
                                onKeyPress={handleSearchKeyPress}
                                className="search-input"
                            />
                        </div>
                    </div>
                </div>
            </div>

            <div className="main-content">
                <div className="content-main">
                    <div className="section-card filter-section">
                        <div className="filter-header">
                            <div className="filter-left">
                                <TagOutlined className="section-icon" />
                                <h3>话题筛选</h3>
                            </div>
                            <div className="sort-controls">
                                <button
                                    className={`sort-btn ${sortBy === 'latest' ? 'active' : ''}`}
                                    onClick={() => handleSortChange('latest')}
                                >
                                    <ClockCircleOutlined /> 最新
                                </button>
                                <button
                                    className={`sort-btn ${sortBy === 'mostLiked' ? 'active' : ''}`}
                                    onClick={() => handleSortChange('mostLiked')}
                                >
                                    <FireOutlined /> 最多点赞
                                </button>
                                <button
                                    className={`sort-btn ${sortBy === 'mostCommented' ? 'active' : ''}`}
                                    onClick={() => handleSortChange('mostCommented')}
                                >
                                    <CommentOutlined /> 最多评论
                                </button>
                            </div>
                        </div>
                        <div className="tags-grid">
                            {tags.map(tag => (
                                <button
                                    key={tag}
                                    className={`tag-btn ${selectedTag === tag ? 'active' : ''}`}
                                    onClick={() => handleTagClick(tag)}
                                >
                                    {tag}
                                </button>
                            ))}
                        </div>
                    </div>

                    <div className="posts-section">
                        <div className="posts-header">
                            <h3>
                                {selectedTag === '全部' ? '所有帖子' : `${selectedTag} 相关帖子`}
                                <span className="posts-count">（{posts.totalElements}条）</span>
                            </h3>
                            {/* 显示当前搜索状态 */}
                            {searchKeyword && (
                                <div className="search-status" style={{ marginTop: '8px', fontSize: '14px', color: '#666' }}>
                                    正在搜索: "{searchKeyword}" 
                                    <button 
                                        className="clear-search-btn"
                                        onClick={() => {
                                            setSearchValue('');
                                            setSearchKeyword('');
                                        }}
                                        style={{ 
                                            marginLeft: '8px', 
                                            padding: '2px 8px', 
                                            border: 'none', 
                                            background: '#f0f0f0', 
                                            borderRadius: '4px',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        清除
                                    </button>
                                </div>
                            )}
                        </div>
                        <div className="posts-list-main">
                            {posts.content.length === 0 ? (
                                <div className="no-posts">
                                    <p>暂无匹配的帖子</p>
                                    <p>试试调整筛选条件或搜索关键词</p>
                                </div>
                            ) : (
                                posts.content.map(post => (
                                    <div key={post.id} className="post-card" onClick={() => navigate(`/post/${post.id}`)}>
                                        <h4 className="post-title">{post.title}</h4>
                                        <div className="post-meta">
                                            <div className="post-tags">
                                                {post.tags.map(tag => (
                                                    <span key={tag} className="post-tag">#{tag}</span>
                                                ))}
                                            </div>
                                            <div className="post-stats">
                                                <span className="stat-item">
                                                    <HeartOutlined className={post.isLiked ? 'liked' : ''} />
                                                    {post.likeCount || 0}
                                                </span>
                                                <span className="stat-item">
                                                    <CommentOutlined />
                                                    {post.commentCount || 0}
                                                </span>
                                            </div>
                                            <div className="post-author">
                                                <span className="author-name">{getUserDisplayName(post.userId)}</span>
                                            </div>
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>

                        {posts.totalElements > 0 && (
                            <div className="pagination-container">
                                <Pagination
                                    current={currentPage}
                                    total={posts.totalElements}
                                    pageSize={pageSize}
                                    showSizeChanger
                                    showQuickJumper
                                    showTotal={(total, range) =>
                                        `第 ${range[0]}-${range[1]} 条 / 共 ${total} 条`
                                    }
                                    pageSizeOptions={['5', '10', '15', '20']}
                                    onChange={handlePageChange}
                                    onShowSizeChange={handlePageChange}
                                />
                            </div>
                        )}
                    </div>
                </div>

                <div className="sidebar">
                    <div className="sidebar-card">
                        <div className="sidebar-header">
                            <FireOutlined className="sidebar-icon" />
                            <h3>热门帖子</h3>
                        </div>
                        <div className="hot-posts-list">
                            {posts.content
                                .sort((a, b) => (b.postMetrics?.likeCount || 0) - (a.postMetrics?.likeCount || 0))
                                .slice(0, 3)
                                .map((post, index) => (
                                    <div key={post.id} className="hot-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                                        <div className="hot-post-rank">{index + 1}</div>
                                        <div className="hot-post-content">
                                            <h4 className="hot-post-title">{post.title}</h4>
                                            <div className="hot-post-meta">
                                                <span className="author">@{getUserDisplayName(post.userId)}</span>
                                                <span className="likes">
                                                    <HeartOutlined /> {post.postMetrics?.likeCount || 0}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                        </div>
                    </div>

                    <div className="sidebar-card">
                        <div className="sidebar-header">
                            <ClockCircleOutlined className="sidebar-icon" />
                            <h3>最新动态</h3>
                        </div>
                        <div className="recent-posts-list">
                            {posts.content
                                .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                                .slice(0, 3)
                                .map(post => (
                                    <div key={post.id} className="recent-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                                        <div className="recent-post-content">
                                            <h4 className="recent-post-title">{post.title}</h4>
                                            <div className="recent-post-meta">
                                                <span className="author">@{getUserDisplayName(post.userId)}</span>
                                                <span className="time">{formatTime(post.createdAt)}</span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                        </div>
                    </div>

                    <div className="sidebar-card">
                        <div className="sidebar-header">
                            <TrophyOutlined className="sidebar-icon" />
                            <h3>社区数据</h3>
                        </div>
                        <div className="stats-grid">
                            <div className="stat-item">
                                <div className="stat-number">{communityStats.totalPosts}</div>
                                <div className="stat-label">总帖子数</div>
                            </div>
                            <div className="stat-item">
                                <div className="stat-number">{communityStats.totalUsers}</div>
                                <div className="stat-label">社区成员</div>
                            </div>
                            <div className="stat-item">
                                <div className="stat-number">{communityStats.todayPosts}</div>
                                <div className="stat-label">今日发帖</div>
                            </div>
                            <div className="stat-item">
                                <div className="stat-number">{communityStats.onlineUsers}</div>
                                <div className="stat-label">在线用户</div>
                            </div>
                        </div>
                    </div>

                    {userProfile && valid && (
                        <div className="sidebar-card">
                            <div className="sidebar-header">
                                <UserOutlined className="sidebar-icon" />
                                <h3>我的</h3>
                            </div>
                            <div className="user-actions">
                                <button
                                    className="user-action-btn"
                                    onClick={() => navigate('/community_user_page')}
                                >
                                    <EditOutlined /> 我的帖子
                                </button>
                                <button
                                    className="user-action-btn"
                                    onClick={() => navigate('/community_user_page?tab=liked')}
                                >
                                    <HeartOutlined /> 我的点赞
                                </button>
                            </div>
                        </div>
                    )}

                    <div className="sidebar-card">
                        <div className="sidebar-header">
                            <TeamOutlined className="sidebar-icon" />
                            <h3>社区指南</h3>
                        </div>
                        <div className="guide-content">
                            <div className="guide-item">
                                <h4>💚 友善交流</h4>
                                <p>用温暖的话语互相支持</p>
                            </div>
                            <div className="guide-item">
                                <h4>🤝 互助成长</h4>
                                <p>分享经验，共同进步</p>
                            </div>
                            <div className="guide-item">
                                <h4>🌱 积极向上</h4>
                                <p>传播正能量，保持乐观</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CommunityHomepage;