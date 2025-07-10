import React from 'react';
import '../css/AdminCheckins.css';

const AdminMoodStats = ({ showStats, moodDistribution, stats, getMoodText }) => (
  showStats && (
    <div className="admin-mood-stats">
      <h4>心情分布统计</h4>
      <div className="admin-mood-chart">
        {Object.entries(moodDistribution).map(([mood, count]) => (
          <div key={mood} className="admin-mood-item">
            <div className="admin-mood-label">
              <span>{getMoodText(mood)}</span>
            </div>
            <div className="admin-mood-bar">
              <div
                className="admin-mood-fill"
                style={{
                  width: `${stats.total > 0 ? (count / stats.total) * 100 : 0}%`,
                  backgroundColor: ['开心', '兴奋', '满足', '宁静', '感恩', '自信', '充满活力', '乐观'].includes(mood)
                    ? '#52c41a'
                    : ['平静', '冷静', '专注', '放松'].includes(mood)
                    ? '#faad14'
                    : '#ff4d4f',
                }}
              ></div>
            </div>
            <div className="admin-mood-count">{count}</div>
          </div>
        ))}
      </div>
    </div>
  )
);

export default AdminMoodStats; 