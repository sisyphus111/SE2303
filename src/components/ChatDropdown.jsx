import React, { useState, useRef, useEffect } from 'react';
import { MailOutlined, FullscreenOutlined, FullscreenExitOutlined, SendOutlined, LoadingOutlined } from '@ant-design/icons';
import ChatMessage, { MessageList } from './ChatMessage';
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
  const [isOpen, setIsOpen] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [messages, setMessages] = useState([...mockMessages]);
  const [input, setInput] = useState('');
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true); // 是否还有更多历史消息
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);

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

  // 加载更多历史消息（模拟）
  const loadMoreMessages = () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    setTimeout(() => {
      // 模拟更多历史消息
      const moreMessages = [
        { id: `old_${Date.now()}_1`, text: '昨天的聚会很开心呢！', sender: 'user2', timestamp: new Date(Date.now() - 1000 * 60 * 20) },
        { id: `old_${Date.now()}_2`, text: '是啊，下次再一起玩', sender: 'user4', timestamp: new Date(Date.now() - 1000 * 60 * 19) },
        { id: `old_${Date.now()}_3`, text: '明天有课吗？', sender: 'user1', timestamp: new Date(Date.now() - 1000 * 60 * 18) },
      ];
      setMessages(prev => [...moreMessages, ...prev]);
      setHasMore(false); // 只模拟一次
      setLoadingMore(false);
    }, 1000);
  };

  // 滚动到顶部时加载更多
  const handleScroll = () => {
    if (!messagesContainerRef.current) return;
    if (messagesContainerRef.current.scrollTop === 0 && hasMore && !loadingMore) {
      loadMoreMessages();
    }
  };

  // 发送消息
  const handleSend = () => {
    if (!input.trim()) return;
    const newMsg = {
      id: `msg_${Date.now()}`,
      text: input,
      sender: 'current',
      timestamp: new Date(),
    };
    setMessages(prev => [...prev, newMsg]);
    setInput('');
    
    // 模拟其他用户的随机回复
    setTimeout(() => {
      const randomUser = CHAT_USERS[Math.floor(Math.random() * CHAT_USERS.length)];
      const randomReplies = [
        '哈哈，有道理！',
        '我也这么想',
        '确实如此',
        '说得对！',
        '有意思～',
        '我同意',
        '好的好的',
        '嗯嗯，明白了'
      ];
      const randomReply = randomReplies[Math.floor(Math.random() * randomReplies.length)];
      
      setMessages(prev => [...prev, {
        id: `reply_${Date.now()}`,
        text: randomReply,
        sender: randomUser.id,
        timestamp: new Date(),
      }]);
    }, 1000 + Math.random() * 2000); // 1-3秒随机延迟
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
    if (senderId === 'current') return CURRENT_USER;
    
    const user = CHAT_USERS.find(user => user.id === senderId);
    if (user) {
      return user;
    }
    
    // 如果找不到用户，根据senderId创建一个随机用户
    console.warn(`未找到用户 ${senderId}，使用默认用户`);
    return {
      id: senderId,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${senderId}`,
      username: `用户${senderId.slice(-1)}`
    };
  };

  // 渲染单条消息（左右布局区分自己和他人）
  function ChatBubble({ message }) {
    const isCurrentUser = message.sender === 'current';
    const userInfo = getUserInfo(message.sender);
    
    // 添加调试信息
    console.log('消息渲染调试:', {
      messageId: message.id,
      sender: message.sender,
      isCurrentUser: isCurrentUser,
      text: message.text.substring(0, 20)
    });
    
    if (isCurrentUser) {
      console.log('渲染当前用户消息 - 应该在右边');
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
                lineHeight: 1
              }}>
                {message.timestamp.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
              </div>
            </div>
          </div>
        </div>
      );
    } else {
      console.log('渲染其他用户消息 - 应该在左边');
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
                {message.timestamp.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
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
                />
                <button className="send-btn" onClick={handleSend} disabled={!input.trim()}>
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
                />
                <button className="send-btn" onClick={handleSend} disabled={!input.trim()}>
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
