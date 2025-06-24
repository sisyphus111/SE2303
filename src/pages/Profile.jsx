import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Profile.css';

const Profile = () => {
  const [user, setUser] = useState({
    username: 'admin',
    avatar: '👤',
    joinDate: '2024-01-15',
    moodScore: 85,
    chatCount: 23,
    diaryCount: 12
  });
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // 检查登录状态
    const loginStatus = localStorage.getItem('isLoggedIn');
    const username = localStorage.getItem('username');
    
    if (loginStatus === 'true' && username) {
      setIsLoggedIn(true);
      setUser(prev => ({ ...prev, username }));
    } else {
      navigate('/login');
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('username');
    setIsLoggedIn(false);
    navigate('/');
  };

  const getMoodEmoji = (score) => {
    if (score >= 80) return '😊';
    if (score >= 60) return '🙂';
    if (score >= 40) return '😐';
    return '😔';
  };

  const getMoodText = (score) => {
    if (score >= 80) return '心情很好';
    if (score >= 60) return '心情不错';
    if (score >= 40) return '心情一般';
    return '心情低落';
  };

  if (!isLoggedIn) {
    return null;
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-avatar">
          <span>{user.avatar}</span>
        </div>
        <div className="profile-info">
          <h1>{user.username}</h1>
          <p>加入时间：{user.joinDate}</p>
          <div className="mood-indicator">
            <span className="mood-emoji">{getMoodEmoji(user.moodScore)}</span>
            <span className="mood-text">{getMoodText(user.moodScore)}</span>
            <span className="mood-score">({user.moodScore}分)</span>
          </div>
        </div>
        <button className="logout-button" onClick={handleLogout}>
          退出登录
        </button>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">💬</div>
          <div className="stat-content">
            <h3>{user.chatCount}</h3>
            <p>AI对话次数</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">📝</div>
          <div className="stat-content">
            <h3>{user.diaryCount}</h3>
            <p>日记记录</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <h3>{user.moodScore}</h3>
            <p>心情评分</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-content">
            <h3>7</h3>
            <p>连续打卡</p>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <h2>快速操作</h2>
        <div className="action-grid">
          <button 
            className="action-card"
            onClick={() => navigate('/ai-chat')}
          >
            <div className="action-icon">🤖</div>
            <h3>AI陪伴对话</h3>
            <p>与AI助手聊天，获得情感支持</p>
          </button>
          
          <button 
            className="action-card"
            onClick={() => navigate('/diary')}
          >
            <div className="action-icon">📝</div>
            <h3>写日记</h3>
            <p>记录今天的心情和想法</p>
          </button>
          
          <button 
            className="action-card"
            onClick={() => navigate('/mood-tracker')}
          >
            <div className="action-icon">📈</div>
            <h3>心情追踪</h3>
            <p>查看心情变化趋势</p>
          </button>
          
          <button 
            className="action-card"
            onClick={() => navigate('/resources')}
          >
            <div className="action-icon">📚</div>
            <h3>心理资源</h3>
            <p>获取心理健康知识</p>
          </button>
        </div>
      </div>

      <div className="recent-activity">
        <h2>最近活动</h2>
        <div className="activity-list">
          <div className="activity-item">
            <div className="activity-icon">💬</div>
            <div className="activity-content">
              <h4>与AI助手对话</h4>
              <p>讨论了今天的工作压力</p>
              <span className="activity-time">2小时前</span>
            </div>
          </div>
          
          <div className="activity-item">
            <div className="activity-icon">📝</div>
            <div className="activity-content">
              <h4>写下了今天的日记</h4>
              <p>记录了与朋友聚会的快乐时光</p>
              <span className="activity-time">昨天</span>
            </div>
          </div>
          
          <div className="activity-item">
            <div className="activity-icon">📊</div>
            <div className="activity-content">
              <h4>更新了心情评分</h4>
              <p>今天的心情评分为85分</p>
              <span className="activity-time">2天前</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile; 