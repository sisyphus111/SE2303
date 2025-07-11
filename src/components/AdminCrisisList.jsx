import React, { useState } from 'react';
import { 
  WarningOutlined, 
  UserOutlined, 
  PhoneOutlined,
  CalendarOutlined,
  MessageOutlined,
  EyeOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';

const AdminCrisisList = ({ alerts, loading, error, onUpdateStatus, onAddFollowUpNotes, operatingAlertId, onViewUserDetail }) => {
  const [expandedAlertId, setExpandedAlertId] = useState(null);

  const toggleAlertExpansion = (alertId) => {
    setExpandedAlertId(expandedAlertId === alertId ? null : alertId);
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'PENDING': { class: 'warning', text: '待处理' },
      'IN_PROGRESS': { class: 'info', text: '处理中' },
      'RESOLVED': { class: 'success', text: '已解决' },
      'CLOSED': { class: 'default', text: '已关闭' }
    };
    const statusInfo = statusMap[status] || { class: 'default', text: status };
    return <span className={`status-badge ${statusInfo.class}`}>{statusInfo.text}</span>;
  };

  const getLevelBadge = (level) => {
    const levelMap = {
      'HIGH': { class: 'danger', text: '高危' },
      'MEDIUM': { class: 'warning', text: '中危' },
      'LOW': { class: 'success', text: '低危' }
    };
    const levelInfo = levelMap[level] || { class: 'default', text: level };
    return <span className={`status-badge ${levelInfo.class}`}>{levelInfo.text}</span>;
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const truncateText = (text, maxLength = 100) => {
    if (!text) return '-';
    return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
  };

  return (
    <div className="admin-crisis-list">
      {loading ? (
        <div className="admin-loading">
          <div className="admin-loading-spinner"></div>
          <span>加载中...</span>
        </div>
      ) : alerts.length === 0 ? (
        <div className="admin-empty-state">
          <WarningOutlined />
          <p>没有找到匹配的警报</p>
        </div>
      ) : (
        alerts.map((alert) => (
          <div key={alert.id} className={`admin-crisis-item ${alert.alertLevel === 'HIGH' ? 'admin-crisis-urgent' : ''}`}>
            <div className="admin-crisis-header">
              <div className="admin-crisis-meta">
                <div className="admin-crisis-type">
                  <WarningOutlined />
                  {alert.alertTypeDescription || '危险警报'}
                </div>
                <div className="admin-crisis-time">
                  <CalendarOutlined />
                  {formatDate(alert.createdAt)}
                </div>
                <div className="admin-crisis-status">
                  {getStatusBadge(alert.statusDescription)}
                </div>
              </div>
            </div>

            <div className="admin-crisis-content">
              <div className="admin-crisis-info">
                <div className="admin-crisis-details">
                  <div className="admin-crisis-author" style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <UserOutlined />
                    用户ID: {alert.userId}
                    <button
                      onClick={() => onViewUserDetail && onViewUserDetail(alert.userId)}
                      className="admin-btn admin-btn-info"
                      style={{ height: 28, padding: '0 12px', fontSize: 13 }}
                    >
                      <EyeOutlined style={{ fontSize: 13, marginRight: 2 }} />查看用户详情
                    </button>
                  </div>
                  <div className="admin-crisis-stats">
                    {/* <span>
                      <PhoneOutlined />
                      咨询师: {alert.assignedCounselorId || '未分配'}
                    </span> */}
                    <span>
                      <ExclamationCircleOutlined />
                      {getLevelBadge(alert.alertLevelDescription)}
                    </span>
                  </div>
                </div>

                <div className="admin-crisis-description">
                  <div className="admin-crisis-text">
                    {truncateText(alert.description)}
                  </div>
                  
                  {(alert.description && alert.description.length > 100) && (
                    <button 
                      className="admin-toggle-btn"
                      onClick={() => toggleAlertExpansion(alert.id)}
                    >
                      {expandedAlertId === alert.id ? '收起详情' : '查看详情'}
                    </button>
                  )}

                  {expandedAlertId === alert.id && (
                    <div className="admin-crisis-full-content">
                      <div><strong>详细描述:</strong> {alert.description}</div>
                      <div><strong>操作详情:</strong> {alert.operationDetails || '-'}</div>
                      <div><strong>跟进备注:</strong> {alert.followUpNotes || '-'}</div>
                      <div><strong>跟进日期:</strong> {formatDate(alert.followUpDate)}</div>
                    </div>
                  )}
                </div>
              </div>

              <div className="admin-crisis-actions">
                <button
                  className="admin-btn admin-btn-info"
                  onClick={() => onUpdateStatus(alert.id, 'IN_PROGRESS')}
                  disabled={operatingAlertId === alert.id}
                >
                  <EyeOutlined />
                  开始处理
                </button>
                <button
                  className="admin-btn admin-btn-success"
                  onClick={() => onUpdateStatus(alert.id, 'RESOLVED')}
                  disabled={operatingAlertId === alert.id}
                >
                  <CheckCircleOutlined />
                  标记解决
                </button>
                <button
                  className="admin-btn admin-btn-warning"
                  onClick={() => onUpdateStatus(alert.id, 'CLOSED')}
                  disabled={operatingAlertId === alert.id}
                >
                  <CloseCircleOutlined />
                  关闭警报
                </button>
                <button
                  className="admin-btn admin-btn-secondary"
                  onClick={() => {
                    const notes = prompt('请输入跟进备注:');
                    if (notes !== null) {
                      onAddFollowUpNotes(alert.id, notes);
                    }
                  }}
                  disabled={operatingAlertId === alert.id}
                >
                  <MessageOutlined />
                  添加备注
                </button>
              </div>
            </div>
          </div>
        ))
      )}
      {error && (
        <div className="admin-error-message">
          {error}
          <button className="admin-error-close">×</button>
        </div>
      )}
    </div>
  );
};

export default AdminCrisisList; 