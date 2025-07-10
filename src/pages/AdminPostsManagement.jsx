import React, { useState, useEffect } from 'react';
import { 
  FileTextOutlined, 
  SearchOutlined, 
  EyeOutlined, 
  DeleteOutlined, 
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  FilterOutlined,
  CalendarOutlined,
  UserOutlined,
  LikeOutlined,
  MessageOutlined,
  FlagOutlined,
  ReloadOutlined,
  DownOutlined,
  UpOutlined
} from '@ant-design/icons';
import { 
  getAdminPosts, 
  approvePost, 
  banPost, 
  deletePost 
} from '../service/community_common';
import '../css/AdminPostsManagement.css';

const AdminPostsManagement = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [typeFilter, setTypeFilter] = useState('all');
  const [selectedPost, setSelectedPost] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [postsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [operatingPostId, setOperatingPostId] = useState(null);
  const [expandedPostId, setExpandedPostId] = useState(null);

  // 获取帖子数据
  const fetchPosts = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const options = {
        page: currentPage,
        size: postsPerPage,
        sortBy: 'latest'
      };
      
      if (searchTerm) options.keyword = searchTerm;
      if (typeFilter !== 'all') options.category = typeFilter;
      
      const data = await getAdminPosts(options);
      const postsArray = Array.isArray(data) ? data : (data?.content || []);
      setPosts(postsArray);
      setTotalPages(data?.totalPages || 0);
    } catch (err) {
      console.error('获取帖子列表失败:', err);
      setError('获取帖子列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [currentPage, searchTerm, typeFilter]);

  // 筛选帖子
  const filteredPosts = Array.isArray(posts) ? posts.filter(post => {
    const matchesStatus = statusFilter === 'all' || 
      (statusFilter === 'published' && post.reviewStatus === 'APPROVED') ||
      (statusFilter === 'pending' && post.reviewStatus === 'PENDING') ||
      (statusFilter === 'reported' && post.reviewStatus === 'REPORTED') ||
      (statusFilter === 'rejected' && post.reviewStatus === 'BANNED');
    
    return matchesStatus;
  }) : [];

  // 统计数据
  const stats = {
    total: Array.isArray(posts) ? posts.length : 0,
    published: Array.isArray(posts) ? posts.filter(p => p.reviewStatus === 'APPROVED').length : 0,
    pending: Array.isArray(posts) ? posts.filter(p => p.reviewStatus === 'PENDING').length : 0,
    reported: Array.isArray(posts) ? posts.filter(p => p.reviewStatus === 'REPORTED').length : 0,
    rejected: Array.isArray(posts) ? posts.filter(p => p.reviewStatus === 'BANNED').length : 0
  };

  // 审核通过
  const handleApprovePost = async (postId) => {
    try {
      setOperatingPostId(postId);
      await approvePost(postId);
      
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, reviewStatus: 'APPROVED' } : post
      ));
      console.log('帖子已审核通过:', postId);
    } catch (err) {
      console.error('审核通过失败:', err);
      setError('审核通过失败');
    } finally {
      setOperatingPostId(null);
    }
  };

  // 审核拒绝
  const handleRejectPost = async (postId) => {
    try {
      setOperatingPostId(postId);
      await banPost(postId);
      
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, reviewStatus: 'BANNED' } : post
      ));
      console.log('帖子已审核拒绝:', postId);
    } catch (err) {
      console.error('审核拒绝失败:', err);
      setError('审核拒绝失败');
    } finally {
      setOperatingPostId(null);
    }
  };

  // 删除帖子
  const handleDeletePost = async (postId) => {
    if (window.confirm('确定要删除这个帖子吗？此操作不可恢复。')) {
      try {
        setOperatingPostId(postId);
        await deletePost(postId);
        
        fetchPosts();
        console.log('帖子已删除:', postId);
      } catch (err) {
        console.error('删除帖子失败:', err);
        setError('删除帖子失败');
      } finally {
        setOperatingPostId(null);
      }
    }
  };

  // 获取状态显示
  const getStatusBadge = (reviewStatus) => {
    switch (reviewStatus) {
      case 'APPROVED':
        return <span className="status-badge success">已发布</span>;
      case 'PENDING':
        return <span className="status-badge warning">待审核</span>;
      case 'BANNED':
        return <span className="status-badge rejected">已拒绝</span>;
      default:
        return <span className="status-badge default">未知</span>;
    }
  };

  // 获取类型显示
  const getTypeDisplay = (category) => {
    switch (category) {
      case 'mood':
        return '心情分享';
      case 'help':
        return '求助互助';
      case 'share':
        return '经验分享';
      case 'other':
        return '其他';
      default:
        return category || '未知';
    }
  };

  // 格式化时间
  const formatDate = (dateString) => {
    if (!dateString) return '未知';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  };

  // 截断文本
  const truncateText = (text, maxLength = 150) => {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  };

  // 展开/收起帖子内容
  const togglePostExpansion = (postId) => {
    setExpandedPostId(expandedPostId === postId ? null : postId);
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
    <div className="admin-posts-management-container">
      <div className="admin-card">
        <div className="admin-card-title">
          <FileTextOutlined />
          帖子管理与审核
        </div>
        
        {/* 错误提示 */}
        {error && (
          <div className="admin-error-message">
            {error}
            <button onClick={() => setError(null)} className="admin-error-close">×</button>
          </div>
        )}
        
        {/* 控制面板 */}
        <div className="admin-controls">
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
          
          <div className="admin-filter-group">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">全部状态</option>
              <option value="published">已发布</option>
              <option value="pending">待审核</option>
              <option value="rejected">已拒绝</option>
            </select>
          </div>
          
          <div className="admin-filter-group">
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">全部类型</option>
              <option value="mood">心情分享</option>
              <option value="help">求助互助</option>
              <option value="share">经验分享</option>
              <option value="other">其他</option>
            </select>
          </div>
          
          <button 
            onClick={fetchPosts}
            disabled={loading}
            className="admin-btn admin-btn-primary"
          >
            <ReloadOutlined />
            刷新
          </button>
        </div>
        
        {/* 统计信息 */}
        <div className="admin-stats">
          <div className="admin-stat-item">
            <span className="admin-stat-label">总帖子数</span>
            <span className="admin-stat-value">{stats.total}</span>
          </div>
          <div className="admin-stat-item">
            <span className="admin-stat-label">已发布</span>
            <span className="admin-stat-value success">{stats.published}</span>
          </div>
          <div className="admin-stat-item">
            <span className="admin-stat-label">待审核</span>
            <span className="admin-stat-value warning">{stats.pending}</span>
          </div>
          <div className="admin-stat-item">
            <span className="admin-stat-label">已拒绝</span>
            <span className="admin-stat-value rejected">{stats.rejected}</span>
          </div>
        </div>
        
        {/* 帖子列表 */}
        <div className="admin-posts-list">
          {filteredPosts.length === 0 ? (
            <div className="admin-empty-state">
              <FileTextOutlined />
              <p>暂无帖子数据</p>
            </div>
          ) : (
            filteredPosts.map(post => (
              <div key={post.id} className="admin-post-item">
                <div className="admin-post-header">
                  <div className="admin-post-meta">
                    <span className="admin-post-type">{getTypeDisplay(post.category)}</span>
                    <span className="admin-post-time">
                      <CalendarOutlined />
                      {formatDate(post.createdAt)}
                    </span>
                  </div>
                  <div className="admin-post-status">
                    {getStatusBadge(post.reviewStatus)}
                  </div>
                </div>
                
                <div className="admin-post-content">
                  <div className="admin-post-info">
                    <h4 className="admin-post-title">{post.title}</h4>
                    
                    <div className="admin-post-details">
                      <span className="admin-post-author">
                        <UserOutlined />
                        {post.userDisplayName || post.author || '未知用户'}
                      </span>
                      <div className="admin-post-stats">
                        <span>
                          <LikeOutlined />
                          {post.likes || 0}
                        </span>
                        <span>
                          <MessageOutlined />
                          {post.comments || 0}
                        </span>
                        {post.reports > 0 && (
                          <span className="admin-post-reports">
                            <FlagOutlined />
                            {post.reports}
                          </span>
                        )}
                      </div>
                    </div>
                    
                    <div className="admin-post-text">
                      {expandedPostId === post.id ? (
                        <div className="admin-post-full-content">
                          {post.content}
                          <button 
                            onClick={() => togglePostExpansion(post.id)}
                            className="admin-toggle-btn"
                          >
                            <UpOutlined />
                            收起
                          </button>
                        </div>
                      ) : (
                        <>
                          {truncateText(post.content)}
                          {post.content && post.content.length > 150 && (
                            <button 
                              onClick={() => togglePostExpansion(post.id)}
                              className="admin-toggle-btn"
                            >
                              <DownOutlined />
                              展开
                            </button>
                          )}
                        </>
                      )}
                    </div>
                  </div>
                  
                  <div className="admin-post-actions">
                    {post.reviewStatus === 'PENDING' && (
                      <>
                        <button
                          onClick={() => handleApprovePost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-success"
                        >
                          <CheckCircleOutlined />
                          审核通过
                        </button>
                        <button
                          onClick={() => handleRejectPost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-danger"
                        >
                          <CloseCircleOutlined />
                          审核拒绝
                        </button>
                      </>
                    )}
                    
                    {post.reviewStatus === 'APPROVED' && (
                      <button
                        onClick={() => handleRejectPost(post.id)}
                        disabled={operatingPostId === post.id}
                        className="admin-btn admin-btn-warning"
                      >
                        <ExclamationCircleOutlined />
                        取消发布
                      </button>
                    )}
                    
                    {post.reviewStatus === 'BANNED' && (
                      <button
                        onClick={() => handleApprovePost(post.id)}
                        disabled={operatingPostId === post.id}
                        className="admin-btn admin-btn-success"
                      >
                        <CheckCircleOutlined />
                        恢复发布
                      </button>
                    )}
                    
                    <button
                      onClick={() => handleDeletePost(post.id)}
                      disabled={operatingPostId === post.id}
                      className="admin-btn admin-btn-danger"
                    >
                      <DeleteOutlined />
                      删除
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
              onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
              disabled={currentPage === 1}
              className="admin-btn admin-btn-secondary"
            >
              上一页
            </button>
            
            <span className="admin-page-info">
              第 {currentPage} 页 / 共 {totalPages} 页
            </span>
            
            <button
              onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
              disabled={currentPage === totalPages}
              className="admin-btn admin-btn-secondary"
            >
              下一页
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPostsManagement; 