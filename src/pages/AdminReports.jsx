import React, { useState, useEffect } from 'react';
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
import { getReportsList, handleReport } from '../service/adminService';
import '../css/AdminReports.css';

const AdminReports = () => {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterStatus, setFilterStatus] = useState('all'); // all, pending, resolved, rejected
    const [filterType, setFilterType] = useState('all'); // all, post, comment
    const [operatingReportId, setOperatingReportId] = useState(null);
    const [selectedReport, setSelectedReport] = useState(null);
    const [showDetailModal, setShowDetailModal] = useState(false);

    // 获取举报列表
    const fetchReports = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getReportsList();
            if (response.success) {
                setReports(response.data || []);
            } else {
                setError(response.message || '获取举报列表失败');
            }
        } catch (err) {
            console.error('获取举报列表失败:', err);
            setError('获取举报列表失败');
        } finally {
            setLoading(false);
        }
    };

    // 页面加载时获取举报列表
    useEffect(() => {
        fetchReports();
    }, []);

    // 处理举报
    const handleReportAction = async (reportId, action, reason = '') => {
        try {
            setOperatingReportId(reportId);
            const response = await handleReport(reportId, action, reason);
            if (response.success) {
                // 更新本地状态
                setReports(reports.map(report => 
                    report.id === reportId 
                        ? { ...report, status: action, handledAt: new Date().toISOString() }
                        : report
                ));
                console.log(`举报 ${reportId} 已${action === 'resolved' ? '通过' : '拒绝'}处理`);
            } else {
                setError(response.message || '处理举报失败');
            }
        } catch (err) {
            console.error('处理举报失败:', err);
            setError('处理举报失败');
        } finally {
            setOperatingReportId(null);
        }
    };

    // 过滤举报列表
    const filteredReports = reports.filter(report => {
        const matchesSearch = report.reason?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             report.reportedUser?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             report.reporter?.toLowerCase().includes(searchTerm.toLowerCase());
        
        const matchesStatus = filterStatus === 'all' || report.status === filterStatus;
        const matchesType = filterType === 'all' || report.type === filterType;
        
        return matchesSearch && matchesStatus && matchesType;
    });

    // 获取状态显示文本
    const getStatusText = (status) => {
        switch (status) {
            case 'pending': return '待处理';
            case 'resolved': return '已处理';
            case 'rejected': return '已拒绝';
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
    const viewReportDetail = (report) => {
        setSelectedReport(report);
        setShowDetailModal(true);
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
                            type="text"
                            placeholder="搜索举报原因、用户名..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="admin-search-input"
                        />
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterStatus} 
                            onChange={(e) => setFilterStatus(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部状态</option>
                            <option value="pending">待处理</option>
                            <option value="resolved">已处理</option>
                            <option value="rejected">已拒绝</option>
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
                        <span className="admin-stat-value">{reports.length}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">待处理:</span>
                        <span className="admin-stat-value admin-stat-pending">
                            {reports.filter(r => r.status === 'pending').length}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已处理:</span>
                        <span className="admin-stat-value admin-stat-resolved">
                            {reports.filter(r => r.status === 'resolved').length}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">已拒绝:</span>
                        <span className="admin-stat-value admin-stat-rejected">
                            {reports.filter(r => r.status === 'rejected').length}
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
                                        {report.type === 'post' ? <FileTextOutlined /> : <MessageOutlined />}
                                        <span>{getTypeText(report.type)}</span>
                                    </div>
                                    <div className="admin-report-time">
                                        {formatDate(report.createdAt)}
                                    </div>
                                </div>
                                
                                <div className="admin-report-content">
                                    <div className="admin-report-info">
                                        <div className="admin-report-reason">
                                            <strong>举报原因:</strong> {report.reason}
                                        </div>
                                        <div className="admin-report-users">
                                            <span className="admin-report-reporter">
                                                举报人: {report.reporter}
                                            </span>
                                            <span className="admin-report-reported">
                                                被举报人: {report.reportedUser}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div className="admin-report-status">
                                        <span className={`admin-status-badge ${report.status}`}>
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
                                    
                                    {report.status === 'pending' && (
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
                                <strong>举报类型:</strong> {getTypeText(selectedReport.type)}
                            </div>
                            <div className="admin-detail-item">
                                <strong>举报原因:</strong> {selectedReport.reason}
                            </div>
                            <div className="admin-detail-item">
                                <strong>详细描述:</strong> {selectedReport.description}
                            </div>
                            <div className="admin-detail-item">
                                <strong>举报人:</strong> {selectedReport.reporter}
                            </div>
                            <div className="admin-detail-item">
                                <strong>被举报人:</strong> {selectedReport.reportedUser}
                            </div>
                            <div className="admin-detail-item">
                                <strong>举报时间:</strong> {formatDate(selectedReport.createdAt)}
                            </div>
                            <div className="admin-detail-item">
                                <strong>处理状态:</strong> {getStatusText(selectedReport.status)}
                            </div>
                            {selectedReport.handledAt && (
                                <div className="admin-detail-item">
                                    <strong>处理时间:</strong> {formatDate(selectedReport.handledAt)}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminReports; 