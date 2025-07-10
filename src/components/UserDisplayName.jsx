import React from 'react';
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
  const { getUserDisplayInfoByIdSync, getUserUsernameById } = useUser();

  // 如果是@提及场景，只显示用户名
  if (forMention) {
    const username = getUserUsernameById(userId);
    return (
      <span className={className} style={style}>
        {username}
      </span>
    );
  }

  // 获取用户显示信息
  const userInfo = getUserDisplayInfoByIdSync(userId);
  
  if (!userInfo) {
    return (
      <span className={className} style={style}>
        用户
      </span>
    );
  }

  const { nickname, username, hasNickname, hasUsername } = userInfo;

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