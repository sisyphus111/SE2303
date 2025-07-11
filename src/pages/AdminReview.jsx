import React, { useState, useEffect, useRef } from 'react';
import { 
  FileTextOutlined, 
  CheckCircleOutlined, 
  CloseCircleOutlined,
  EyeOutlined,
  SearchOutlined,
  ReloadOutlined,
  MessageOutlined
} from '@ant-design/icons';
import { getReviewQueue, reviewContent, getContentDetail } from '../service/community_common';
import '../css/AdminReview.css';

const AdminReview = () => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
    const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
    const [filterStatus, setFilterStatus] = useState('all'); // all, PENDING, APPROVED, BANNED
    const [filterType, setFilterType] = useState('all'); // all, post, comment
    const [operatingContentId, setOperatingContentId] = useState(null);
    const [selectedContent, setSelectedContent] = useState(null);
    const [showDetailModal, setShowDetailModal] = useState(false);
    const searchInputRef = useRef(null);
    const clearButtonRef = useRef(null);

    // 获取审核队列
    const fetchReviewQueue = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getReviewQueue();
            // 确保 contents 是数组
            setContents(data.content || []);
        } catch (err) {
            console.error('获取审核队列失败:', err);
            setError('获取审核队列失败');
            // 确保错误时也设置为空数组
            setContents([]);
        } finally {
            setLoading(false);
        }
    };

    // 页面加载时获取审核队列
    useEffect(() => {
        fetchReviewQueue();
    }, []);

    // 审核内容
    const handleReviewAction = async (contentId, action, type, reason = '') => {
        try {
            setOperatingContentId(contentId);
            await reviewContent(contentId, action, type, reason);
            
            // 更新本地状态
            setContents(prevContents => {
                if (!Array.isArray(prevContents)) return [];
                return prevContents.map(content => 
                    content.id === contentId 
                        ? { ...content, status: action, reviewedAt: new Date().toISOString() }
                        : content
                );
            });
            console.log(`内容 ${contentId} 已${action === 'APPROVED' ? '通过' : '拒绝'}审核`);
        } catch (err) {
            console.error('审核内容失败:', err);
            setError('审核内容失败');
        } finally {
            setOperatingContentId(null);
        }
    };

    // 处理搜索回车键
    const handleSearchKeyPress = (e) => {
        if (e.key === 'Enter') {
            setActiveSearchTerm(searchTerm.trim());
        }
    };

    // 清除搜索
    const handleClearSearch = () => {
        setSearchTerm('');
        setActiveSearchTerm('');
    };

    // 动态调整清除按钮位置
    const updateClearButtonPosition = () => {
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
    };

    // 监听搜索词变化，更新清除按钮位置
    useEffect(() => {
        updateClearButtonPosition();
    }, [searchTerm, activeSearchTerm]);

    // 过滤内容列表
    const filteredContents = Array.isArray(contents) ? contents.filter(content => {
        const matchesSearch = content.title?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             content.content?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             content.author?.toLowerCase().includes(activeSearchTerm.toLowerCase());
        
        const matchesStatus = filterStatus === 'all' || content.status === filterStatus;
        const matchesType = filterType === 'all' || content.type === filterType;
        
        return matchesSearch && matchesStatus && matchesType;
    }) : [];

    // 获取状态显示文本
    const getStatusText = (status) => {
        switch (status) {
            case 'PENDING': return '待审核';
            case 'APPROVED': return '已通过';
            case 'BANNED': return '已拒绝';
            default: return '未知';
        }
    };

    // 获取内容类型显示文本
    const getTypeText = (type) => {
        switch (type) {
            case 'post': return '帖子';
            case 'comment': return '评论';
            default: return '未知';
        }
    };

    // 格式化时间
    const formatDate = (dateString) => {
        if (!dateString) return '未知';
        return new Date(dateString).toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // 截断文本
    const truncateText = (text, maxLength = 100) => {
        if (!text) return '';
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    };

    // 查看详情
    const viewContentDetail = async (content) => {
        try {
            const detailData = await getContentDetail(content.id, content.type);
            setSelectedContent(detailData);
            setShowDetailModal(true);
        } catch (err) {
            console.error('获取内容详情失败:', err);
            setError('获取内容详情失败');
        }
    };

    return (
        <div className="admin-review-container">
            <div className="admin-card">
                <div className="admin-card-title">
                    <FileTextOutlined />
                    内容审核
                </div>
                
                {/* 搜索和过滤 */}
                <div className="admin-review-controls">
                    <div className="admin-search-box">
                        <SearchOutlined className="admin-search-icon" />
                        <input
                            ref={searchInputRef}
                            type="text"
                            placeholder="搜索标题、内容、作者... (按回车键搜索)"
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
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterStatus} 
                            onChange={(e) => setFilterStatus(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部状态</option>
                            <option value="PENDING">待审核</option>
                            <option value="APPROVED">已通过</option>
                            <option value="BANNED">已拒绝</option>
                        </select>
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterType} 
                            onChange={(e) => setFilterType(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部类型</option>
                            <option value="post">帖子审核</option>
                            <option value="comment">评论审核</option>
                        </select>
                    </div>
                    
                    <button 
                        onClick={fetchReviewQueue}
                        disabled={loading}
                        className="admin-btn admin-btn-primary"
                    >
                        <ReloadOutlined />
                        刷新
                    </button>
                </div>

                {/* 审核统计 */}
                <div className="admin-review-stats">
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">总内容数:</span>
                        <span className="admin-stat-value">{Array.isArray(contents) ? contents.length : 0}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">待审核:</span>
                        <span className="admin-stat-value admin-stat-PENDING">
                            {Array.isArray(contents) ? contents.filter(c => c.status === 'PENDING').length : 0}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已通过:</span>
                        <span className="admin-stat-value admin-stat-APPROVED">
                            {Array.isArray(contents) ? contents.filter(c => c.status === 'APPROVED').length : 0}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已拒绝:</span>
                        <span className="admin-stat-value admin-stat-BANNED">
                            {Array.isArray(contents) ? contents.filter(c => c.status === 'BANNED').length : 0}
                        </span>
                    </div>
                </div>

                {/* 错误提示 */}
                {error && (
                    <div className="admin-error-message">
                        {error}
                    </div>
                )}

                {/* 内容列表 */}
                <div className="admin-review-list">
                    {loading ? (
                        <div className="admin-loading">
                            <div className="admin-loading-spinner"></div>
                            <span>加载中...</span>
                        </div>
                    ) : filteredContents.length === 0 ? (
                        <div className="admin-empty-state">
                            <FileTextOutlined />
                            <p>没有找到匹配的内容</p>
                        </div>
                    ) : (
                        filteredContents.map(content => (
                            <div key={content.id} className="admin-content-item">
                                <div className="admin-content-header">
                                    <div className="admin-content-type">
                                        {content.type === 'post' ? <FileTextOutlined /> : <MessageOutlined />}
                                        <span>{getTypeText(content.type)}</span>
                                    </div>
                                    <div className="admin-content-time">
                                        {formatDate(content.createdAt)}
                                    </div>
                                </div>
                                
                                <div className="admin-content-body">
                                    <div className="admin-content-info">
                                        <div className="admin-content-title">
                                            {content.title && (
                                                <h4>{truncateText(content.title, 50)}</h4>
                                            )}
                                        </div>
                                        <div className="admin-content-text">
                                            {truncateText(content.content, 120)}
                                        </div>
                                        <div className="admin-content-meta">
                                            <span className="admin-content-author">
                                                作者: {content.author}
                                            </span>
                                            {content.tags && (
                                                <span className="admin-content-tags">
                                                    标签: {content.tags.join(', ')}
                                                </span>
                                            )}
                                            {content.postId && (
                                                <span className="admin-content-post">
                                                    帖子ID: {content.postId}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                    
                                    <div className="admin-content-status">
                                        <span className={`admin-status-badge ${content.status}`}>
                                            {getStatusText(content.status)}
                                        </span>
                                    </div>
                                </div>
                                
                                <div className="admin-content-actions">
                                    <button
                                        onClick={() => viewContentDetail(content)}
                                        className="admin-btn admin-btn-secondary"
                                    >
                                        <EyeOutlined />
                                        查看详情
                                    </button>
                                    
                                    {content.status === 'PENDING' && (
                                        <>
                                            <button
                                                onClick={() => handleReviewAction(content.id, 'APPROVED', content.type)}
                                                disabled={operatingContentId === content.id}
                                                className="admin-btn admin-btn-success"
                                            >
                                                {operatingContentId === content.id ? (
                                                    <div className="admin-btn-loading"></div>
                                                ) : (
                                                    <>
                                                        <CheckCircleOutlined />
                                                        通过
                                                    </>
                                                )}
                                            </button>
                                            <button
                                                onClick={() => handleReviewAction(content.id, 'BANNED', content.type)}
                                                disabled={operatingContentId === content.id}
                                                className="admin-btn admin-btn-danger"
                                            >
                                                {operatingContentId === content.id ? (
                                                    <div className="admin-btn-loading"></div>
                                                ) : (
                                                    <>
                                                        <CloseCircleOutlined />
                                                        拒绝
                                                    </>
                                                )}
                                            </button>
                                        </>
                                    )}
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            {/* 详情模态框 */}
            {showDetailModal && selectedContent && (
                <div className="admin-modal-overlay" onClick={() => setShowDetailModal(false)}>
                    <div className="admin-modal-content" onClick={e => e.stopPropagation()}>
                        <div className="admin-modal-header">
                            <h3>内容详情</h3>
                            <button 
                                onClick={() => setShowDetailModal(false)}
                                className="admin-modal-close"
                            >
                                ×
                            </button>
                        </div>
                        <div className="admin-modal-body">
                            <div className="admin-detail-item">
                                <strong>内容类型:</strong> {getTypeText(selectedContent.type)}
                            </div>
                            {selectedContent.title && (
                                <div className="admin-detail-item">
                                    <strong>标题:</strong> {selectedContent.title}
                                </div>
                            )}
                            <div className="admin-detail-item">
                                <strong>内容:</strong> 
                                <div className="admin-content-full">
                                    {selectedContent.content || selectedContent.comment.content}
                                </div>
                            </div>
                            <div className="admin-detail-item">
                                <strong>作者ID:</strong> {selectedContent.author || selectedContent.comment.userId}
                            </div>
                            <div className="admin-detail-item">
                                <strong>创建时间:</strong> {formatDate(selectedContent.createdAt || selectedContent.comment.createdAt)}
                            </div>
                            <div className="admin-detail-item">
                                <strong>审核状态:</strong> {getStatusText(selectedContent.status || selectedContent.comment.reviewStatus)}
                            </div>
                            {selectedContent.tags && (
                                <div className="admin-detail-item">
                                    <strong>标签:</strong> {selectedContent.tags.join(', ')}
                                </div>
                            )}
                
                            {selectedContent.post && (
                                <>
                                    <div className="admin-detail-item">
                                    <strong>所属帖子ID:</strong> {selectedContent.post.id}
                                    </div>
                                    <div className="admin-detail-item">
                                        <strong>所属帖子标题:</strong> {selectedContent.post.title}
                                    </div>
                                    <div className="admin-detail-item">
                                        <strong>所属帖子内容:</strong> 
                                        <div className="admin-content-full">
                                            {selectedContent.post.content}
                                        </div>
                                    </div>
        
                                </>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminReview;