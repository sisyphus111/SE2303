import React, { useState, useEffect } from 'react';
import { 
  FileTextOutlined, 
  SearchOutlined, 
  EyeOutlined, 
  DeleteOutlined, 
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  FilterOutlined,
  CalendarOutlined,
  UserOutlined,
  LikeOutlined,
  MessageOutlined,
  FlagOutlined
} from '@ant-design/icons';
import '../css/AdminPosts.css';

const AdminPosts = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [typeFilter, setTypeFilter] = useState('all');
  const [selectedPost, setSelectedPost] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [postsPerPage] = useState(10);

  // 模拟帖子数据
  const mockPosts = [
    {
      id: 1,
      title: "今天的心情分享",
      content: "今天感觉心情不错，想和大家分享一下我的小确幸...",
      author: "张三",
      authorId: 101,
      type: "mood",
      status: "published",
      createdAt: "2024-01-15 14:30:00",
      updatedAt: "2024-01-15 14:30:00",
      likes: 25,
      comments: 8,
      reports: 0,
      tags: ["心情", "分享", "生活"]
    },
    {
      id: 2,
      title: "关于焦虑的一些想法",
      content: "最近总是感到焦虑，不知道是不是因为期末考试快到了...",
      author: "李四",
      authorId: 102,
      type: "help",
      status: "published",
      createdAt: "2024-01-15 10:15:00",
      updatedAt: "2024-01-15 10:15:00",
      likes: 12,
      comments: 15,
      reports: 1,
      tags: ["焦虑", "求助", "心理"]
    },
    {
      id: 3,
      title: "学习压力太大了",
      content: "感觉学习压力很大，有时候会想要放弃...",
      author: "王五",
      authorId: 103,
      type: "help",
      status: "pending",
      createdAt: "2024-01-14 16:45:00",
      updatedAt: "2024-01-14 16:45:00",
      likes: 5,
      comments: 3,
      reports: 0,
      tags: ["学习", "压力", "心理"]
    },
    {
      id: 4,
      title: "分享一些减压的方法",
      content: "给大家分享一些我平时用来减压的小方法，希望对大家有帮助...",
      author: "赵六",
      authorId: 104,
      type: "share",
      status: "published",
      createdAt: "2024-01-14 09:20:00",
      updatedAt: "2024-01-14 09:20:00",
      likes: 45,
      comments: 20,
      reports: 0,
      tags: ["减压", "方法", "分享"]
    },
    {
      id: 5,
      title: "不当内容举报",
      content: "这里包含一些不当内容，需要管理员审核...",
      author: "用户X",
      authorId: 105,
      type: "other",
      status: "reported",
      createdAt: "2024-01-13 20:30:00",
      updatedAt: "2024-01-13 20:30:00",
      likes: 2,
      comments: 1,
      reports: 5,
      tags: ["其他"]
    }
  ];

  useEffect(() => {
    // 模拟API调用
    const fetchPosts = async () => {
      setLoading(true);
      try {
        // TODO: 调用实际API获取帖子列表
        // const response = await fetch('/api/admin/posts');
        // const data = await response.json();
        // setPosts(data);
        
        // 模拟网络延迟
        setTimeout(() => {
          setPosts(mockPosts);
          setLoading(false);
        }, 1000);
      } catch (error) {
        console.error('获取帖子列表失败:', error);
        setLoading(false);
      }
    };

    fetchPosts();
  }, []);

  // 筛选帖子
  const filteredPosts = posts.filter(post => {
    const matchesSearch = post.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         post.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         post.author.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || post.status === statusFilter;
    const matchesType = typeFilter === 'all' || post.type === typeFilter;
    
    return matchesSearch && matchesStatus && matchesType;
  });

  // 分页
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = filteredPosts.slice(indexOfFirstPost, indexOfLastPost);
  const totalPages = Math.ceil(filteredPosts.length / postsPerPage);

  // 统计数据
  const stats = {
    total: posts.length,
    published: posts.filter(p => p.status === 'published').length,
    pending: posts.filter(p => p.status === 'pending').length,
    reported: posts.filter(p => p.status === 'reported').length
  };

  // 帖子操作
  const handleViewPost = (post) => {
    setSelectedPost(post);
    setShowModal(true);
  };

  const handleApprovePost = async (postId) => {
    try {
      // TODO: 调用API批准帖子
      // await fetch(`/api/admin/posts/${postId}/approve`, { method: 'POST' });
      
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, status: 'published' } : post
      ));
      console.log('帖子已批准:', postId);
    } catch (error) {
      console.error('批准帖子失败:', error);
    }
  };

  const handleRejectPost = async (postId) => {
    try {
      // TODO: 调用API拒绝帖子
      // await fetch(`/api/admin/posts/${postId}/reject`, { method: 'POST' });
      
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, status: 'rejected' } : post
      ));
      console.log('帖子已拒绝:', postId);
    } catch (error) {
      console.error('拒绝帖子失败:', error);
    }
  };

  const handleDeletePost = async (postId) => {
    if (window.confirm('确定要删除这个帖子吗？此操作不可恢复。')) {
      try {
        // TODO: 调用API删除帖子
        // await fetch(`/api/admin/posts/${postId}`, { method: 'DELETE' });
        
        setPosts(posts.filter(post => post.id !== postId));
        console.log('帖子已删除:', postId);
      } catch (error) {
        console.error('删除帖子失败:', error);
      }
    }
  };

  // 获取状态显示
  const getStatusBadge = (status) => {
    switch (status) {
      case 'published':
        return <span className="admin-badge success">已发布</span>;
      case 'pending':
        return <span className="admin-badge warning">待审核</span>;
      case 'reported':
        return <span className="admin-badge danger">被举报</span>;
      case 'rejected':
        return <span className="admin-badge info">已拒绝</span>;
      default:
        return <span className="admin-badge info">未知</span>;
    }
  };

  // 获取类型显示
  const getTypeDisplay = (type) => {
    switch (type) {
      case 'mood':
        return '心情分享';
      case 'help':
        return '求助互助';
      case 'share':
        return '经验分享';
      case 'other':
        return '其他';
      default:
        return '未知';
    }
  };

  // 格式化时间
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  };

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="admin-loading-spinner"></div>
        <p>正在加载帖子数据...</p>
      </div>
    );
  }

  return (
    <div className="admin-posts-container">
      <div className="admin-card">
        <div className="admin-card-title">
          <FileTextOutlined />
          帖子内容管理
        </div>
        
        {/* 控制面板 */}
        <div className="admin-posts-controls">
          <div className="admin-search-box">
            <SearchOutlined className="admin-search-icon" />
            <input
              type="text"
              placeholder="搜索帖子标题、内容或作者..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="admin-search-input"
            />
          </div>
          
          <div className="admin-filter-box">
            <FilterOutlined className="admin-filter-icon" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">所有状态</option>
              <option value="published">已发布</option>
              <option value="pending">待审核</option>
              <option value="reported">被举报</option>
              <option value="rejected">已拒绝</option>
            </select>
          </div>
          
          <div className="admin-filter-box">
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">所有类型</option>
              <option value="mood">心情分享</option>
              <option value="help">求助互助</option>
              <option value="share">经验分享</option>
              <option value="other">其他</option>
            </select>
          </div>
        </div>

        {/* 统计信息 */}
        <div className="admin-posts-stats">
          <div className="admin-stat-item">
            <div className="admin-stat-label">总帖子数</div>
            <div className="admin-stat-value">{stats.total}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">已发布</div>
            <div className="admin-stat-value admin-stat-published">{stats.published}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">待审核</div>
            <div className="admin-stat-value admin-stat-pending">{stats.pending}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">被举报</div>
            <div className="admin-stat-value admin-stat-reported">{stats.reported}</div>
          </div>
        </div>

        {/* 帖子列表 */}
        <div className="admin-posts-list">
          {currentPosts.length === 0 ? (
            <div className="admin-empty-state">
              <FileTextOutlined />
              <p>暂无帖子数据</p>
            </div>
          ) : (
            currentPosts.map((post) => (
              <div key={post.id} className="admin-post-item">
                <div className="admin-post-header">
                  <div className="admin-post-title">
                    <h4>{post.title}</h4>
                    <div className="admin-post-meta">
                      <span className="admin-post-type">{getTypeDisplay(post.type)}</span>
                      {getStatusBadge(post.status)}
                    </div>
                  </div>
                  <div className="admin-post-time">
                    <CalendarOutlined /> {formatDate(post.createdAt)}
                  </div>
                </div>
                
                <div className="admin-post-content">
                  <div className="admin-post-info">
                    <p className="admin-post-text">
                      {post.content.length > 100 ? 
                        `${post.content.substring(0, 100)}...` : 
                        post.content
                      }
                    </p>
                    <div className="admin-post-details">
                      <div className="admin-post-author">
                        <UserOutlined /> {post.author}
                      </div>
                      <div className="admin-post-stats">
                        <span><LikeOutlined /> {post.likes}</span>
                        <span><MessageOutlined /> {post.comments}</span>
                        {post.reports > 0 && (
                          <span className="admin-post-reports">
                            <FlagOutlined /> {post.reports}
                          </span>
                        )}
                      </div>
                    </div>
                    <div className="admin-post-tags">
                      {post.tags.map((tag, index) => (
                        <span key={index} className="admin-post-tag">#{tag}</span>
                      ))}
                    </div>
                  </div>
                  
                  <div className="admin-post-actions">
                    <button
                      onClick={() => handleViewPost(post)}
                      className="admin-btn admin-btn-primary"
                    >
                      <EyeOutlined /> 查看详情
                    </button>
                    
                    {post.status === 'pending' && (
                      <>
                        <button
                          onClick={() => handleApprovePost(post.id)}
                          className="admin-btn admin-btn-success"
                        >
                          <CheckCircleOutlined /> 批准
                        </button>
                        <button
                          onClick={() => handleRejectPost(post.id)}
                          className="admin-btn admin-btn-secondary"
                        >
                          <ExclamationCircleOutlined /> 拒绝
                        </button>
                      </>
                    )}
                    
                    {post.status === 'reported' && (
                      <>
                        <button
                          onClick={() => handleApprovePost(post.id)}
                          className="admin-btn admin-btn-success"
                        >
                          <CheckCircleOutlined /> 恢复
                        </button>
                        <button
                          onClick={() => handleRejectPost(post.id)}
                          className="admin-btn admin-btn-secondary"
                        >
                          <ExclamationCircleOutlined /> 下架
                        </button>
                      </>
                    )}
                    
                    <button
                      onClick={() => handleDeletePost(post.id)}
                      className="admin-btn admin-btn-danger"
                    >
                      <DeleteOutlined /> 删除
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* 分页 */}
        {totalPages > 1 && (
          <div className="admin-pagination">
            <button
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
              className="admin-btn admin-btn-secondary"
            >
              上一页
            </button>
            
            <span className="admin-page-info">
              第 {currentPage} 页，共 {totalPages} 页
            </span>
            
            <button
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
              className="admin-btn admin-btn-secondary"
            >
              下一页
            </button>
          </div>
        )}
      </div>

      {/* 帖子详情模态框 */}
      {showModal && selectedPost && (
        <div className="admin-modal-overlay" onClick={() => setShowModal(false)}>
          <div className="admin-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="admin-modal-header">
              <h3>帖子详情</h3>
              <button 
                onClick={() => setShowModal(false)}
                className="admin-modal-close"
              >
                ×
              </button>
            </div>
            
            <div className="admin-modal-body">
              <div className="admin-detail-item">
                <strong>标题:</strong> {selectedPost.title}
              </div>
              
              <div className="admin-detail-item">
                <strong>作者:</strong> {selectedPost.author} (ID: {selectedPost.authorId})
              </div>
              
              <div className="admin-detail-item">
                <strong>类型:</strong> {getTypeDisplay(selectedPost.type)}
              </div>
              
              <div className="admin-detail-item">
                <strong>状态:</strong> {getStatusBadge(selectedPost.status)}
              </div>
              
              <div className="admin-detail-item">
                <strong>发布时间:</strong> {formatDate(selectedPost.createdAt)}
              </div>
              
              <div className="admin-detail-item">
                <strong>更新时间:</strong> {formatDate(selectedPost.updatedAt)}
              </div>
              
              <div className="admin-detail-item">
                <strong>数据统计:</strong>
                <div className="admin-post-stats">
                  <span><LikeOutlined /> 点赞: {selectedPost.likes}</span>
                  <span><MessageOutlined /> 评论: {selectedPost.comments}</span>
                  <span><FlagOutlined /> 举报: {selectedPost.reports}</span>
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>标签:</strong>
                <div className="admin-post-tags">
                  {selectedPost.tags.map((tag, index) => (
                    <span key={index} className="admin-post-tag">#{tag}</span>
                  ))}
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>内容:</strong>
                <div className="admin-post-full-content">
                  {selectedPost.content}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPosts; 