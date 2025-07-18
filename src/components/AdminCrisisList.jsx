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
  ExclamationCircleOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';

const AdminCrisisList = ({ alerts, loading, error, onUpdateStatus, onAddFollowUpNotes, operatingAlertId, onViewUserDetail }) => {
  const [expandedAlertId, setExpandedAlertId] = useState(null);

  const toggleAlertExpansion = (alertId) => {
    setExpandedAlertId(expandedAlertId === alertId ? null : alertId);
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'PENDING': { class: 'warning', text: '待处理', icon: <ClockCircleOutlined /> },
      'ASSIGNED': { class: 'info', text: '已分配', icon: <InfoCircleOutlined /> },
      'IN_PROGRESS': { class: 'info', text: '处理中', icon: <ClockCircleOutlined /> },
      'COMPLETED': { class: 'success', text: '已完成', icon: <CheckCircleOutlined /> },
      'CLOSED': { class: 'default', text: '已关闭', icon: <CloseCircleOutlined /> },
      'CANCELLED': { class: 'default', text: '已取消', icon: <CloseCircleOutlined /> }
    };
    const statusInfo = statusMap[status] || { class: 'default', text: status, icon: <InfoCircleOutlined /> };
    return (
      <span className={`status-badge ${statusInfo.class}`}>
        {statusInfo.icon}
        {statusInfo.text}
      </span>
    );
  };

  const getLevelBadge = (level) => {
    const levelMap = {
      'HIGH': { class: 'danger', text: '高危', icon: <ExclamationCircleOutlined /> },
      'MEDIUM': { class: 'warning', text: '中危', icon: <WarningOutlined /> },
      'LOW': { class: 'success', text: '低危', icon: <CheckCircleOutlined /> },
      'URGENT': { class: 'danger', text: '紧急', icon: <ExclamationCircleOutlined /> }
    };
    const levelInfo = levelMap[level] || { class: 'default', text: level, icon: <InfoCircleOutlined /> };
    return (
      <span className={`status-badge ${levelInfo.class}`}>
        {levelInfo.icon}
        {levelInfo.text}
      </span>
    );
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) {
      return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    } else if (diffDays === 0) {
      return '今天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    } else if (diffDays <= 7) {
      return `${diffDays}天前`;
    } else {
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  };

  const truncateText = (text, maxLength = 100) => {
    if (!text) return '-';
    return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
  };

  // 根据状态获取可用的操作按钮
  const getAvailableActions = (alert) => {
    const { status } = alert;
    const actions = [];

    // 查看用户详情按钮 - 所有状态都显示
    actions.push(
      <button
        key="view-user"
        onClick={() => onViewUserDetail && onViewUserDetail(alert.userId)}
        className="admin-btn admin-btn-info admin-btn-sm"
        title="查看用户详细信息"
      >
        <EyeOutlined /> 查看用户详情
      </button>
    );

    // 根据状态显示不同的操作按钮
    switch (status) {
      case 'PENDING':
        // 待处理状态：可以开始处理、关闭警报、添加备注
        actions.push(
          <button
            key="start-process"
            className="admin-btn admin-btn-info"
            onClick={() => onUpdateStatus(alert.id, 'IN_PROGRESS')}
            disabled={operatingAlertId === alert.id}
            title="开始处理此警报"
          >
            <EyeOutlined />
            开始处理
          </button>,
          <button
            key="close-alert"
            className="admin-btn admin-btn-warning"
            onClick={() => onUpdateStatus(alert.id, 'CLOSED')}
            disabled={operatingAlertId === alert.id}
            title="关闭此警报"
          >
            <CloseCircleOutlined />
            关闭警报
          </button>,
          <button
            key="add-notes"
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              const notes = prompt('请输入跟进备注:');
              if (notes !== null && notes.trim()) {
                onAddFollowUpNotes(alert.id, notes);
              }
            }}
            disabled={operatingAlertId === alert.id}
            title="添加跟进备注"
          >
            <MessageOutlined />
            添加备注
          </button>
        );
        break;

      case 'ASSIGNED':
        // 已分配状态：可以开始处理、关闭警报、添加备注
        actions.push(
          <button
            key="start-process"
            className="admin-btn admin-btn-info"
            onClick={() => onUpdateStatus(alert.id, 'IN_PROGRESS')}
            disabled={operatingAlertId === alert.id}
            title="开始处理此警报"
          >
            <EyeOutlined />
            开始处理
          </button>,
          <button
            key="close-alert"
            className="admin-btn admin-btn-warning"
            onClick={() => onUpdateStatus(alert.id, 'CLOSED')}
            disabled={operatingAlertId === alert.id}
            title="关闭此警报"
          >
            <CloseCircleOutlined />
            关闭警报
          </button>,
          <button
            key="add-notes"
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              const notes = prompt('请输入跟进备注:');
              if (notes !== null && notes.trim()) {
                onAddFollowUpNotes(alert.id, notes);
              }
            }}
            disabled={operatingAlertId === alert.id}
            title="添加跟进备注"
          >
            <MessageOutlined />
            添加备注
          </button>
        );
        break;

      case 'IN_PROGRESS':
        // 处理中状态：可以标记完成、关闭警报、添加备注
        actions.push(
          <button
            key="mark-completed"
            className="admin-btn admin-btn-success"
            onClick={() => onUpdateStatus(alert.id, 'COMPLETED')}
            disabled={operatingAlertId === alert.id}
            title="标记为已完成"
          >
            <CheckCircleOutlined />
            标记完成
          </button>,
          <button
            key="close-alert"
            className="admin-btn admin-btn-warning"
            onClick={() => onUpdateStatus(alert.id, 'CLOSED')}
            disabled={operatingAlertId === alert.id}
            title="关闭此警报"
          >
            <CloseCircleOutlined />
            关闭警报
          </button>,
          <button
            key="add-notes"
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              const notes = prompt('请输入跟进备注:');
              if (notes !== null && notes.trim()) {
                onAddFollowUpNotes(alert.id, notes);
              }
            }}
            disabled={operatingAlertId === alert.id}
            title="添加跟进备注"
          >
            <MessageOutlined />
            添加备注
          </button>
        );
        break;

      case 'COMPLETED':
        // 已完成状态：只显示查看用户详情，不显示其他操作按钮
        // 可以添加一个"重新打开"的选项，如果需要的话
        actions.push(
          <span key="completed-status" className="admin-status-completed">
            <CheckCircleOutlined style={{ color: '#52c41a', marginRight: '4px' }} />
            处理完成
          </span>
        );
        break;

      case 'CLOSED':
        // 已关闭状态：只显示查看用户详情，不显示其他操作按钮
        actions.push(
          <span key="closed-status" className="admin-status-closed">
            <CloseCircleOutlined style={{ color: '#999', marginRight: '4px' }} />
            已关闭
          </span>
        );
        break;

      case 'CANCELLED':
        // 已取消状态：只显示查看用户详情，不显示其他操作按钮
        actions.push(
          <span key="cancelled-status" className="admin-status-cancelled">
            <CloseCircleOutlined style={{ color: '#999', marginRight: '4px' }} />
            已取消
          </span>
        );
        break;

      default:
        // 默认状态：显示所有操作按钮
        actions.push(
          <button
            key="start-process"
            className="admin-btn admin-btn-info"
            onClick={() => onUpdateStatus(alert.id, 'IN_PROGRESS')}
            disabled={operatingAlertId === alert.id}
            title="开始处理此警报"
          >
            <EyeOutlined />
            开始处理
          </button>,
          <button
            key="mark-completed"
            className="admin-btn admin-btn-success"
            onClick={() => onUpdateStatus(alert.id, 'COMPLETED')}
            disabled={operatingAlertId === alert.id}
            title="标记为已完成"
          >
            <CheckCircleOutlined />
            标记完成
          </button>,
          <button
            key="close-alert"
            className="admin-btn admin-btn-warning"
            onClick={() => onUpdateStatus(alert.id, 'CLOSED')}
            disabled={operatingAlertId === alert.id}
            title="关闭此警报"
          >
            <CloseCircleOutlined />
            关闭警报
          </button>,
          <button
            key="add-notes"
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              const notes = prompt('请输入跟进备注:');
              if (notes !== null && notes.trim()) {
                onAddFollowUpNotes(alert.id, notes);
              }
            }}
            disabled={operatingAlertId === alert.id}
            title="添加跟进备注"
          >
            <MessageOutlined />
            添加备注
          </button>
        );
    }

    return actions;
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
                  {getStatusBadge(alert.status)}
                </div>
                <div className="admin-crisis-level">
                  {getLevelBadge(alert.alertLevel)}
                </div>
              </div>
            </div>

            <div className="admin-crisis-content">
              <div className="admin-crisis-info">
                <div className="admin-crisis-details">
                  <div className="admin-crisis-author">
                    <UserOutlined />
                    用户ID: {alert.userId}
                  </div>
                  
                  {alert.assignedCounselorId && (
                    <div className="admin-crisis-counselor">
                      <PhoneOutlined />
                      咨询师: {alert.assignedCounselorId}
                    </div>
                  )}
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
                      <div className="admin-crisis-detail-item">
                        <strong>详细描述:</strong> {alert.description}
                      </div>
                      <div className="admin-crisis-detail-item">
                        <strong>操作详情:</strong> {alert.operationDetails || '-'}
                      </div>
                      <div className="admin-crisis-detail-item">
                        <strong>跟进备注:</strong> {alert.followUpNotes || '-'}
                      </div>
                      <div className="admin-crisis-detail-item">
                        <strong>跟进日期:</strong> {formatDate(alert.followUpDate)}
                      </div>
                    </div>
                  )}
                </div>
              </div>

              <div className="admin-crisis-actions">
                {getAvailableActions(alert)}
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