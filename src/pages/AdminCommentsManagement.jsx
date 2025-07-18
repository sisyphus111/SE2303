import React, { useState, useEffect, useRef } from 'react';
import { 
  MessageOutlined, 
  SearchOutlined, 
  EyeOutlined, 
  DeleteOutlined, 
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  CalendarOutlined,
  UserOutlined,
  LikeOutlined,
  FlagOutlined,
  ReloadOutlined,
  DownOutlined,
  UpOutlined,
  FileTextOutlined
} from '@ant-design/icons';
import { 
  getAdminComments, 
  approveComment, 
  banComment, 
  deleteComment 
} from '../service/community_common';
import { getUserAdminInfo } from '../service/userService';
import { Modal, Spin } from 'antd';
import '../css/AdminCommentsManagement.css';

const AdminCommentsManagement = () => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
  const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
  const [statusFilter, setStatusFilter] = useState('all');
  const [currentPage, setCurrentPage] = useState(1);
  const [commentsPerPage] = useState(15);
  const [totalPages, setTotalPages] = useState(0);
  const [operatingCommentId, setOperatingCommentId] = useState(null);
  const [expandedCommentId, setExpandedCommentId] = useState(null);
  const searchInputRef = useRef(null);
  const clearButtonRef = useRef(null);
  const [userDetailModal, setUserDetailModal] = useState({ visible: false, loading: false, user: null });

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

  // 获取评论数据
  const fetchComments = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const options = {
        page: currentPage,
        size: commentsPerPage,
        sortBy: 'latest'
      };
      
      if (activeSearchTerm) options.keyword = activeSearchTerm;
      
      const data = await getAdminComments(options);
      const commentsArray = Array.isArray(data) ? data : (data?.content || []);
      setComments(commentsArray);
      setTotalPages(data?.totalPages || 0);
    } catch (err) {
      console.error('获取评论列表失败:', err);
      setError('获取评论列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [currentPage, activeSearchTerm]);

  // 筛选评论
  const filteredComments = Array.isArray(comments) ? comments.filter(comment => {
    const matchesStatus = statusFilter === 'all' || 
      (statusFilter === 'approved' && comment.reviewStatus === 'APPROVED') ||
      (statusFilter === 'pending' && comment.reviewStatus === 'PENDING') ||
      (statusFilter === 'reported' && comment.reviewStatus === 'REPORTED') ||
      (statusFilter === 'rejected' && comment.reviewStatus === 'BANNED');
    
    return matchesStatus;
  }) : [];

  // 统计数据
  const stats = {
    total: Array.isArray(comments) ? comments.length : 0,
    approved: Array.isArray(comments) ? comments.filter(c => c.reviewStatus === 'APPROVED').length : 0,
    pending: Array.isArray(comments) ? comments.filter(c => c.reviewStatus === 'PENDING').length : 0,
    reported: Array.isArray(comments) ? comments.filter(c => c.reviewStatus === 'REPORTED').length : 0,
    rejected: Array.isArray(comments) ? comments.filter(c => c.reviewStatus === 'BANNED').length : 0
  };

  // 审核通过
  const handleApproveComment = async (commentId) => {
    try {
      setOperatingCommentId(commentId);
      await approveComment(commentId);
      
      setComments(comments.map(comment => 
        comment.id === commentId ? { ...comment, reviewStatus: 'APPROVED' } : comment
      ));
      console.log('评论已审核通过:', commentId);
    } catch (err) {
      console.error('审核通过失败:', err);
      setError('审核通过失败');
    } finally {
      setOperatingCommentId(null);
    }
  };

  // 审核拒绝
  const handleRejectComment = async (commentId) => {
    try {
      setOperatingCommentId(commentId);
      await banComment(commentId);
      
      setComments(comments.map(comment => 
        comment.id === commentId ? { ...comment, reviewStatus: 'BANNED' } : comment
      ));
      console.log('评论已审核拒绝:', commentId);
    } catch (err) {
      console.error('审核拒绝失败:', err);
      setError('审核拒绝失败');
    } finally {
      setOperatingCommentId(null);
    }
  };

  // 删除评论
  const handleDeleteComment = async (commentId) => {
    if (window.confirm('确定要删除这条评论吗？此操作不可恢复。')) {
      try {
        setOperatingCommentId(commentId);
        await deleteComment(commentId);
        
        fetchComments();
        console.log('评论已删除:', commentId);
      } catch (err) {
        console.error('删除评论失败:', err);
        setError('删除评论失败');
      } finally {
        setOperatingCommentId(null);
      }
    }
  };

  // 查看用户详情
  const handleViewUserDetail = async (userId) => {
    setUserDetailModal({ visible: true, loading: true, user: null });
    try {
      const user = await getUserAdminInfo(userId);
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

  // 格式化时间
  const formatDate = (dateString) => {
    if (!dateString) return '未知';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  };

  // 截断文本
  const truncateText = (text, maxLength = 100) => {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  };

  // 展开/收起评论内容
  const toggleCommentExpansion = (commentId) => {
    setExpandedCommentId(expandedCommentId === commentId ? null : commentId);
  };

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="admin-loading-spinner"></div>
        <p>正在加载评论数据...</p>
      </div>
    );
  }

  return (
    <div className="admin-comments-management-container">
      <div className="admin-card">
        <div className="admin-card-title">
          <MessageOutlined />
          评论管理与审核
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

              placeholder="搜索评论内容或作者..."

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
              <option value="approved">已发布</option>
              <option value="pending">待审核</option>
              <option value="rejected">已拒绝</option>
            </select>
          </div>
          
          <button 
            onClick={fetchComments}
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
            <span className="admin-stat-label">总评论数</span>
            <span className="admin-stat-value">{stats.total}</span>
          </div>
          <div className="admin-stat-item">
            <span className="admin-stat-label">已发布</span>
            <span className="admin-stat-value success">{stats.approved}</span>
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
        
        {/* 评论列表 */}
        <div className="admin-comments-list">
          {filteredComments.length === 0 ? (
            <div className="admin-empty-state">
              <MessageOutlined />
              <p>暂无评论数据</p>
            </div>
          ) : (
            filteredComments.map(comment => (
              <div key={comment.id} className="admin-comment-item">
                <div className="admin-comment-header">
                  <div className="admin-comment-meta">
                    <span className="admin-comment-type">
                      <MessageOutlined />
                      评论
                    </span>
                    <span className="admin-comment-time">
                      <CalendarOutlined />
                      {formatDate(comment.createdAt)}
                    </span>
                  </div>
                  <div className="admin-comment-status">
                    {getStatusBadge(comment.reviewStatus)}
                  </div>
                </div>
                
                <div className="admin-comment-content">
                  <div className="admin-comment-info">
                    <div className="admin-comment-details">
                      <span className="admin-comment-author">
                        <UserOutlined />
                        {`用户ID:${comment.userId}` || '未知用户'}
                        <span
                          style={{ marginLeft: 8, color: '#1677ff', cursor: 'pointer', fontSize: 12, display: 'inline-flex', alignItems: 'center' }}
                          onClick={() => handleViewUserDetail(comment.userId)}
                        >
                          <EyeOutlined style={{ fontSize: 13, marginRight: 2 }} />查看详情
                        </span>
                      </span>
                      <div className="admin-comment-stats">
                        <span>
                          <LikeOutlined />
                          {comment.likes || 0}
                        </span>
                        {comment.reports > 0 && (
                          <span className="admin-comment-reports">
                            <FlagOutlined />
                            {comment.reports}
                          </span>
                        )}
                      </div>
                    </div>
                    
                    {/* 关联帖子信息 */}
                    {comment.postTitle && (
                      <div className="admin-comment-post">
                        <FileTextOutlined />
                        <span>回复帖子: {comment.postTitle}</span>
                      </div>
                    )}
                    
                    <div className="admin-comment-text">
                      {expandedCommentId === comment.id ? (
                        <div className="admin-comment-full-content">
                          {comment.content}
                          <button 
                            onClick={() => toggleCommentExpansion(comment.id)}
                            className="admin-toggle-btn"
                          >
                            <UpOutlined />
                            收起
                          </button>
                        </div>
                      ) : (
                        <>
                          {truncateText(comment.content)}
                          {comment.content && comment.content.length > 100 && (
                            <button 
                              onClick={() => toggleCommentExpansion(comment.id)}
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
                  
                  <div className="admin-comment-actions">
                    {comment.reviewStatus === 'PENDING' && (
                      <>
                        <button
                          onClick={() => handleApproveComment(comment.id)}
                          disabled={operatingCommentId === comment.id}
                          className="admin-btn admin-btn-success"
                        >
                          <CheckCircleOutlined />
                          审核通过
                        </button>
                        <button
                          onClick={() => handleRejectComment(comment.id)}
                          disabled={operatingCommentId === comment.id}
                          className="admin-btn admin-btn-danger"
                        >
                          <CloseCircleOutlined />
                          审核拒绝
                        </button>
                      </>
                    )}
                    
                    {comment.reviewStatus === 'APPROVED' && (
                      <button
                        onClick={() => handleRejectComment(comment.id)}
                        disabled={operatingCommentId === comment.id}
                        className="admin-btn admin-btn-warning"
                      >
                        <ExclamationCircleOutlined />
                        取消发布
                      </button>
                    )}
                    
                    {comment.reviewStatus === 'BANNED' && (
                      <button
                        onClick={() => handleApproveComment(comment.id)}
                        disabled={operatingCommentId === comment.id}
                        className="admin-btn admin-btn-success"
                      >
                        <CheckCircleOutlined />
                        恢复发布
                      </button>
                    )}
                    
                    <button
                      onClick={() => handleDeleteComment(comment.id)}
                      disabled={operatingCommentId === comment.id}
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

export default AdminCommentsManagement; 