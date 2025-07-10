import React, { useState, useEffect } from 'react';
import { UserOutlined } from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import '../css/UserInfoCard.css';

const UserInfoCard = ({ userId, showStats = true, className = '', style = {} }) => {
  const { getUserCommunityInfoById, getUserStatisticsById } = useUser();
  const [userInfo, setUserInfo] = useState(null);
  const [userStats, setUserStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [avatarError, setAvatarError] = useState(false);

  useEffect(() => {
    const fetchUserInfo = async () => {
      if (!userId) return;
      
      try {
        setLoading(true);
        const [communityInfo, statistics] = await Promise.all([
          getUserCommunityInfoById(userId),
          showStats ? getUserStatisticsById(userId) : null
        ]);
        
        setUserInfo(communityInfo);
        setUserStats(statistics);
      } catch (error) {
        console.error('获取用户信息失败:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [userId, showStats, getUserCommunityInfoById, getUserStatisticsById]);

  if (loading) {
    return (
      <div className={`user-info-card loading ${className}`} style={style}>
        <div className="user-info-skeleton">
          <div className="avatar-skeleton"></div>
          <div className="info-skeleton">
            <div className="name-skeleton"></div>
            <div className="stats-skeleton"></div>
          </div>
        </div>
      </div>
    );
  }

  if (!userInfo) {
    return (
      <div className={`user-info-card error ${className}`} style={style}>
        <div className="user-info-error">
          <UserOutlined className="error-icon" />
          <span>用户信息加载失败</span>
        </div>
      </div>
    );
  }

  const { username, nickname, avatar } = userInfo;

  return (
    <div className={`user-info-card ${className}`} style={style}>
      <div className="user-avatar">
        {avatar && !avatarError ? (
          <img
            src={avatar}
            alt={nickname || username}
            onError={() => setAvatarError(true)}
          />
        ) : (
          <div className="avatar-fallback">
            <UserOutlined />
          </div>
        )}
      </div>
      
      <div className="user-details">
        <div className="user-name">
          {nickname && (
            <span className="user-nickname">{nickname}</span>
          )}
          <span className="user-username">@{username}</span>
        </div>
        
        {showStats && userStats && (
          <div className="user-stats">
            <span className="stat-item">
              <span className="stat-label">获赞</span>
              <span className="stat-value">{userStats.likedCount || 0}</span>
            </span>
            <span className="stat-item">
              <span className="stat-label">评论</span>
              <span className="stat-value">{userStats.commentCount || 0}</span>
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserInfoCard; 