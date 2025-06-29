import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  EditOutlined, 
  SearchOutlined,
  FileTextOutlined,
  HeartOutlined,
  HeartFilled,
  MessageOutlined,
  EyeOutlined,
  UserOutlined
} from '@ant-design/icons';
import '../css/Community.css';

// API服务导入注释
// import { getPostList, togglePostLike, createPost } from '../service/communityService';

const Community = () => {
  const [posts, setPosts] = useState([
    {
      id: 1,
      title: '如何缓解工作压力？',
      content: '最近工作压力很大，想和大家分享一下我的减压方法，也希望能听到大家的建议...',
      author: '小明',
      authorAvatar: 'M',
      category: '压力管理',
      tags: ['工作压力', '减压方法', '心理健康'],
      likes: 24,
      comments: 8,
      views: 156,
      createdAt: '2024-01-15 14:30',
      isLiked: false
    },
    {
      id: 2,
      title: '分享一个让我心情变好的小习惯',
      content: '每天早上起床后，我会花5分钟时间做深呼吸和简单的伸展运动，这让我一整天都感觉很好...',
      author: '小红',
      authorAvatar: 'H',
      category: '心情分享',
      tags: ['好习惯', '晨间routine', '积极心态'],
      likes: 42,
      comments: 15,
      views: 289,
      createdAt: '2024-01-14 09:15',
      isLiked: true
    },
    {
      id: 3,
      title: '关于焦虑症的一些思考',
      content: '想和大家讨论一下焦虑症这个话题，希望能帮助到有类似经历的朋友...',
      author: '小李',
      authorAvatar: 'L',
      category: '心理健康',
      tags: ['焦虑症', '心理治疗', '互助'],
      likes: 18,
      comments: 12,
      views: 203,
      createdAt: '2024-01-13 16:45',
      isLiked: false
    },
    {
      id: 4,
      title: '推荐几本治愈系书籍',
      content: '最近读了几本很棒的心理学书籍，想推荐给大家，希望能帮助到需要的人...',
      author: '小王',
      authorAvatar: 'W',
      category: '资源推荐',
      tags: ['书籍推荐', '心理学', '治愈系'],
      likes: 31,
      comments: 6,
      views: 178,
      createdAt: '2024-01-12 11:20',
      isLiked: false
    }
  ]);

  const [filteredPosts, setFilteredPosts] = useState(posts);
  const [selectedCategory, setSelectedCategory] = useState('全部');
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('最新');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  const categories = ['全部', '心情分享', '压力管理', '心理健康', '资源推荐', '互助交流'];

  useEffect(() => {
    // 加载帖子列表
    loadPosts();
  }, []);

  // 加载帖子列表的函数
  const loadPosts = async () => {
    // API调用注释：获取帖子列表
    // try {
    //   const params = {
    //     page: 1,
    //     size: 20,
    //     category: selectedCategory !== '全部' ? selectedCategory : undefined,
    //     keyword: searchTerm || undefined,
    //     sortBy: sortBy
    //   };
    //   const response = await getPostList(params);
    //   setPosts(response.data);
    //   setFilteredPosts(response.data);
    // } catch (error) {
    //   console.error('获取帖子列表失败:', error);
    //   // 使用模拟数据作为备选
    // }
  };

  useEffect(() => {
    let filtered = posts;

    // 按分类筛选
    if (selectedCategory !== '全部') {
      filtered = filtered.filter(post => post.category === selectedCategory);
    }

    // 按搜索词筛选
    if (searchTerm.trim()) {
      filtered = filtered.filter(post => 
        post.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        post.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
        post.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
      );
    }

    // 排序
    switch (sortBy) {
      case '最新':
        filtered = [...filtered].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        break;
      case '最热':
        filtered = [...filtered].sort((a, b) => b.likes - a.likes);
        break;
      case '最多评论':
        filtered = [...filtered].sort((a, b) => b.comments - a.comments);
        break;
      case '最多浏览':
        filtered = [...filtered].sort((a, b) => b.views - a.views);
        break;
      default:
        break;
    }

    setFilteredPosts(filtered);
  }, [posts, selectedCategory, searchTerm, sortBy]);

  const handleLike = async (postId) => {
    if (!isLoggedIn) {
      alert('请先登录');
      navigate('/login');
      return;
    }

    // API调用注释：点赞/取消点赞帖子
    // try {
    //   await togglePostLike(postId);
    //   // 更新本地状态
    //   setPosts(prev => prev.map(post => {
    //     if (post.id === postId) {
    //       return {
    //         ...post,
    //         likes: post.isLiked ? post.likes - 1 : post.likes + 1,
    //         isLiked: !post.isLiked
    //       };
    //     }
    //     return post;
    //   }));
    // } catch (error) {
    //   console.error('点赞操作失败:', error);
    //   alert('操作失败，请重试');
    // }

    setPosts(prev => prev.map(post => {
      if (post.id === postId) {
        return {
          ...post,
          likes: post.isLiked ? post.likes - 1 : post.likes + 1,
          isLiked: !post.isLiked
        };
      }
      return post;
    }));
  };

  // 创建新帖子的函数
  const handleCreatePost = async (postData) => {
    // API调用注释：创建帖子
    // try {
    //   const newPost = await createPost(postData);
    //   setPosts(prev => [newPost.data, ...prev]);
    //   alert('帖子发布成功！');
    //   navigate('/community');
    // } catch (error) {
    //   console.error('发布帖子失败:', error);
    //   alert('发布失败，请重试');
    // }
  };

  const formatTime = (timeString) => {
    const now = new Date();
    const postTime = new Date(timeString);
    const diffInHours = Math.floor((now - postTime) / (1000 * 60 * 60));
    
    if (diffInHours < 1) return '刚刚';
    if (diffInHours < 24) return `${diffInHours}小时前`;
    if (diffInHours < 48) return '昨天';
    return postTime.toLocaleDateString();
  };

  return (
    <div className="community-container">
      <div className="community-header">
        <h1>社区交流</h1>
        <p>在这里分享你的心情，获得温暖的支持</p>
        {isLoggedIn && (
          <button 
            className="new-post-btn"
            onClick={() => navigate('/new-post')}
          >
            <EditOutlined /> 发布新帖子
          </button>
        )}
      </div>

      <div className="community-filters">
        <div className="search-box">
          <input
            type="text"
            placeholder="搜索帖子、标签或内容..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <span className="search-icon"><SearchOutlined /></span>
        </div>

        <div className="filter-controls">
          <div className="category-filter">
            {categories.map(category => (
              <button
                key={category}
                className={`category-btn ${selectedCategory === category ? 'active' : ''}`}
                onClick={() => setSelectedCategory(category)}
              >
                {category}
              </button>
            ))}
          </div>

          <div className="sort-filter">
            <select 
              value={sortBy} 
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="最新">最新发布</option>
              <option value="最热">最多点赞</option>
              <option value="最多评论">最多评论</option>
              <option value="最多浏览">最多浏览</option>
            </select>
          </div>
        </div>
      </div>

      <div className="posts-container">
        {filteredPosts.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon"><FileTextOutlined /></div>
            <p>没有找到相关帖子</p>
            <button 
              className="start-post-btn"
              onClick={() => navigate('/new-post')}
            >
              发布第一个帖子
            </button>
          </div>
        ) : (
          <div className="posts-list">
            {filteredPosts.map(post => (
              <div key={post.id} className="post-card">
                <div className="post-header">
                  <div className="post-author">
                    <div className="author-avatar">
                      <span>{post.authorAvatar}</span>
                    </div>
                    <div className="author-info">
                      <span className="author-name">{post.author}</span>
                      <span className="post-time">{formatTime(post.createdAt)}</span>
                    </div>
                  </div>
                  <div className="post-category">
                    <span className="category-tag">{post.category}</span>
                  </div>
                </div>

                <div className="post-content" onClick={() => navigate(`/post/${post.id}`)}>
                  <h3 className="post-title">{post.title}</h3>
                  <p className="post-excerpt">{post.content}</p>
                  <div className="post-tags">
                    {post.tags.map(tag => (
                      <span key={tag} className="tag">{tag}</span>
                    ))}
                  </div>
                </div>

                <div className="post-footer">
                  <div className="post-stats">
                    <button 
                      className={`like-btn ${post.isLiked ? 'liked' : ''}`}
                      onClick={() => handleLike(post.id)}
                    >
                      {post.isLiked ? <HeartFilled /> : <HeartOutlined />}
                      <span>{post.likes}</span>
                    </button>
                    <div className="stat-item">
                      <MessageOutlined />
                      <span>{post.comments}</span>
                    </div>
                    <div className="stat-item">
                      <EyeOutlined />
                      <span>{post.views}</span>
                    </div>
                  </div>
                  <button 
                    className="read-more-btn"
                    onClick={() => navigate(`/post/${post.id}`)}
                  >
                    阅读全文
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Community; 