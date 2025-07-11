import React from 'react';
import { UserOutlined, CalendarOutlined, EyeOutlined, DeleteOutlined } from '@ant-design/icons';
import '../css/AdminCheckins.css';

const AdminCheckinList = ({ currentCheckins, onView, onDelete, getMoodLabelDisplay, getMoodText, formatDateOnly, onViewUserDetail }) => (
  <div className="admin-checkins-list">
    {currentCheckins.length === 0 ? (
      <div className="admin-empty-state">
        <span><UserOutlined /></span>
        <p>暂无打卡数据</p>
      </div>
    ) : (
      currentCheckins.map((checkin) => (
        <div key={checkin.id} className="admin-checkin-item">
          <div className="admin-checkin-header">
            <div className="admin-checkin-user">
              <UserOutlined />
              <span className="admin-checkin-username">用户ID: {checkin.userId}</span>
              <span className="admin-checkin-mood">
                {checkin.moodLabels && checkin.moodLabels.length > 0 ? (
                  <span className="admin-mood-tag">
                    {getMoodLabelDisplay(checkin.moodLabels)}
                  </span>
                ) : (
                  <span className="admin-mood-tag admin-mood-empty">
                    无心情记录
                  </span>
                )}
                <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                  ({checkin.score || 0}/5)
                </span>
              </span>
            </div>
            <div className="admin-checkin-time">
              <CalendarOutlined /> {formatDateOnly(checkin.date || checkin.checkinDate || checkin.createdAt)}
            </div>
          </div>
          <div className="admin-checkin-content">
            <div className="admin-checkin-info">
              <p className="admin-checkin-text">{checkin.diaryDescription || '无日记内容'}</p>
              <div className="admin-checkin-details">
                <div className="admin-checkin-weather-row" style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                  <div className="admin-checkin-weather">
                    <strong>天气:</strong> {checkin.weather || '未知'}
                  </div>
                  <button
                    onClick={() => onViewUserDetail && onViewUserDetail(checkin.userId)}
                    className="admin-btn admin-btn-info"
                    style={{ height: 32, padding: '0 14px', fontSize: 14 }}
                  >
                    <EyeOutlined style={{ fontSize: 13, marginRight: 2 }} />查看用户详情
                  </button>
                </div>
              </div>
            </div>
            <div className="admin-checkin-actions">
              <button
                onClick={() => onView(checkin)}
                className="admin-btn admin-btn-primary"
              >
                <EyeOutlined /> 查看详情
              </button>
              <button
                onClick={() => onDelete(checkin.id)}
                className="admin-btn admin-btn-danger"
              >
                <DeleteOutlined /> 删除
              </button>
            </div>
          </div>
        </div>
      ))
    )}
  </div>
);

export default AdminCheckinList; 