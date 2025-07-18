import React, { useState, useEffect } from 'react';
import { useUser } from '../lib/UserContext';
import '../css/UserDisplayName.css';

// 用户显示名称组件
const UserDisplayName = ({ 
  userId, 
  showUsername = true, 
  showNickname = true, 
  className = '',
  style = {},
  forMention = false // 用于@提及场景，只显示用户名
}) => {
  const { getUserCommunityInfoById } = useUser();
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserInfo = async () => {
      if (!userId) {
        setLoading(false);
        return;
      }
      
      try {
        setLoading(true);
        const communityInfo = await getUserCommunityInfoById(userId);
        setUserInfo(communityInfo);
      } catch (error) {
        console.error('获取用户信息失败:', error);
        setUserInfo(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [userId, getUserCommunityInfoById]);

  // 如果是@提及场景，只显示用户名
  if (forMention) {
    const username = userInfo?.username || `用户${userId}`;
    return (
      <span className={className} style={style}>
        {username}
      </span>
    );
  }

  // 加载状态或没有用户信息
  if (loading || !userInfo) {
    return (
      <span className={className} style={style}>
        用户
      </span>
    );
  }

  const { nickname, username } = userInfo;
  const hasNickname = !!nickname;
  const hasUsername = !!username;

  // 如果只有用户名，显示用户名
  if (!hasNickname && hasUsername) {
    return (
      <span className={className} style={style}>
        {username}
      </span>
    );
  }

  // 如果只有昵称，显示昵称
  if (hasNickname && !hasUsername) {
    return (
      <span className={className} style={style}>
        {nickname}
      </span>
    );
  }

  // 如果同时有昵称和用户名，分开显示
  if (hasNickname && hasUsername) {
    return (
      <span className={className} style={style}>
        <span className="user-nickname">{nickname}</span>
        <span className="user-username">@{username}</span>
      </span>
    );
  }

  // 默认情况
  return (
    <span className={className} style={style}>
      用户
    </span>
  );
};

export default UserDisplayName; 