import React, { useState, useRef, useEffect } from 'react';
import { MailOutlined, FullscreenOutlined, FullscreenExitOutlined, SendOutlined, LoadingOutlined } from '@ant-design/icons';
import ChatMessage, { MessageList } from './ChatMessage';
import chatService from '../service/chatService';
import '../css/ChatDropdown.css';

// 模拟聊天用户信息 - 需要根据实际用户系统获取
const CHAT_USERS = [
  {
    id: '1',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=felix',
    username: '小明'
  },
  {
    id: '2', 
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=annie',
    username: '小红'
  },
  {
    id: '3',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=sarah',
    username: '张同学'
  },
  {
    id: '4',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=alex',
    username: '李华'
  },
  {
    id: '5',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=mike',
    username: '王小美'
  }
];

// 当前用户信息 - 应该从登录状态获取
const getCurrentUser = () => {
  // TODO: 从实际的用户认证系统获取当前用户信息
  const currentUser = localStorage.getItem('currentUser');
  if (currentUser) {
    return JSON.parse(currentUser);
  }
  
  // 临时默认用户
  return {
    id: 'current',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=current',
    username: '我',
  };
};

const ChatDropdown = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [isInitializing, setIsInitializing] = useState(false);
  const [sendingMessage, setSendingMessage] = useState(false);
  const [currentUser] = useState(getCurrentUser());
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);

  // 初始化聊天室 - 获取历史消息
  const initializeChatRoom = async () => {
    if (isInitializing) return;
    
    setIsInitializing(true);
    try {
      const result = await chatService.initializeChatRoom(30);
      if (result.success) {
        setMessages(result.messages);
        setHasMore(result.hasMore);
        console.log('✅ 聊天室初始化成功，加载了', result.messages.length, '条历史消息');
      } else {
        console.error('❌ 聊天室初始化失败:', result.message);
        // 初始化失败时使用空消息列表
        setMessages([]);
        setHasMore(false);
      }
    } catch (error) {
      console.error('❌ 聊天室初始化异常:', error);
      setMessages([]);
      setHasMore(false);
    } finally {
      setIsInitializing(false);
    }
  };

  // 打开聊天面板时初始化
  useEffect(() => {
    if (isOpen && messages.length === 0 && !isInitializing) {
      initializeChatRoom();
    }
  }, [isOpen]);

  // 滚动到顶部时加载更多
  const handleScroll = () => {
    if (!messagesContainerRef.current) return;
    if (messagesContainerRef.current.scrollTop === 0 && hasMore && !loadingMore) {
      loadMoreMessages();
    }
  };

  // 滚动到底部
  const scrollToBottom = () => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  // 新消息自动滚动到底部
  useEffect(() => {
    if (isOpen) scrollToBottom();
  }, [messages, isOpen]);

  // 加载更多历史消息
  const loadMoreMessages = async () => {
    if (loadingMore || !hasMore) return;
    
    setLoadingMore(true);
    try {
      const result = await chatService.loadMoreMessages(messages, 20);
      if (result.success && result.messages.length > 0) {
        // 将新消息添加到现有消息列表的前面（因为是历史消息）
        setMessages(prev => [...result.messages, ...prev]);
        setHasMore(result.hasMore);
        console.log('✅ 加载了', result.messages.length, '条更多历史消息');
      } else {
        setHasMore(false);
        console.log('📝 没有更多历史消息了');
      }
    } catch (error) {
      console.error('❌ 加载更多消息失败:', error);
      setHasMore(false);
    } finally {
      setLoadingMore(false);
    }
  };

  // 发送消息
  const handleSend = async () => {
    if (!input.trim() || sendingMessage) return;
    
    const messageContent = input.trim();
    setInput(''); // 立即清空输入框
    setSendingMessage(true);
    
    // 立即添加消息到界面（乐观更新）
    const tempMessage = {
      id: `temp_${Date.now()}`,
      text: messageContent,
      sender: currentUser.id,
      timestamp: new Date(),
      messageType: 'sending' // 标记为发送中
    };
    setMessages(prev => [...prev, tempMessage]);
    
    try {
      const result = await chatService.sendMessage(messageContent);
      
      if (result.success) {
        // 发送成功，更新消息状态
        setMessages(prev => prev.map(msg => 
          msg.id === tempMessage.id 
            ? { ...msg, messageType: 'sent', id: result.data?.id || msg.id }
            : msg
        ));
        console.log('✅ 消息发送成功');
      } else {
        // 发送失败，标记消息为失败状态
        setMessages(prev => prev.map(msg => 
          msg.id === tempMessage.id 
            ? { ...msg, messageType: 'failed' }
            : msg
        ));
        console.error('❌ 消息发送失败:', result.message);
        // 可以在这里显示错误提示
      }
    } catch (error) {
      // 发送异常，标记消息为失败状态
      setMessages(prev => prev.map(msg => 
        msg.id === tempMessage.id 
          ? { ...msg, messageType: 'failed' }
          : msg
      ));
      console.error('❌ 消息发送异常:', error);
    } finally {
      setSendingMessage(false);
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

  // 未全屏和全屏时都禁止页面滚动
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

  // Esc 键退出全屏
  useEffect(() => {
    if (!isFullscreen) return;
    const handleEsc = (e) => {
      if (e.key === 'Escape') setIsFullscreen(false);
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [isFullscreen]);

  // 未全屏时点击遮罩关闭
  const handleMaskClick = (e) => {
    if (e.target.classList.contains('chat-mask') || e.target.classList.contains('chat-fullscreen-mask')) {
      setIsOpen(false);
      setIsFullscreen(false);
    }
  };

  // 获取用户信息
  const getUserInfo = (senderId) => {
    // 如果是当前用户
    if (senderId === currentUser.id || senderId === 'current') {
      return currentUser;
    }
    
    // 查找已知用户
    const user = CHAT_USERS.find(user => user.id === senderId);
    if (user) {
      return user;
    }
    
    // 如果找不到用户，创建一个默认用户信息
    // TODO: 应该调用用户服务API获取真实用户信息
    console.warn(`未找到用户 ${senderId}，使用默认用户信息`);
    return {
      id: senderId,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${senderId}`,
      username: `用户${senderId}`
    };
  };

  // 渲染单条消息（左右布局区分自己和他人）
  function ChatBubble({ message }) {
    const isCurrentUser = message.sender === currentUser.id || message.sender === 'current';
    const userInfo = getUserInfo(message.sender);
    
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
      // 当前用户的消息 - 显示在右边，使用外层容器强制右对齐
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
              <img src={userInfo.avatar} alt="头像" style={{
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                objectFit: 'cover',
                background: '#f0f0f0'
              }} />
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
              }}>{userInfo.username}</div>
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
              }}>{message.text}</div>
              <div style={{
                fontSize: '11px',
                color: '#999',
                lineHeight: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end'
              }}>
                {chatService.formatMessageTime(message.timestamp)}
                {getMessageStatusIcon()}
              </div>
            </div>
          </div>
        </div>
      );
    } else {
      // 其他用户的消息 - 显示在左边
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
              <img src={userInfo.avatar} alt="头像" style={{
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                objectFit: 'cover',
                background: '#f0f0f0'
              }} />
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
              }}>{userInfo.username}</div>
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
              }}>{message.text}</div>
              <div style={{
                fontSize: '11px',
                color: '#999',
                lineHeight: 1
              }}>
                {chatService.formatMessageTime(message.timestamp)}
              </div>
            </div>
          </div>
        </div>
      );
    }
  }

  // 修改 MessageList 渲染为 ChatBubble
  const MessageList = ({ messages }) => (
    <>
      {messages.map((msg) => (
        <ChatBubble key={msg.id} message={msg} />
      ))}
    </>
  );

  return (
    <div className="chat-dropdown">
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
                style={{flex: 1, minHeight: 0, maxHeight: '100vh', overflowY: 'auto'}}
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
                {!isInitializing && <MessageList messages={messages} />}
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
                />
                <button className="send-btn" onClick={handleSend} disabled={!input.trim() || sendingMessage}>
                  {sendingMessage ? <LoadingOutlined spin /> : <SendOutlined />}
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
                {!isInitializing && <MessageList messages={messages} />}
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
                />
                <button className="send-btn" onClick={handleSend} disabled={!input.trim() || sendingMessage}>
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
