import React, { useState, useRef, useEffect } from 'react';
import { MailOutlined, FullscreenOutlined, FullscreenExitOutlined, SendOutlined, LoadingOutlined } from '@ant-design/icons';
import { useUser } from '../lib/UserContext'; // 引入UserContext
import { useWebSocket } from '../utils/ws'; // 引入WebSocketService
import '../css/ChatDropdown.css';

// 模拟聊天用户信息
const CHAT_USERS = [
  {
    id: 'user1',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=felix',
    username: '小明'
  },
  {
    id: 'user2', 
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=annie',
    username: '小红'
  },
  {
    id: 'user3',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=sarah',
    username: '张同学'
  },
  {
    id: 'user4',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=alex',
    username: '李华'
  },
  {
    id: 'user5',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=mike',
    username: '王小美'
  }
];

// 模拟历史消息
const mockMessages = [
  { id: '1', text: '大家好，新来的同学！', sender: 'user1', timestamp: new Date(Date.now() - 1000 * 60 * 10) },
  { id: '2', text: '欢迎欢迎～有什么问题可以随时问', sender: 'user2', timestamp: new Date(Date.now() - 1000 * 60 * 9) },
  { id: '3', text: '今天的作业好难啊😭', sender: 'user3', timestamp: new Date(Date.now() - 1000 * 60 * 8) },
  { id: '4', text: '我也觉得，尤其是数学题', sender: 'user4', timestamp: new Date(Date.now() - 1000 * 60 * 7) },
  { id: '5', text: '要不我们组个学习小组？', sender: 'user5', timestamp: new Date(Date.now() - 1000 * 60 * 6) },
  { id: '6', text: '好主意！我也想参加', sender: 'user1', timestamp: new Date(Date.now() - 1000 * 60 * 5) },
  { id: '7', text: '图书馆见面怎么样？', sender: 'user2', timestamp: new Date(Date.now() - 1000 * 60 * 4) },
  { id: '8', text: '今天下午有空吗？', sender: 'user3', timestamp: new Date(Date.now() - 1000 * 60 * 3) },
];

// 当前用户信息
const CURRENT_USER = {
  id: 'current',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=current',
  username: '我',
};

const ChatDropdown = () => {
  const { userProfile, getUserCommunityInfoById } = useUser();
  const [isOpen, setIsOpen] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState(null);
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);
  const [page, setPage] = useState(1);

  // 使用WebSocket Hook
  const { sendMessage, fetchPastMessages, setOnMessage, setOnError, setOnConnect, setOnDisconnect } = useWebSocket(
      userProfile?.userId,
      getUserCommunityInfoById
  );

  // 滚动到底部
  const scrollToBottom = () => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  // 新消息或打开聊天室时滚动到底部
  useEffect(() => {
    if (isOpen) scrollToBottom();
  }, [messages, isOpen]);

  // 设置WebSocket回调
  useEffect(() => {
    if (!isOpen) return;

    // 处理接收到的消息
    setOnMessage(async (message) => {
      if (message.type === 'text') {
        const senderInfo = await getUserCommunityInfoById(message.content.from_userId);
        setMessages((prev) => [
          ...prev,
          {
            id: message.content.id,
            userId: message.content.from_userId,
            username: senderInfo.username || `用户${message.content.from_userId}`,
            nickname: senderInfo.nickname || `用户${message.content.from_userId}`,
            avatar: senderInfo.avatar,
            content: message.content.content,
            createdAt: new Date(message.content.createdAt)
          }
        ]);
      }
    });

    // 处理错误
    setOnError((err) => {
      setError(err);
    });

    // 连接成功时加载历史消息
    setOnConnect(() => {
      console.log('WebSocket连接成功');
      setLoadingMore(true);
      fetchPastMessages(1, 20)
          .then((pastMessages) => {
            setMessages(pastMessages);
            setPage(1);
            setHasMore(pastMessages.length === 20); // 假设每页20条
          })
          .catch((err) => {
            setError('加载历史消息失败');
          })
          .finally(() => {
            setLoadingMore(false);
          });
    });

    // 处理连接断开
    setOnDisconnect(() => {
      console.log('WebSocket已断开');
    });
  }, [isOpen, setOnMessage, setOnError, setOnConnect, setOnDisconnect, fetchPastMessages, getUserCommunityInfoById]);

  // 加载更多历史消息
  const loadMoreMessages = () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    //TODO 获取之前信息的分页查询可能有问题，主要是新旧消息的前后排列
    fetchPastMessages(page + 1, 20)
        .then((pastMessages) => {
          setMessages((prev) => [...pastMessages, ...prev]); // 新消息在下
          setPage(page + 1);
          setHasMore(pastMessages.length === 20);
        })
        .catch((err) => {
          setError('加载更多消息失败');
        })
        .finally(() => {
          setLoadingMore(false);
        });
  };

  // 滚动到顶部时加载更多
  const handleScroll = () => {
    if (!messagesContainerRef.current) return;
    if (messagesContainerRef.current.scrollTop === 0 && hasMore && !loadingMore) {
      loadMoreMessages();
    }
  };

  // 发送消息
  const handleSend = async () => {
    if (!input.trim()) return;
    if (!userProfile?.userId) {
      setError('未登录，无法发送消息');
      return;
    }
    setLoadingMore(true);
    const success = await sendMessage(input);
    if (success) {
      setInput('');
    } else {
      setError('发送消息失败');
    }
    setLoadingMore(false);
  };

  // 键盘回车发送
  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  // 点击外部关闭弹窗
  useEffect(() => {
    if (!isOpen) return;
    const handleClickOutside = (event) => {
      if (messagesContainerRef.current && !messagesContainerRef.current.parentNode.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  // 未全屏和全屏时禁止页面滚动
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  // Esc键退出全屏
  useEffect(() => {
    if (!isFullscreen) return;
    const handleEsc = (e) => {
      if (e.key === 'Escape') setIsFullscreen(false);
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [isFullscreen]);

  // 点击遮罩关闭
  const handleMaskClick = (e) => {
    if (e.target.classList.contains('chat-mask') || e.target.classList.contains('chat-fullscreen-mask')) {
      setIsOpen(false);
      setIsFullscreen(false);
    }
  };

  // // 获取用户信息
  // const getUserInfo = async (userId) => {
  //   if (userId === userProfile?.userId) {
  //     return {
  //       userId: userProfile.userId,
  //       username: userProfile.username || '我',
  //       nickname: userProfile.nickname,
  //       avatar: userProfile.avatar
  //     };
  //   }
  //   const senderInfo = await getUserCommunityInfoById(userId);
  //   return {
  //     userId: senderInfo.userId,
  //     username: senderInfo.username || `用户${userId}`,
  //     nickname: senderInfo.nickname,
  //     avatar: senderInfo.avatar
  //   };
  // };

  // 渲染单条消息（左右布局区分自己和他人）
  function ChatBubble({ message }) {
    const isCurrentUser = message.userId === userProfile?.userId;
    const userInfo = message; // 消息已包含username, nickname, avatar

    console.log('消息渲染调试:', {
      messageId: message.id,
      userId: message.userId,
      isCurrentUser,
      content: message.content.substring(0, 20)
    });

    if (isCurrentUser) {
      console.log('渲染当前用户消息 - 右边');
      return (
          <div style={{
            width: '100%',
            display: 'flex',
            justifyContent: 'flex-end',
            marginBottom: '4px'
          }}>
            <div style={{
              display: 'flex',
              alignItems: 'flex-start',
              flexDirection: 'row-reverse',
              padding: '8px 16px',
              background: 'rgba(24, 144, 255, 0.05)',
              borderRadius: '8px',
              maxWidth: '85%'
            }}>
              <div style={{
                flexShrink: 0,
                marginLeft: '12px',
                marginRight: '0'
              }}>
                {userInfo.avatar ? (
                    <img src={userInfo.avatar} alt="头像" style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      objectFit: 'cover',
                      background: '#f0f0f0'
                    }} />
                ) : (
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: '#f0f0f0',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}>
                      {userInfo.username?.charAt(0).toUpperCase()}
                    </div>
                )}
              </div>
              <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'flex-end'
              }}>
                <div style={{
                  fontSize: '13px',
                  fontWeight: 500,
                  color: '#666',
                  marginBottom: '4px',
                  lineHeight: 1.2
                }}>{userInfo.nickname || userInfo.username}</div>
                <div style={{
                  fontSize: '14px',
                  color: 'white',
                  lineHeight: 1.4,
                  wordWrap: 'break-word',
                  whiteSpace: 'pre-wrap',
                  marginBottom: '4px',
                  background: '#1890ff',
                  padding: '10px 14px',
                  borderRadius: '18px 18px 4px 18px',
                  display: 'inline-block',
                  boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
                }}>{message.content}</div>
                <div style={{
                  fontSize: '11px',
                  color: '#999',
                  lineHeight: 1
                }}>
                  {message.createdAt.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
            </div>
          </div>
      );
    } else {
      console.log('渲染其他用户消息 - 左边');
      return (
          <div style={{
            width: '100%',
            display: 'flex',
            justifyContent: 'flex-start',
            marginBottom: '4px'
          }}>
            <div style={{
              display: 'flex',
              alignItems: 'flex-start',
              flexDirection: 'row',
              padding: '8px 16px',
              background: 'transparent',
              borderRadius: '8px',
              maxWidth: '85%'
            }}>
              <div style={{
                flexShrink: 0,
                marginLeft: '0',
                marginRight: '12px'
              }}>
                {userInfo.avatar ? (
                    <img src={userInfo.avatar} alt="头像" style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      objectFit: 'cover',
                      background: '#f0f0f0'
                    }} />
                ) : (
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: '#f0f0f0',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}>
                      {userInfo.username?.charAt(0).toUpperCase()}
                    </div>
                )}
              </div>
              <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'flex-start'
              }}>
                <div style={{
                  fontSize: '13px',
                  fontWeight: 500,
                  color: '#666',
                  marginBottom: '4px',
                  lineHeight: 1.2
                }}>{userInfo.nickname || userInfo.username}</div>
                <div style={{
                  fontSize: '14px',
                  color: '#333',
                  lineHeight: 1.4,
                  wordWrap: 'break-word',
                  whiteSpace: 'pre-wrap',
                  marginBottom: '4px',
                  background: '#f5f5f5',
                  padding: '10px 14px',
                  borderRadius: '18px 18px 18px 4px',
                  display: 'inline-block',
                  boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
                }}>{message.content}</div>
                <div style={{
                  fontSize: '11px',
                  color: '#999',
                  lineHeight: 1
                }}>
                  {message.createdAt.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
            </div>
          </div>
      );
    }
  }

  // MessageList 组件
  const MessageList = ({ messages }) => (
      <>
        {messages.map((msg) => (
            <ChatBubble key={msg.id} message={msg} />
        ))}
      </>
  );

  return (
      <div className="chat-dropdown">
        {error && (
            <div style={{ color: 'red', textAlign: 'center', padding: '10px' }}>{error}</div>
        )}
        <button className="chat-trigger" onClick={() => setIsOpen(v => !v)}>
          <MailOutlined />
        </button>
        {isOpen && (
            isFullscreen ? (
                <div className="chat-fullscreen-mask" onClick={handleMaskClick}>
                  <div className="chat-panel fullscreen" onClick={e => e.stopPropagation()}>
                    <div className="chat-header">
                      <span>群聊</span>
                      <button className="fullscreen-btn" onClick={() => setIsFullscreen(false)}>
                        <FullscreenExitOutlined />
                      </button>
                      <button className="close-btn" onClick={() => { setIsOpen(false); setIsFullscreen(false); }}>&times;</button>
                    </div>
                    <div
                        className="chat-messages-container"
                        ref={messagesContainerRef}
                        onScroll={handleScroll}
                        style={{ flex: 1, minHeight: 0, maxHeight: '100vh', overflowY: 'auto' }}
                    >
                      {hasMore && (
                          <div className="load-more" onClick={loadMoreMessages}>
                            {loadingMore ? <LoadingOutlined spin /> : '加载更多...'}
                          </div>
                      )}
                      <MessageList messages={messages} />
                      <div ref={messagesEndRef} />
                    </div>
                    <div className="chat-input-bar">
                <textarea
                    className="chat-input"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="输入消息..."
                    rows={1}
                    disabled={!userProfile?.userId}
                />
                      <button className="send-btn" onClick={handleSend} disabled={!input.trim() || !userProfile?.userId}>
                        <SendOutlined />
                      </button>
                    </div>
                  </div>
                </div>
            ) : (
                <div className="chat-mask" onClick={handleMaskClick}>
                  <div className="chat-panel" onClick={e => e.stopPropagation()}>
                    <div className="chat-header">
                      <span>群聊</span>
                      <button className="fullscreen-btn" onClick={() => setIsFullscreen(true)}>
                        <FullscreenOutlined />
                      </button>
                      <button className="close-btn" onClick={() => setIsOpen(false)}>&times;</button>
                    </div>
                    <div
                        className="chat-messages-container"
                        ref={messagesContainerRef}
                        onScroll={handleScroll}
                    >
                      {hasMore && (
                          <div className="load-more" onClick={loadMoreMessages}>
                            {loadingMore ? <LoadingOutlined spin /> : '加载更多...'}
                          </div>
                      )}
                      <MessageList messages={messages} />
                      <div ref={messagesEndRef} />
                    </div>
                    <div className="chat-input-bar">
                <textarea
                    className="chat-input"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="输入消息..."
                    rows={1}
                    disabled={!userProfile?.userId}
                />
                      <button className="send-btn" onClick={handleSend} disabled={!input.trim() || !userProfile?.userId}>
                        <SendOutlined />
                      </button>
                    </div>
                  </div>
                </div>
            )
        )}
      </div>
  );
};

export default ChatDropdown;
