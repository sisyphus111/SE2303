import React from 'react';
import { WarningOutlined } from '@ant-design/icons';

const AdminCrisisList = ({ alerts, loading, error }) => (
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
        <div key={alert.id} className="admin-crisis-item">
          <div><b>用户ID:</b> {alert.userId}</div>
          <div><b>心理咨询师ID:</b> {alert.assignedCounselorId || '-'}</div>
          <div><b>类型:</b> {alert.alertTypeDescription}</div>
          <div><b>等级:</b> {alert.alertLevelDescription}</div>
          <div><b>状态:</b> {alert.statusDescription}</div>
          <div><b>描述:</b> {alert.description}</div>
          <div><b>操作详情:</b> {alert.operationDetails}</div>
          <div><b>创建时间:</b> {alert.createdAt ? new Date(alert.createdAt).toLocaleString() : '-'}</div>
          <div><b>跟进备注:</b> {alert.followUpNotes}</div>
          <div><b>跟进日期:</b> {alert.followUpDate ? new Date(alert.followUpDate).toLocaleString() : '-'}</div>
        </div>
      ))
    )}
    {error && (
      <div className="admin-error-message">{error}</div>
    )}
  </div>
);

export default AdminCrisisList; 