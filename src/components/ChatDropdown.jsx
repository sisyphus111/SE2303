import React, { useState, useRef, useEffect, useCallback } from 'react';
import { MailOutlined, FullscreenOutlined, FullscreenExitOutlined, SendOutlined, LoadingOutlined } from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { useWebSocket } from '../utils/ws';
import '../css/ChatDropdown.css';

// 渲染单条消息（左右布局区分自己和他人）
function ChatBubble({ message, currentUserId }) {
  const isCurrentUser = message.userId === currentUserId;
  const userInfo = {
    id: message.userId,
    username: message.username,
    nickname: message.nickname,
    avatar: message.avatar
  };

  // 头像组件 - 增强的头像显示逻辑
  const Avatar = ({ userInfo, size = 40 }) => {
    const [imageError, setImageError] = useState(false);
    const [imageLoading, setImageLoading] = useState(true);
    
    // 检查头像URL是否有效
    const hasValidAvatar = userInfo.avatar && 
                          typeof userInfo.avatar === 'string' && 
                          userInfo.avatar.trim() !== '' &&
                          !imageError;

    // 根据用户ID生成不同的背景色
    const getAvatarBgColor = (userId) => {
      const colors = [
        'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
        'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
        'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
        'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)'
      ];
      const index = parseInt(userId || '0') % colors.length;
      return colors[index];
    };

    // 图片加载错误处理
    const handleImageError = () => {
      setImageError(true);
      setImageLoading(false);
    };

    // 图片加载成功处理
    const handleImageLoad = () => {
      setImageLoading(false);
      setImageError(false);
    };

    // 如果有有效头像且未出错，显示图片
    if (hasValidAvatar) {
      return (
        <div style={{ position: 'relative' }}>
          <img 
            src={userInfo.avatar} 
            alt="头像" 
            style={{
              width: `${size}px`,
              height: `${size}px`,
              borderRadius: '50%',
              objectFit: 'cover',
              background: '#f0f0f0',
              display: imageLoading ? 'none' : 'block',
              border: '2px solid #fff',
              boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
            }}
            onError={handleImageError}
            onLoad={handleImageLoad}
          />
          {imageLoading && (
            <div style={{
              width: `${size}px`,
              height: `${size}px`,
              borderRadius: '50%',
              background: '#f0f0f0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '12px',
              color: '#999',
              border: '2px solid #fff',
              boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
            }}>
              <LoadingOutlined />
            </div>
          )}
        </div>
      );
    }

    // 默认头像：显示用户名首字母，根据用户ID使用不同颜色
    return (
      <div style={{
        width: `${size}px`,
        height: `${size}px`,
        borderRadius: '50%',
        background: getAvatarBgColor(userInfo.id),
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: 'white',
        fontSize: `${size * 0.4}px`,
        fontWeight: 'bold',
        border: '2px solid #fff',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      }}>
        {(userInfo.nickname || userInfo.username || '?').charAt(0).toUpperCase()}
      </div>
    );
  };

  // 消息状态指示器
  const getMessageStatusIcon = () => {
    if (!isCurrentUser) return null;

    switch (message.messageType) {
      case 'sending':
        return <LoadingOutlined style={{ fontSize: '12px', color: '#999', marginLeft: '4px' }} />;
      case 'failed':
        return <span style={{ fontSize: '12px', color: '#ff4d4f', marginLeft: '4px' }}>失败</span>;
      case 'sent':
      default:
        return null;
    }
  };

  if (isCurrentUser) {
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
              <Avatar userInfo={userInfo} size={40} />
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
                lineHeight: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end'
              }}>
                {new Date(message.createdAt).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
                {getMessageStatusIcon()}
              </div>
            </div>
          </div>
        </div>
    );
  } else {
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
              <Avatar userInfo={userInfo} size={40} />
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
                {new Date(message.createdAt).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
              </div>
            </div>
          </div>
        </div>
    );
  }
}

// MessageList 组件
const MessageList = ({ messages, currentUserId }) => (
    <>
      {messages.map((msg) => (
          <ChatBubble key={msg.id} message={msg} currentUserId={currentUserId} />
      ))}
    </>
);

const ChatDropdown = () => {
  const { userProfile, getUserCommunityInfoById } = useUser();
  const [isOpen, setIsOpen] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [isInitializing, setIsInitializing] = useState(false);
  const [sendingMessage, setSendingMessage] = useState(false);
  const [error, setError] = useState(null);
  const [lastScrollTrigger, setLastScrollTrigger] = useState(0); // 防重复触发
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);
  const inputRef = useRef(null); // 添加输入框ref
  const [page, setPage] = useState(1); // 修复：页码从1开始，符合后端期望

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

  // 聊天框打开或切换模式时自动聚焦输入框
  useEffect(() => {
    if (isOpen) {
      // 延迟聚焦，确保DOM已经渲染完成
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
        }
      }, 100);
    }
  }, [isOpen, isFullscreen]);

  // 新消息或打开聊天室时滚动到底部
  useEffect(() => {
    if (isOpen) scrollToBottom();
  }, [messages, isOpen]);

  // 初始化聊天室和WebSocket回调
  useEffect(() => {
    if (!isOpen || !userProfile?.userId) {
      // 关闭时重置状态
      if (!isOpen) {
        setMessages([]);
        setHasMore(true);
        setPage(1); // 修复：重置页码为1（第一页），符合后端期望
        setIsInitializing(false);
        setLoadingMore(false);
        setError(null);
        setLastScrollTrigger(0);
      }
      return;
    }

    let isInitialized = false;

    // 初始化聊天室
    const initializeChatRoom = async () => {
      if (isInitializing || isInitialized) return;
      isInitialized = true;
      setIsInitializing(true);
      try {
        // 修复：获取最新的消息作为第一页内容
        // 通常聊天室的第一页应该是最新的消息，用户首先看到最近的对话
        const pastMessages = await fetchPastMessages(1, 10);
        // 直接使用后端返回的消息顺序，不进行任何排序
        console.log('🏠 初始化加载第一页消息:', pastMessages ? pastMessages.map(m => ({ id: m.id, createdAt: m.createdAt, content: m.content?.substring(0, 20) })) : []);
        setMessages(pastMessages || []);
        setPage(1); // 当前已加载的页数
        setHasMore((pastMessages || []).length === 10);
        console.log('✅ 聊天室初始化成功，显示第一页内容，加载了', (pastMessages || []).length, '条消息，当前页:', 1);
        
        // 滚动到底部显示最新消息
        setTimeout(() => {
          scrollToBottom();
        }, 100);
      } catch (err) {
        console.error('❌ 聊天室初始化失败:', err);
        setError('加载聊天记录失败');
        setMessages([]);
        setHasMore(false);
      } finally {
        setIsInitializing(false);
      }
    };

    // 设置WebSocket回调
    setOnMessage(async (message) => {
      if (message.type === 'text') {
        const senderInfo = await getUserCommunityInfoById(message.content.from_userId);
        const newMessage = {
          id: message.content.id,
          userId: message.content.from_userId,
          username: senderInfo.username || `用户${message.content.from_userId}`,
          nickname: senderInfo.nickname || `用户${message.content.from_userId}`,
          avatar: senderInfo.avatar,
          content: message.content.content,
          createdAt: new Date(message.content.createdAt),
          messageType: 'sent'
        };
        setMessages((prev) => {
          const existingTempIndex = prev.findIndex(msg => msg.id === `temp_${newMessage.id}`);
          if (existingTempIndex !== -1) {
            // 替换临时消息
            const newMessages = [...prev];
            newMessages[existingTempIndex] = newMessage;
            return newMessages;
          } else {
            // 检查是否已存在相同ID的消息，避免重复添加
            const existingMessageIndex = prev.findIndex(msg => msg.id === newMessage.id);
            if (existingMessageIndex !== -1) {
              return prev; // 消息已存在，不添加
            }
            return [...prev, newMessage];
          }
        });
        console.log('✅ 收到新消息:', newMessage);
      }
    });

    setOnError((err) => {
      setError(err);
      console.error('❌ WebSocket错误:', err);
    });

    setOnConnect(() => {
      console.log('✅ WebSocket连接成功');
      initializeChatRoom();
    });

    setOnDisconnect(() => {
      console.log('📴 WebSocket连接断开');
    });

    // 立即初始化聊天室（如果WebSocket已连接）
    initializeChatRoom();

    return () => {
      isInitialized = false;
    };
  }, [isOpen, userProfile?.userId]); // 移除fetchPastMessages等函数依赖，避免无限循环

  // 加载更多历史消息
  const loadMoreMessages = useCallback(async () => {
    if (loadingMore || !hasMore || isInitializing) {
      console.log('跳过加载更多:', { loadingMore, hasMore, isInitializing });
      return;
    }
    
    console.log('📜 开始加载更多历史消息, 当前页:', page, '将请求第', page + 1, '页');
    setLoadingMore(true);
    try {
      const pastMessages = await fetchPastMessages(page + 1, 10);
      if (pastMessages && pastMessages.length > 0) {
        setMessages((prev) => {
          // 过滤掉重复的消息
          const existingIds = new Set(prev.map(msg => msg.id));
          const newMessages = pastMessages.filter(msg => !existingIds.has(msg.id));
          console.log('📖 加载更多历史消息:', newMessages.map(m => ({ id: m.id, createdAt: m.createdAt, content: m.content?.substring(0, 20) })));
          console.log('📅 当前最旧消息:', prev.length > 0 ? { id: prev[0].id, createdAt: prev[0].createdAt } : '无');
          
          // 历史消息的合并策略：新加载的旧消息应该放在现有消息前面
          // 这样保持时间顺序：更旧的消息在上面，更新的消息在下面
          // 但是需要确保消息按时间正确排序
          const allMessages = [...newMessages, ...prev];
          
          // 按时间戳排序，确保消息按正确的时间顺序显示
          const sortedMessages = allMessages.sort((a, b) => {
            const timeA = new Date(a.createdAt).getTime();
            const timeB = new Date(b.createdAt).getTime();
            return timeA - timeB; // 升序排列：旧消息在前，新消息在后
          });
          
          const result = sortedMessages;
          console.log('📋 合并后消息顺序（显示前5条）:', result.slice(0, 5).map(m => ({ id: m.id, createdAt: m.createdAt, content: m.content?.substring(0, 20) })));
          return result;
        });
        setPage(page + 1);
        // 更严格的hasMore判断：只有当返回的消息数等于请求数时才认为还有更多
        setHasMore(pastMessages.length === 10);
        console.log('✅ 加载了', pastMessages.length, '条更多历史消息, 当前页:', page + 1, 'hasMore:', pastMessages.length === 10);
      } else {
        setHasMore(false);
        console.log('📝 没有更多历史消息了');
      }
    } catch (err) {
      console.error('❌ 加载更多消息失败:', err);
      setError('加载更多消息失败');
      setHasMore(false);
    } finally {
      setLoadingMore(false);
    }
  }, [loadingMore, hasMore, isInitializing, page]); // 移除fetchPastMessages依赖

  // 滚动到顶部时加载更多 - 添加防抖和更精确的判断
  const handleScroll = useCallback(() => {
    const container = messagesContainerRef.current;
    if (!container || loadingMore || !hasMore || isInitializing) return;
    
    // 更严格的条件：必须完全滚动到顶部
    const scrollTop = container.scrollTop;
    const shouldLoadMore = scrollTop <= 5; // 进一步缩小触发范围
    
    console.log('滚动检查:', { scrollTop, shouldLoadMore, loadingMore, hasMore, isInitializing });
    
    if (shouldLoadMore) {
      const now = Date.now();
      // 防止1秒内重复触发
      if (now - lastScrollTrigger < 1000) {
        console.log('滚动触发过于频繁，忽略');
        return;
      }
      
      console.log('触发加载更多消息');
      setLastScrollTrigger(now);
      loadMoreMessages();
    }
  }, [loadingMore, hasMore, isInitializing, loadMoreMessages, lastScrollTrigger]);

  // 发送消息
  const handleSend = async () => {
    if (!input.trim() || sendingMessage || !userProfile?.userId) return;

    const messageContent = input.trim();
    setInput('');
    setSendingMessage(true);

    // 乐观更新
    const tempMessage = {
      id: `temp_${Date.now()}`,
      userId: userProfile.userId,
      username: userProfile.username || '我',
      nickname: userProfile.nickname,
      avatar: userProfile.avatar,
      content: messageContent,
      createdAt: new Date(),
      messageType: 'sending'
    };
    setMessages((prev) => [...prev, tempMessage]);

    // 立即保持输入框焦点
    if (inputRef.current) {
      inputRef.current.focus();
    }

    try {
      const success = await sendMessage(messageContent);
      if (success) {
        setMessages((prev) => prev.map((msg) =>
            msg.id === tempMessage.id ? { ...msg, messageType: 'sent' } : msg
        ));
        console.log('✅ 消息发送成功');
      } else {
        setMessages((prev) => prev.map((msg) =>
            msg.id === tempMessage.id ? { ...msg, messageType: 'failed' } : msg
        ));
        setError('发送消息失败');
        console.error('❌ 消息发送失败');
      }
    } catch (err) {
      setMessages((prev) => prev.map((msg) =>
          msg.id === tempMessage.id ? { ...msg, messageType: 'failed' } : msg
      ));
      setError('发送消息失败');
      console.error('❌ 消息发送异常:', err);
    } finally {
      setSendingMessage(false);
      // 确保在发送完成后重新聚焦（使用多个时机确保焦点不丢失）
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
        }
      }, 0);
      
      // 添加一个稍后的备用聚焦，防止某些情况下焦点丢失
      setTimeout(() => {
        if (inputRef.current && document.activeElement !== inputRef.current) {
          inputRef.current.focus();
        }
      }, 50);
    }
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

  // 聊天框打开或切换模式时自动聚焦输入框
  useEffect(() => {
    if (isOpen) {
      // 延迟聚焦，确保DOM已经渲染完成
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
        }
      }, 100);
    }
  }, [isOpen, isFullscreen]);

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
                      {isInitializing && (
                          <div style={{
                            textAlign: 'center',
                            padding: '20px',
                            color: '#666'
                          }}>
                            <LoadingOutlined spin style={{ marginRight: '8px' }} />
                            正在加载聊天记录...
                          </div>
                      )}
                      {!isInitializing && hasMore && (
                          <div className="load-more" onClick={loadMoreMessages}>
                            {loadingMore ? <LoadingOutlined spin /> : '加载更多...'}
                          </div>
                      )}
                      {!isInitializing && <MessageList messages={messages} currentUserId={userProfile?.userId} />}
                      <div ref={messagesEndRef} />
                    </div>
                    <div className="chat-input-bar">
                <textarea
                    ref={inputRef}
                    className="chat-input"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder={userProfile?.userId ? '输入消息...' : '请登录后发送消息'}
                    rows={1}
                    disabled={!userProfile?.userId || sendingMessage}
                />
                      <button
                          className="send-btn"
                          onClick={handleSend}
                          disabled={!input.trim() || sendingMessage || !userProfile?.userId}
                      >
                        {sendingMessage ? <LoadingOutlined spin /> : <SendOutlined />}
                      </button>
                    </div>
                  </div>
                </div>                    ) : (
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
                      {isInitializing && (
                          <div style={{
                            textAlign: 'center',
                            padding: '20px',
                            color: '#666'
                          }}>
                            <LoadingOutlined spin style={{ marginRight: '8px' }} />
                            正在加载聊天记录...
                          </div>
                      )}
                      {!isInitializing && hasMore && (
                          <div className="load-more" onClick={loadMoreMessages}>
                            {loadingMore ? <LoadingOutlined spin /> : '加载更多...'}
                          </div>
                      )}
                      {!isInitializing && <MessageList messages={messages} currentUserId={userProfile?.userId} />}
                      <div ref={messagesEndRef} />
                    </div>
                    <div className="chat-input-bar">
                <textarea
                    ref={inputRef}
                    className="chat-input"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder={userProfile?.userId ? '输入消息...' : '请登录后发送消息'}
                    rows={1}
                    disabled={!userProfile?.userId || sendingMessage}
                />
                      <button
                          className="send-btn"
                          onClick={handleSend}
                          disabled={!input.trim() || sendingMessage || !userProfile?.userId}
                      >
                        {sendingMessage ? <LoadingOutlined spin /> : <SendOutlined />}
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