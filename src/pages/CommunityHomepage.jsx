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
  PlusOutlined
} from '@ant-design/icons';
import '../css/CommunityHomepage.css';

const CommunityHomepage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedTag, setSelectedTag] = useState('全部');
  const [searchValue, setSearchValue] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 热门标签
  const hotTags = ['心情分享', '压力管理', '学习交流', '生活感悟', '健康心理', '人际关系'];
  
  // 社区统计数据（模拟）
  const communityStats = {
    totalPosts: 1248,
    totalUsers: 356,
    todayPosts: 12,
    onlineUsers: 28
  };

  // 热门帖子（模拟数据）
  const hotPosts = [
    { id: 1, title: '如何缓解考试焦虑？分享我的经验', author: '小明', likes: 89, comments: 34 },
    { id: 2, title: '大学生活压力大，我是这样调节的', author: '小红', likes: 67, comments: 28 },
    { id: 3, title: '推荐几本治愈系书籍', author: '小李', likes: 45, comments: 19 }
  ];

  // 最新帖子（模拟数据）
  const recentPosts = [
    { id: 4, title: '今天心情不错，分享一下好消息', author: '阳光女孩', time: '5分钟前' },
    { id: 5, title: '有没有一起学习的小伙伴？', author: '努力学习', time: '12分钟前' },
    { id: 6, title: '关于时间管理的一些思考', author: '时间管理师', time: '25分钟前' }
  ];

  useEffect(() => {
    // 检查登录状态
    const loginStatus = localStorage.getItem('isLoggedIn');
    if (loginStatus === 'true') {
      setIsLoggedIn(true);
    }

    // 从URL获取标签参数
    const params = new URLSearchParams(location.search);
    const tag = params.get('tag');
    if (tag) {
      setSelectedTag(tag);
    }
  }, [location.search]);

  const handleSearch = (e) => {
    setSearchValue(e.target.value);
  };

  const handleTagClick = (tag) => {
    setSelectedTag(tag);
    navigate(`/community?tag=${encodeURIComponent(tag)}`);
  };

  return (
    <div className="community-homepage">
      {/* 顶部欢迎区域 */}
      <div className="welcome-section">
        <div className="welcome-content">
          <div className="welcome-text">
            <h1>心理健康社区</h1>
            <p>分享心情，互相支持，一起成长</p>
          </div>
          <div className="quick-actions">
            {isLoggedIn ? (
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
        {/* 左侧内容区 */}
        <div className="content-left">
          {/* 热门话题标签 */}
          <div className="section-card">
            <div className="section-header">
              <TagOutlined className="section-icon" />
              <h3>热门话题</h3>
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

          {/* 热门帖子 */}
          <div className="section-card">
            <div className="section-header">
              <FireOutlined className="section-icon" />
              <h3>热门帖子</h3>
              <button className="more-btn" onClick={() => navigate('/community?sort=hot')}>
                更多 <ArrowRightOutlined />
              </button>
            </div>
            <div className="posts-list">
              {hotPosts.map(post => (
                <div key={post.id} className="post-item hot-post" onClick={() => navigate(`/post/${post.id}`)}>
                  <div className="post-content">
                    <h4 className="post-title">{post.title}</h4>
                    <div className="post-meta">
                      <span className="author">@{post.author}</span>
                      <div className="stats">
                        <span><HeartOutlined /> {post.likes}</span>
                        <span>💬 {post.comments}</span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 最新帖子 */}
          <div className="section-card">
            <div className="section-header">
              <ClockCircleOutlined className="section-icon" />
              <h3>最新动态</h3>
              <button className="more-btn" onClick={() => navigate('/community?sort=latest')}>
                更多 <ArrowRightOutlined />
              </button>
            </div>
            <div className="posts-list">
              {recentPosts.map(post => (
                <div key={post.id} className="post-item recent-post" onClick={() => navigate(`/post/${post.id}`)}>
                  <div className="post-content">
                    <h4 className="post-title">{post.title}</h4>
                    <div className="post-meta">
                      <span className="author">@{post.author}</span>
                      <span className="time">{post.time}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* 右侧侧边栏 */}
        <div className="sidebar">
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
          {isLoggedIn && (
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