import React, { useState, useEffect, useRef } from 'react';
import { 
  ExclamationCircleOutlined, 
  CheckCircleOutlined, 
  CloseCircleOutlined,
  EyeOutlined,
  SearchOutlined,
  ReloadOutlined,
  FileTextOutlined,
  MessageOutlined
} from '@ant-design/icons';
import { getReportsList, handleReport, getReportDetail } from '../service/community_common';
import { getUserAdminInfo } from '../service/userService';
import { Modal, Spin } from 'antd';
import '../css/AdminReports.css';

const AdminReports = () => {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
    const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
    const [filterStatus, setFilterStatus] = useState('all'); // all, pending, approved, rejected
    const [filterType, setFilterType] = useState('all'); // all, post, comment
    const [operatingReportId, setOperatingReportId] = useState(null);
    const [selectedReport, setSelectedReport] = useState(null);
    const [showDetailModal, setShowDetailModal] = useState(false);
    const [userDetailModal, setUserDetailModal] = useState({ visible: false, loading: false, user: null });
    const searchInputRef = useRef(null);
    const clearButtonRef = useRef(null);

    // 举报原因映射
    const reportReasons = {
        'spam': '垃圾信息/广告',
        'inappropriate': '不当内容',
        'harassment': '骚扰/攻击',
        'copyright': '版权侵犯',
        'fake': '虚假信息',
        'other': '其他原因'
    };

    // 获取举报原因的中文显示
    const getReasonLabel = (reason) => {
        return reportReasons[reason] || reason || '未知原因';
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

    // 获取举报列表
    const fetchReports = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getReportsList();
            // 确保 reports 是数组
            const reportsArray = Array.isArray(data) ? data : (data?.content || []);
            setReports(reportsArray);
        } catch (err) {
            console.error('获取举报列表失败:', err);
            setError('获取举报列表失败');
            // 确保错误时也设置为空数组
            setReports([]);
        } finally {
            setLoading(false);
        }
    };

    // 页面加载时获取举报列表
    useEffect(() => {
        fetchReports();
    }, []);

    // 通过/拒绝举报
    const handleReportAction = async (reportId, action, reason = '') => {
        try {
            setOperatingReportId(reportId);
            await handleReport(reportId, action, reason);
            
            // 更新本地状态
            setReports(prevReports => {
                if (!Array.isArray(prevReports)) return [];
                return prevReports.map(report => 
                    report.id === reportId 
                        ? { ...report, status: action, handledAt: new Date().toISOString() }
                        : report
                );
            });
            console.log(`举报 ${reportId} 已${action === 'resolved' ? '通过' : '拒绝'}处理`);
        } catch (err) {
            console.error('处理举报失败:', err);
            setError('处理举报失败');
        } finally {
            setOperatingReportId(null);
        }
    };

    // 过滤举报列表
    const filteredReports = Array.isArray(reports) ? reports.filter(report => {
        const reasonLabel = getReasonLabel(report.reason);
        const matchesSearch = report.reason?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             reasonLabel?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             report.userId?.toLowerCase().includes(activeSearchTerm.toLowerCase());
        
        const matchesStatus = filterStatus === 'all' || report.status === filterStatus;
        const matchesType = filterType === 'all' || report.type === filterType;
        
        return matchesSearch && matchesStatus && matchesType;
    }) : [];

    // 获取状态显示文本
    const getStatusText = (status) => {
        switch (status) {
            case 'PENDING': return '待处理';
            case 'APPROVED': return '已处理';
            case 'REJECTED': return '已拒绝';
            default: return '未知';
        }
    };

    // 获取举报类型显示文本
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

    // 查看详情
    const viewReportDetail = async (report) => {
        try {
            const detailData = await getReportDetail(report.id);
            setSelectedReport(detailData);
            setShowDetailModal(true);
        } catch (err) {
            console.error('获取举报详情失败:', err);
            setError('获取举报详情失败');
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

    return (
        <div className="admin-reports-container">
            <div className="admin-card">
                <div className="admin-card-title">
                    <ExclamationCircleOutlined />
                    举报处理
                </div>
                
                {/* 搜索和过滤 */}
                <div className="admin-reports-controls">
                    <div className="admin-search-box">
                        <SearchOutlined className="admin-search-icon" />
                        <input
                            ref={searchInputRef}
                            type="text"
                            
                            placeholder="搜索举报原因、用户名..."

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
                            <option value="PENDING">待处理</option>
                            <option value="APPROVED">已处理</option>
                            <option value="REJECTED">已拒绝</option>
                        </select>
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterType} 
                            onChange={(e) => setFilterType(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部类型</option>
                            <option value="post">帖子举报</option>
                            <option value="comment">评论举报</option>
                        </select>
                    </div>
                    
                    <button 
                        onClick={fetchReports}
                        disabled={loading}
                        className="admin-btn admin-btn-primary"
                    >
                        <ReloadOutlined />
                        刷新
                    </button>
                </div>

                {/* 举报统计 */}
                <div className="admin-reports-stats">
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">总举报数:</span>
                        <span className="admin-stat-value">{Array.isArray(reports) ? reports.length : 0}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">待处理:</span>
                        <span className="admin-stat-value admin-stat-pending">
                            {Array.isArray(reports) ? reports.filter(r => r.status === 'PENDING').length : 0}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已处理:</span>
                        <span className="admin-stat-value admin-stat-resolved">
                            {Array.isArray(reports) ? reports.filter(r => r.status === 'APPROVED').length : 0}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已拒绝:</span>
                        <span className="admin-stat-value admin-stat-rejected">
                            {Array.isArray(reports) ? reports.filter(r => r.status === 'REJECTED').length : 0}
                        </span>
                    </div>
                </div>

                {/* 错误提示 */}
                {error && (
                    <div className="admin-error-message">
                        {error}
                    </div>
                )}

                {/* 举报列表 */}
                <div className="admin-reports-list">
                    {loading ? (
                        <div className="admin-loading">
                            <div className="admin-loading-spinner"></div>
                            <span>加载中...</span>
                        </div>
                    ) : filteredReports.length === 0 ? (
                        <div className="admin-empty-state">
                            <ExclamationCircleOutlined />
                            <p>没有找到匹配的举报</p>
                        </div>
                    ) : (
                        filteredReports.map(report => (
                            <div key={report.id} className="admin-report-item">
                                <div className="admin-report-header">
                                    <div className="admin-report-type">
                                        <FileTextOutlined />
                                        <span>帖子举报</span>
                                    </div>
                                    <div className="admin-report-time">
                                        {formatDate(report.createdAt)}
                                    </div>
                                </div>
                                
                                <div className="admin-report-content">
                                    <div className="admin-report-info">
                                        <div className="admin-report-reason">
                                            <strong>举报原因:</strong> {getReasonLabel(report.reason)}
                                        </div>
                                    
                                        <div className="admin-report-users">
                                            <span className="admin-report-reporter">
                                                举报人Id: {report.userId}
                                                <span
                                                    style={{ marginLeft: 8, color: '#1677ff', cursor: 'pointer', fontSize: 12, display: 'inline-flex', alignItems: 'center' }}
                                                    onClick={() => handleViewUserDetail(report.userId)}
                                                >
                                                    <EyeOutlined style={{ fontSize: 13, marginRight: 2 }} />查看用户详情
                                                </span>
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div className="admin-report-status">
                                        <span className={`admin-status-badge ${report.status.toLowerCase()}`}>
                                            {getStatusText(report.status)}
                                        </span>
                                    </div>
                                </div>
                                
                                <div className="admin-report-actions">
                                    <button
                                        onClick={() => viewReportDetail(report)}
                                        className="admin-btn admin-btn-secondary"
                                    >
                                        <EyeOutlined />
                                        查看详情
                                    </button>
                                    
                                    {report.status === 'PENDING' && (
                                        <>
                                            <button
                                                onClick={() => handleReportAction(report.id, 'resolved')}
                                                disabled={operatingReportId === report.id}
                                                className="admin-btn admin-btn-success"
                                            >
                                                {operatingReportId === report.id ? (
                                                    <div className="admin-btn-loading"></div>
                                                ) : (
                                                    <>
                                                        <CheckCircleOutlined />
                                                        通过
                                                    </>
                                                )}
                                            </button>
                                            <button
                                                onClick={() => handleReportAction(report.id, 'rejected')}
                                                disabled={operatingReportId === report.id}
                                                className="admin-btn admin-btn-danger"
                                            >
                                                {operatingReportId === report.id ? (
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
            {showDetailModal && selectedReport && (
                <div className="admin-modal-overlay" onClick={() => setShowDetailModal(false)}>
                    <div className="admin-modal-content" onClick={e => e.stopPropagation()}>
                        <div className="admin-modal-header">
                            <h3>举报详情</h3>
                            <button 
                                onClick={() => setShowDetailModal(false)}
                                className="admin-modal-close"
                            >
                                ×
                            </button>
                        </div>
                        <div className="admin-modal-body">
                            <div className="admin-detail-item">
                                <strong>举报类型:</strong> {selectedReport.report?.type === 'post' ? '帖子举报' : '评论举报'}
                            </div>
                            <div className="admin-detail-item">
                                <strong>举报原因:</strong> {getReasonLabel(selectedReport.report?.reason)}
                            </div>
                            {selectedReport.report?.detail && (
                                <div className="admin-detail-item">
                                    <strong>详细描述:</strong> {selectedReport.report.detail}
                                </div>
                            )}
                            <div className="admin-detail-item">
                                <strong>举报人Id:</strong> {selectedReport.report?.userId}
                            </div>
                            <div className="admin-detail-item">
                                <strong>举报时间:</strong> {formatDate(selectedReport.report?.createdAt)}
                            </div>
                            <div className="admin-detail-item">
                                <strong>处理状态:</strong> {getStatusText(selectedReport.report?.status)}
                            </div>
                            {selectedReport.report?.handledAt && (
                                <div className="admin-detail-item">
                                    <strong>处理时间:</strong> {formatDate(selectedReport.report.handledAt)}
                                </div>
                            )}
                            {selectedReport.report?.handledBy && (
                                <div className="admin-detail-item">
                                    <strong>处理人:</strong> {selectedReport.report.handledBy}
                                </div>
                            )}
                            {selectedReport.report?.handleReason && (
                                <div className="admin-detail-item">
                                    <strong>处理原因:</strong> {selectedReport.report.handleReason}
                                </div>
                            )}
                            {selectedReport.post && (
                                <>
                                    <div className="admin-detail-item">
                                        <strong>被举报帖子标题:</strong> {selectedReport.post.title}
                                    </div>
                                    <div className="admin-detail-item">
                                        <strong>被举报帖子内容:</strong> 
                                        <div className="admin-content-full">
                                            {selectedReport.post.content}
                                        </div>
                                    </div>
                                    <div className="admin-detail-item">
                                        <strong>帖子作者:</strong> {selectedReport.post.userId}
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            )}

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

export default AdminReports;