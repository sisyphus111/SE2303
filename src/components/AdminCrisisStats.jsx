import React from 'react';

const AdminCrisisStats = ({ pagination, alerts }) => (
  <div className="admin-crisis-stats">
    <div className="admin-stat-item">
      <span className="admin-stat-label">总计:</span>
      <span className="admin-stat-value">{pagination.total}</span>
    </div>
    <div className="admin-stat-item">
      <span className="admin-stat-label">高风险:</span>
      <span className="admin-stat-value admin-stat-high">
        {alerts.filter(a => a.alertLevel === 'HIGH').length}
      </span>
    </div>
    <div className="admin-stat-item">
      <span className="admin-stat-label">待处理:</span>
      <span className="admin-stat-value admin-stat-pending">
        {alerts.filter(a => a.status === 'PENDING').length}
      </span>
    </div>
  </div>
);

export default AdminCrisisStats; 