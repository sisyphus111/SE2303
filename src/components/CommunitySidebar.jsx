import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
    FireOutlined, 
    ClockCircleOutlined, 
    HeartOutlined,
    TrophyOutlined,
    UserOutlined,
    TeamOutlined,
    EditOutlined
} from '@ant-design/icons';
import UserDisplayName from './UserDisplayName';
import UserInfoCard from './UserInfoCard';
import '../css/CommunitySidebar.css';

const CommunitySidebar = ({ 
    posts, 
    communityStats, 
    userProfile, 
    valid, 
    loading: postsLoading 
}) => {
    const navigate = useNavigate();
    const [isFullyLoaded, setIsFullyLoaded] = useState(false);
    
    // 格式化时间函数
    const formatTime = (timeStr) => {
        const now = new Date();
        const postTime = new Date(timeStr);
        const diffInHours = Math.floor((now - postTime) / (1000 * 60 * 60));

        if (diffInHours < 1) return '刚刚';
        if (diffInHours < 24) return `${diffInHours}小时前`;
        return `${Math.floor(diffInHours / 24)}天前`;
    };

    // 检查所有组件是否加载完成
    useEffect(() => {
        const checkAllLoaded = () => {
            // 检查帖子数据是否加载完成
            if (postsLoading) return false;
            
            // 检查是否有帖子数据
            if (!posts || !posts.content || posts.content.length === 0) return true;
            
            // 给子组件足够的时间加载（包括UserDisplayName和UserInfoCard）
            return true;
        };

        const timer = setTimeout(() => {
            if (checkAllLoaded()) {
                setIsFullyLoaded(true);
            }
        }, 100); // 给足够的时间让所有子组件加载完成

        return () => clearTimeout(timer);
    }, [postsLoading, posts]);

    // 直接使用原始组件
    const UserDisplayNameWrapper = UserDisplayName;
    const UserInfoCardWrapper = UserInfoCard;

    // 如果还没有完全加载，显示加载状态
    if (!isFullyLoaded) {
        return (
            <div className="sidebar">
                <div className="sidebar-card loading">
                    <div className="sidebar-header">
                        <FireOutlined className="sidebar-icon" />
                        <h3>热门帖子</h3>
                    </div>
                    <div className="loading-skeleton">
                        {[1, 2, 3].map(i => (
                            <div key={i} className="hot-post-skeleton">
                                <div className="rank-skeleton"></div>
                                <div className="content-skeleton">
                                    <div className="title-skeleton"></div>
                                    <div className="meta-skeleton"></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="sidebar-card loading">
                    <div className="sidebar-header">
                        <ClockCircleOutlined className="sidebar-icon" />
                        <h3>最新动态</h3>
                    </div>
                    <div className="loading-skeleton">
                        {[1, 2, 3].map(i => (
                            <div key={i} className="recent-post-skeleton">
                                <div className="content-skeleton">
                                    <div className="title-skeleton"></div>
                                    <div className="meta-skeleton"></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="sidebar-card loading">
                    <div className="sidebar-header">
                        <TrophyOutlined className="sidebar-icon" />
                        <h3>社区数据</h3>
                    </div>
                    <div className="stats-skeleton">
                        {[1, 2, 3, 4].map(i => (
                            <div key={i} className="stat-skeleton">
                                <div className="stat-number-skeleton"></div>
                                <div className="stat-label-skeleton"></div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    }

    // 获取热门帖子（按点赞数排序）
    const hotPosts = (posts?.content || [])
        .sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
        .slice(0, 3);

    // 获取最新动态（按时间排序）
    const recentPosts = (posts?.content || [])
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 3);

    return (
        <div className="sidebar">
            <div className="sidebar-card">
                <div className="sidebar-header">
                    <FireOutlined className="sidebar-icon" />
                    <h3>热门帖子</h3>
                </div>
                <div className="hot-posts-list">
                    {hotPosts.map((post, index) => (
                        <div key={post.id} className="hot-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                            <div className="hot-post-rank">{index + 1}</div>
                            <div className="hot-post-content">
                                <h4 className="hot-post-title">{post.title}</h4>
                                <div className="hot-post-meta">
                                    <span className="author">
                                        @<UserDisplayNameWrapper userId={post.userId} forMention={true} />
                                    </span>
                                    <span className="likes">
                                        <HeartOutlined /> {post.likeCount || 0}
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
                    {recentPosts.map(post => (
                        <div key={post.id} className="recent-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                            <div className="recent-post-content">
                                <h4 className="recent-post-title">{post.title}</h4>
                                <div className="recent-post-meta">
                                    <span className="author">
                                        @<UserDisplayNameWrapper userId={post.userId} forMention={true} />
                                    </span>
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
                        <div className="stat-number">{typeof communityStats?.totalPosts === 'number' ? communityStats.totalPosts : 0}</div>
                        <div className="stat-label">总帖子数</div>
                    </div>
                    <div className="stat-item">
                        <div className="stat-number">{typeof communityStats?.totalUsers === 'number' ? communityStats.totalUsers : 0}</div>
                        <div className="stat-label">社区成员</div>
                    </div>
                    <div className="stat-item">
                        <div className="stat-number">{typeof communityStats?.todayPosts === 'number' ? communityStats.todayPosts : 0}</div>
                        <div className="stat-label">今日发帖</div>
                    </div>
                    <div className="stat-item">
                        <div className="stat-number">{typeof communityStats?.todayComments === 'number' ? communityStats.todayComments : 0}</div>
                        <div className="stat-label">今日评论</div>
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
    );
};

export default CommunitySidebar; 