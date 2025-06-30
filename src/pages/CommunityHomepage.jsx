import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
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
import { getCommunityPosts } from '../service/communityService';
import { useUser } from '../lib/UserContext';
import '../css/CommunityHomepage.css';

const CommunityHomepage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated } = useUser();
    const [selectedTag, setSelectedTag] = useState('全部');
    const [searchValue, setSearchValue] = useState('');
    const [allPosts, setAllPosts] = useState([]);
    const [filteredPosts, setFilteredPosts] = useState([]);
    const [sortBy, setSortBy] = useState('latest'); // latest, hot, comments

  // 热门标签
  const hotTags = ['全部', '情感', '学习', '生活', '健康', '成长'];
  
  // 社区统计数据（模拟）
  const communityStats = {
    totalPosts: 1248,
    totalUsers: 356,
    todayPosts: 12,
    onlineUsers: 28
  };

    useEffect(() => {
        // 从URL获取标签参数
        const params = new URLSearchParams(location.search);
        const tag = params.get('tag');
        if (tag) {
            setSelectedTag(tag);
        }

        // 获取所有帖子数据
        const posts = getCommunityPosts();
        setAllPosts(posts);
    }, [location.search]);

    // 过滤和排序帖子
    useEffect(() => {
        let filtered = allPosts;

        // 标签筛选
        if (selectedTag !== '全部') {
            filtered = filtered.filter(post => 
                post.tags.some(tag => tag.includes(selectedTag)) || 
                post.category === selectedTag
            );
        }

        // 搜索筛选
        if (searchValue.trim()) {
            filtered = filtered.filter(post =>
                post.title.toLowerCase().includes(searchValue.toLowerCase()) ||
                post.content.toLowerCase().includes(searchValue.toLowerCase()) ||
                post.author.toLowerCase().includes(searchValue.toLowerCase()) ||
                post.tags.some(tag => tag.toLowerCase().includes(searchValue.toLowerCase()))
            );
        }

        // 排序
        switch (sortBy) {
            case 'hot':
                filtered.sort((a, b) => b.likes - a.likes);
                break;
            case 'comments':
                filtered.sort((a, b) => b.comments - a.comments);
                break;
            case 'latest':
            default:
                filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                break;
        }

        setFilteredPosts(filtered);
    }, [allPosts, selectedTag, searchValue, sortBy]);

    const handleSearch = (e) => {
        setSearchValue(e.target.value);
    };

    const handleTagClick = (tag) => {
        setSelectedTag(tag);
        // 更新URL参数
        if (tag === '全部') {
            navigate('/community');
        } else {
            navigate(`/community?tag=${encodeURIComponent(tag)}`);
        }
    };

    const handleSortChange = (sort) => {
        setSortBy(sort);
    };

    // 获取热门帖子（点赞数最多的前3个）
    const getHotPosts = () => {
        return [...allPosts]
            .sort((a, b) => b.likes - a.likes)
            .slice(0, 3);
    };

    // 获取最新帖子（最新的前3个）
    const getRecentPosts = () => {
        return [...allPosts]
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 3);
    };

    const formatTime = (timeStr) => {
        const now = new Date();
        const postTime = new Date(timeStr);
        const diffInHours = Math.floor((now - postTime) / (1000 * 60 * 60));
        
        if (diffInHours < 1) return '刚刚';
        if (diffInHours < 24) return `${diffInHours}小时前`;
        return `${Math.floor(diffInHours / 24)}天前`;
    };

    return (
    <div className="community-homepage">
      {/* 顶部欢迎区域 */}
      <div className="welcome-section">
        <div className="welcome-content">
          <div className="welcome-text">
            <h1>心晴社区</h1>
            <p>分享心情，互相支持，一起成长</p>
          </div>
          <div className="quick-actions">
            {isAuthenticated ? (
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
                placeholder="搜索感兴趣的话题..."
                value={searchValue}
                onChange={handleSearch}
                className="search-input"
              />
            </div>
          </div>
        </div>
      </div>

      <div className="main-content">
        {/* 主要内容区 - 帖子列表 */}
        <div className="content-main">
          {/* 标签筛选器 */}
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
                  className={`sort-btn ${sortBy === 'hot' ? 'active' : ''}`}
                  onClick={() => handleSortChange('hot')}
                >
                  <FireOutlined /> 最热
                </button>
                <button 
                  className={`sort-btn ${sortBy === 'comments' ? 'active' : ''}`}
                  onClick={() => handleSortChange('comments')}
                >
                  <CommentOutlined /> 评论最多
                </button>
              </div>
            </div>
            <div className="tags-grid">
              {hotTags.map(tag => (
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

          {/* 帖子列表 */}
          <div className="posts-section">
            <div className="posts-header">
              <h3>
                {selectedTag === '全部' ? '所有帖子' : `${selectedTag} 相关帖子`} 
                <span className="posts-count">（{filteredPosts.length}条）</span>
              </h3>
            </div>
            <div className="posts-list-main">
              {filteredPosts.length === 0 ? (
                <div className="no-posts">
                  <p>暂无匹配的帖子</p>
                  <p>试试调整筛选条件或搜索关键词</p>
                </div>
              ) : (
                filteredPosts.map(post => (
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
                            {post.likes}
                          </span>
                          <span className="stat-item">
                            <CommentOutlined />
                            {post.comments}
                          </span>
                          <span className="stat-item">
                            <EyeOutlined />
                            {post.views}
                          </span>
                        </div>
            
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        {/* 右侧边栏 */}
        <div className="sidebar">
          {/* 热门帖子 */}
          <div className="sidebar-card">
            <div className="sidebar-header">
              <FireOutlined className="sidebar-icon" />
              <h3>热门帖子</h3>
              <button className="more-btn" onClick={() => handleSortChange('hot')}>
                更多 <ArrowRightOutlined />
              </button>
            </div>
            <div className="hot-posts-list">
              {getHotPosts().map((post, index) => (
                <div key={post.id} className="hot-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                  <div className="hot-post-rank">{index + 1}</div>
                  <div className="hot-post-content">
                    <h4 className="hot-post-title">{post.title}</h4>
                    <div className="hot-post-meta">
                      <span className="author">@{post.author}</span>
                      <span className="likes">
                        <HeartOutlined /> {post.likes}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 最新动态 */}
          <div className="sidebar-card">
            <div className="sidebar-header">
              <ClockCircleOutlined className="sidebar-icon" />
              <h3>最新动态</h3>
              <button className="more-btn" onClick={() => handleSortChange('latest')}>
                更多 <ArrowRightOutlined />
              </button>
            </div>
            <div className="recent-posts-list">
              {getRecentPosts().map(post => (
                <div key={post.id} className="recent-post-item" onClick={() => navigate(`/post/${post.id}`)}>
                  <div className="recent-post-content">
                    <h4 className="recent-post-title">{post.title}</h4>
                    <div className="recent-post-meta">
                      <span className="author">@{post.author}</span>
                      <span className="time">{formatTime(post.createdAt)}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 社区统计 */}
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

          {/* 用户快捷操作 */}
          {isAuthenticated && (
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
                  onClick={() => navigate('/send_community_post')}
                >
                  <PlusOutlined /> 发布新帖
                </button>
                <button 
                  className="user-action-btn"
                  onClick={() => navigate('/community?filter=liked')}
                >
                  <HeartOutlined /> 我的收藏
                </button>
              </div>
            </div>
          )}

          {/* 社区指南 */}
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