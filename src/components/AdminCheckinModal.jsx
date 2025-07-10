import React from 'react';
import '../css/AdminCheckins.css';

const AdminCheckinModal = ({ show, checkin, onClose, getMoodText, formatDateOnly }) => {
  if (!show || !checkin) return null;
  return (
    <div className="admin-modal-overlay" onClick={onClose}>
      <div className="admin-modal-content" onClick={e => e.stopPropagation()}>
        <div className="admin-modal-header">
          <h3>打卡详情</h3>
          <button onClick={onClose} className="admin-modal-close">×</button>
        </div>
        <div className="admin-modal-body">
          <div className="admin-detail-item">
            <strong>用户:</strong> 用户ID: {checkin.userId}
          </div>
          <div className="admin-detail-item">
            <strong>心情:</strong>
            <span className="admin-mood-display">
              {checkin.moodLabels && checkin.moodLabels.length > 0 ? (
                checkin.moodLabels.map((mood, index) => (
                  <span key={index} className="admin-mood-tag">{getMoodText(mood)}</span>
                ))
              ) : (
                <span className="admin-mood-tag admin-mood-empty">无心情记录</span>
              )}
              <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                ({checkin.score || 5}/5)
              </span>
            </span>
          </div>
          <div className="admin-detail-item">
            <strong>打卡时间:</strong> {formatDateOnly(checkin.date || checkin.checkinDate || checkin.createdAt)}
          </div>
          <div className="admin-detail-item">
            <div className="admin-checkin-weather">
              <strong>天气:</strong> {checkin.weather || '未知'}
            </div>
          </div>
          <div className="admin-detail-item">
            <strong>内容:</strong>
            <div className="admin-checkin-full-content">
              {checkin.diaryDescription || '无日记内容'}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminCheckinModal; 