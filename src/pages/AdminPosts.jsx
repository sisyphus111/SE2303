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
import { 
  getAdminPosts, 
  approvePost, 
  banPost, 
  deletePost 
} from '../service/community_common';
import '../css/AdminPosts.css';

const AdminPosts = () => {
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
      // 确保 posts 是数组
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

  // 筛选帖子（现在主要用于本地状态筛选）
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
    reported: Array.isArray(posts) ? posts.filter(p => p.reviewStatus === 'REPORTED').length : 0
  };

  // 帖子操作
  const handleViewPost = (post) => {
    setSelectedPost(post);
    setShowModal(true);
  };

  const handleApprovePost = async (postId) => {
    try {
      setOperatingPostId(postId);
      await approvePost(postId);
      
      // 更新本地状态
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, reviewStatus: 'APPROVED' } : post
      ));
      console.log('帖子已批准:', postId);
    } catch (err) {
      console.error('批准帖子失败:', err);
      setError('批准帖子失败');
    } finally {
      setOperatingPostId(null);
    }
  };

  const handleRejectPost = async (postId) => {
    try {
      setOperatingPostId(postId);
      await banPost(postId);
      
      // 更新本地状态
      setPosts(posts.map(post => 
        post.id === postId ? { ...post, reviewStatus: 'BANNED' } : post
      ));
      console.log('帖子已拒绝:', postId);
    } catch (err) {
      console.error('拒绝帖子失败:', err);
      setError('拒绝帖子失败');
    } finally {
      setOperatingPostId(null);
    }
  };

  const handleDeletePost = async (postId) => {
    if (window.confirm('确定要删除这个帖子吗？此操作不可恢复。')) {
      try {
        setOperatingPostId(postId);
        await deletePost(postId);
        
        // 重新获取数据
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
        return <span className="admin-badge success">已发布</span>;
      case 'PENDING':
        return <span className="admin-badge warning">待审核</span>;
      case 'REPORTED':
        return <span className="admin-badge danger">被举报</span>;
      case 'BANNED':
        return <span className="admin-badge info">已拒绝</span>;
      default:
        return <span className="admin-badge info">未知</span>;
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
        
        {/* 错误提示 */}
        {error && (
          <div className="admin-error-message">
            {error}
            <button onClick={() => setError(null)} className="admin-error-close">×</button>
          </div>
        )}
        
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
          
          <button 
            onClick={fetchPosts}
            disabled={loading}
            className="admin-btn admin-btn-primary"
          >
            刷新
          </button>
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
          {filteredPosts.length === 0 ? (
            <div className="admin-empty-state">
              <FileTextOutlined />
              <p>暂无帖子数据</p>
            </div>
          ) : (
            filteredPosts.map((post) => (
              <div key={post.id} className="admin-post-item">
                <div className="admin-post-header">
                  <div className="admin-post-title">
                    <h4>{post.title}</h4>
                    <div className="admin-post-meta">
                      <span className="admin-post-type">{getTypeDisplay(post.category)}</span>
                      {getStatusBadge(post.reviewStatus)}
                    </div>
                  </div>
                  <div className="admin-post-time">
                    <CalendarOutlined /> {formatDate(post.createdAt)}
                  </div>
                </div>
                
                <div className="admin-post-content">
                  <div className="admin-post-info">
                    <p className="admin-post-text">
                      {post.content && post.content.length > 100 ? 
                        `${post.content.substring(0, 100)}...` : 
                        post.content || '无内容'
                      }
                    </p>
                    <div className="admin-post-details">
                      <div className="admin-post-author">
                        <UserOutlined /> {post.username || '未知用户'}
                      </div>
                      <div className="admin-post-stats">
                        <span><LikeOutlined /> {post.postMetrics?.likeCount || 0}</span>
                        <span><MessageOutlined /> {post.postMetrics?.commentCount || 0}</span>
                        {post.reportCount > 0 && (
                          <span className="admin-post-reports">
                            <FlagOutlined /> {post.reportCount}
                          </span>
                        )}
                      </div>
                    </div>
                    <div className="admin-post-tags">
                      {post.tags && post.tags.map((tag, index) => (
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
                    
                    {post.reviewStatus === 'PENDING' && (
                      <>
                        <button
                          onClick={() => handleApprovePost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-success"
                        >
                          {operatingPostId === post.id ? (
                            <div className="admin-btn-loading"></div>
                          ) : (
                            <>
                              <CheckCircleOutlined /> 批准
                            </>
                          )}
                        </button>
                        <button
                          onClick={() => handleRejectPost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-secondary"
                        >
                          {operatingPostId === post.id ? (
                            <div className="admin-btn-loading"></div>
                          ) : (
                            <>
                              <ExclamationCircleOutlined /> 拒绝
                            </>
                          )}
                        </button>
                      </>
                    )}
                    
                    {post.reviewStatus === 'REPORTED' && (
                      <>
                        <button
                          onClick={() => handleApprovePost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-success"
                        >
                          {operatingPostId === post.id ? (
                            <div className="admin-btn-loading"></div>
                          ) : (
                            <>
                              <CheckCircleOutlined /> 恢复
                            </>
                          )}
                        </button>
                        <button
                          onClick={() => handleRejectPost(post.id)}
                          disabled={operatingPostId === post.id}
                          className="admin-btn admin-btn-secondary"
                        >
                          {operatingPostId === post.id ? (
                            <div className="admin-btn-loading"></div>
                          ) : (
                            <>
                              <ExclamationCircleOutlined /> 下架
                            </>
                          )}
                        </button>
                      </>
                    )}
                    
                    <button
                      onClick={() => handleDeletePost(post.id)}
                      disabled={operatingPostId === post.id}
                      className="admin-btn admin-btn-danger"
                    >
                      {operatingPostId === post.id ? (
                        <div className="admin-btn-loading"></div>
                      ) : (
                        <>
                          <DeleteOutlined /> 删除
                        </>
                      )}
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
              disabled={currentPage === 1 || loading}
              className="admin-btn admin-btn-secondary"
            >
              上一页
            </button>
            
            <span className="admin-page-info">
              第 {currentPage} 页，共 {totalPages} 页
            </span>
            
            <button
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages || loading}
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
                <strong>作者:</strong> {selectedPost.username} (ID: {selectedPost.userId})
              </div>
              
              <div className="admin-detail-item">
                <strong>类型:</strong> {getTypeDisplay(selectedPost.category)}
              </div>
              
              <div className="admin-detail-item">
                <strong>状态:</strong> {getStatusBadge(selectedPost.reviewStatus)}
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
                  <span><LikeOutlined /> 点赞: {selectedPost.postMetrics?.likeCount || 0}</span>
                  <span><MessageOutlined /> 评论: {selectedPost.postMetrics?.commentCount || 0}</span>
                  <span><FlagOutlined /> 举报: {selectedPost.reportCount || 0}</span>
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>标签:</strong>
                <div className="admin-post-tags">
                  {selectedPost.tags && selectedPost.tags.map((tag, index) => (
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