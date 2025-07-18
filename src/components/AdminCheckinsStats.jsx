import React from 'react';
import '../css/AdminCheckins.css';

const AdminCheckinsStats = ({ stats }) => (
  <div className="admin-checkins-stats">
    <div className="admin-stat-item">
      <div className="admin-stat-label">总打卡数</div>
      <div className="admin-stat-value">{stats.total}</div>
    </div>
    <div className="admin-stat-item">
      <div className="admin-stat-label">今日打卡</div>
      <div className="admin-stat-value admin-stat-today">{stats.today}</div>
    </div>
    <div className="admin-stat-item">
      <div className="admin-stat-label">积极情绪</div>
      <div className="admin-stat-value admin-stat-happy">{stats.happy}</div>
    </div>
    <div className="admin-stat-item">
      <div className="admin-stat-label">消极情绪</div>
      <div className="admin-stat-value admin-stat-sad">{stats.sad}</div>
    </div>
    <div className="admin-stat-item">
      <div className="admin-stat-label">平均心情</div>
      <div className="admin-stat-value admin-stat-average">{stats.averageMood}</div>
    </div>
  </div>
);

export default AdminCheckinsStats; 