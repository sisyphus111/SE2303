import React, { useState, useEffect, useRef } from 'react';
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
  UpOutlined,
  TagOutlined
} from '@ant-design/icons';
import { 
  getAdminPosts, 
  approvePost, 
  banPost, 
  deletePost 
} from '../service/community_common';
import { getUserAdminInfo } from '../service/userService';
import { Modal, Spin } from 'antd';
import '../css/AdminPostsManagement.css';

const AdminPostsManagement = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
  const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedTags, setSelectedTags] = useState([]); // 修改：多选标签
  const [selectedPost, setSelectedPost] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [postsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [operatingPostId, setOperatingPostId] = useState(null);
  const [expandedPostId, setExpandedPostId] = useState(null);
  const searchInputRef = useRef(null);
  const clearButtonRef = useRef(null);
  const [userDetailModal, setUserDetailModal] = useState({ visible: false, loading: false, user: null });

  // 定义标签选项
  const tagOptions = [
    { value: '情感', label: '情感' },
    { value: '学习', label: '学习' },
    { value: '生活', label: '生活' },
    { value: '健康', label: '健康' },
    { value: '成长', label: '成长' }
  ];

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
      
      if (activeSearchTerm) options.keyword = activeSearchTerm;
      if (selectedTags.length > 0) options.category = selectedTags.join(',');
      // 新增：根据statusFilter设置reviewStatus
      if (statusFilter === 'all') {
        options.reviewStatus = 'ALL';
      } else if (statusFilter === 'published') {
        options.reviewStatus = 'APPROVED';
      } else if (statusFilter === 'pending') {
        options.reviewStatus = 'PENDING';
      } else if (statusFilter === 'rejected') {
        options.reviewStatus = 'BANNED';
      }
      
      // 根据状态筛选器设置reviewStatus参数
      if (statusFilter === 'all') {
        options.reviewStatus = 'ALL'; // 获取所有状态的帖子
      } else if (statusFilter === 'published') {
        options.reviewStatus = 'APPROVED';
      } else if (statusFilter === 'pending') {
        options.reviewStatus = 'PENDING';
      } else if (statusFilter === 'rejected') {
        options.reviewStatus = 'BANNED';
      }
      
      const data = await getAdminPosts(options);
      console.log('获取的帖子数据:', data);
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
  }, [currentPage, activeSearchTerm, selectedTags, statusFilter]);

  // 帖子列表（状态筛选已通过后端实现）
  const filteredPosts = Array.isArray(posts) ? posts : [];

  // 统计数据（基于当前筛选后的帖子列表）
  const stats = {
    total: filteredPosts.length,
    published: filteredPosts.filter(p => p.reviewStatus === 'APPROVED').length,
    pending: filteredPosts.filter(p => p.reviewStatus === 'PENDING').length,
    reported: filteredPosts.filter(p => p.reviewStatus === 'REPORTED').length,
    rejected: filteredPosts.filter(p => p.reviewStatus === 'BANNED').length
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

  // 查看用户详情
  const handleViewUserDetail = async (userId) => {
    setUserDetailModal({ visible: true, loading: true, user: null });
    try {
      const user = await getUserAdminInfo(userId);
      // 输出user
      console.log('user:', user);
      setUserDetailModal({ visible: true, loading: false, user });
    } catch (err) {
      setUserDetailModal({ visible: true, loading: false, user: { error: '获取用户信息失败' } });
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
  const getTypeDisplay = (tags) => {
    if (!tags) return '未分类';
    
    // 如果tags是数组，显示所有标签
    if (Array.isArray(tags)) {
      return tags.join(', ');
    }
    
    // 如果tags是字符串，直接返回
    if (typeof tags === 'string') {
      return tags;
    }
    
    return '未分类';
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

  // 处理搜索回车键
  const handleSearchKeyPress = (e) => {
    if (e.key === 'Enter') {
      setActiveSearchTerm(searchTerm.trim());
      setCurrentPage(1); // 重置到第一页
    }
  };

  // 清除搜索
  const handleClearSearch = () => {
    setSearchTerm('');
    setActiveSearchTerm('');
    setCurrentPage(1);
  };

  // 动态调整清除按钮位置
  const updateClearButtonPosition = () => {
    // 使用 setTimeout 确保组件重新渲染完成后再计算位置
    setTimeout(() => {
      if (searchInputRef.current && clearButtonRef.current && activeSearchTerm) {
        const input = searchInputRef.current;
        const clearBtn = clearButtonRef.current;
        
        // 创建临时元素来测量文字宽度
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        const computedStyle = window.getComputedStyle(input);
        context.font = `${computedStyle.fontSize} ${computedStyle.fontFamily}`;
        
        // 使用当前输入框中显示的文字宽度
        const displayedText = searchTerm || activeSearchTerm;
        const textWidth = context.measureText(displayedText).width;
        
        // 计算按钮位置（左边距 + 搜索图标宽度 + 文字宽度 + 一点间隙）
        const leftPosition = 40 + textWidth + 8;
        
        // 确保按钮不超出输入框右边界
        const maxLeft = input.offsetWidth - 32;
        const finalLeft = Math.min(leftPosition, maxLeft);
        
        clearBtn.style.left = `${finalLeft}px`;
      }
    }, 50); // 给一个小的延迟确保DOM渲染完成
  };

  // 监听搜索词变化，更新清除按钮位置
  useEffect(() => {
    // 确保在数据加载完成后更新按钮位置
    if (!loading) {
      updateClearButtonPosition();
    }
  }, [searchTerm, activeSearchTerm, loading]);

  // 处理标签选择
  const handleTagToggle = (tag) => {
    setSelectedTags(prev => 
      prev.includes(tag) 
        ? prev.filter(t => t !== tag)
        : [...prev, tag]
    );
    setCurrentPage(1); // 重置到第一页
  };

  // 清除所有标签筛选
  const clearTagFilters = () => {
    setSelectedTags([]);
    setCurrentPage(1);
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
              ref={searchInputRef}
              type="text"
              placeholder="搜索帖子标题、内容"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={handleSearchKeyPress}
              className="admin-search-input"
            />
            {activeSearchTerm && (
              <button 
                ref={clearButtonRef}
                onClick={handleClearSearch}
                className="admin-search-clear-btn"
                title="清除搜索"
              >
                ×
              </button>
            )}
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
          
          {/* 多选标签筛选 */}
          <div className="admin-tags-filter">
            <div className="admin-tags-header">
              <TagOutlined />
              <span>标签筛选:</span>
              {selectedTags.length > 0 && (
                <button 
                  onClick={clearTagFilters}
                  className="admin-clear-tags-btn"
                  title="清除所有标签"
                >
                  清除 ({selectedTags.length})
                </button>
              )}
            </div>
            <div className="admin-tags-list">
              {tagOptions.map(tag => (
                <button
                  key={tag.value}
                  onClick={() => handleTagToggle(tag.value)}
                  className={`admin-tag-btn ${selectedTags.includes(tag.value) ? 'selected' : ''}`}
                >
                  {tag.label}
                </button>
              ))}
            </div>
          </div>
          
          <div className="admin-filter-group">
            <button 
              onClick={fetchPosts}
              disabled={loading}
              className="admin-btn admin-btn-primary"
            >
              <ReloadOutlined />
              刷新
            </button>
          </div>
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
                    <span className="admin-post-type">{getTypeDisplay(post.tags)}</span>
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
                        {`用户ID:${post.userId}` || '未知用户'}
                        <span
                          style={{ marginLeft: 8, color: '#1677ff', cursor: 'pointer', fontSize: 12, display: 'inline-flex', alignItems: 'center' }}
                          onClick={() => handleViewUserDetail(post.userId)}
                        >
                          <EyeOutlined style={{ fontSize: 13, marginRight: 2 }} />查看详情
                        </span>
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

      {/* 用户详情弹窗 */}
      <Modal
        open={userDetailModal.visible}
        title="用户详细信息"
        onCancel={() => setUserDetailModal({ visible: false, loading: false, user: null })}
        footer={null}
        width={400}
      >
        {userDetailModal.loading ? (
          <div style={{ textAlign: 'center', padding: '32px 0' }}><Spin /> 加载中...</div>
        ) : userDetailModal.user && !userDetailModal.user.error ? (
          <div style={{ fontSize: 14 }}>
            <div><b>用户名：</b>{userDetailModal.user.username}</div>
            <div><b>真实姓名：</b>{userDetailModal.user.realname}</div>
            <div><b>手机号：</b>{userDetailModal.user.phone}</div>
            <div><b>学号：</b>{userDetailModal.user.sjtuId}</div>
            <div><b>Jaccount：</b>{userDetailModal.user.jaccount}</div>
            <div><b>注册时间：</b>{userDetailModal.user.registrationDate ? new Date(userDetailModal.user.registrationDate).toLocaleString() : '-'}</div>
            <div><b>身份：</b>{userDetailModal.user.isUser ? '普通用户' : '管理员'}</div>
            <div><b>状态：</b>{userDetailModal.user.valid ? '启用' : '禁用'}</div>
          </div>
        ) : (
          <div style={{ color: 'red', textAlign: 'center' }}>{userDetailModal.user?.error || '未找到用户信息'}</div>
        )}
      </Modal>
    </div>
  );
};

export default AdminPostsManagement;