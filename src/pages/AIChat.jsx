import React, { useState, useRef, useEffect, useCallback } from 'react';
import '../css/AIChat.css';
import MarkdownRenderer from '../components/MarkdownRenderer';
import { 
  RobotOutlined, 
  SendOutlined, 
  ReloadOutlined,
  MoreOutlined,
  QuestionCircleOutlined,
  HeartOutlined,
  SmileOutlined,
  BookOutlined,
  UserOutlined,
  ClearOutlined,
  HistoryOutlined,
  StarOutlined,
  ThunderboltOutlined,
  BulbOutlined
} from '@ant-design/icons';

// 生成唯一ID的函数
let messageIdCounter = 0;
const generateUniqueId = () => {
  return `msg_${Date.now()}_${++messageIdCounter}`;
};

const AIChat = () => {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [connectionStatus, setConnectionStatus] = useState('connecting');
  const [currentStreamingMessage, setCurrentStreamingMessage] = useState('');
  const [isStreaming, setIsStreaming] = useState(false);
  const [showQuickReplies, setShowQuickReplies] = useState(true);
  
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);
  const webSocketRef = useRef(null);
  const reconnectTimeoutRef = useRef(null);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 5;
  const streamingEndProcessed = useRef(false);
  const lastScrollTop = useRef(0);
  const isUserScrolling = useRef(false);
  const isComponentMounted = useRef(false);

  // 快捷回复建议 - 分类
  const quickRepliesCategories = [
    {
      title: "情绪支持",
      icon: <HeartOutlined />,
      color: "#ff6b6b",
      replies: [
        { text: "我最近感到很焦虑", icon: <HeartOutlined /> },
        { text: "我心情有点低落", icon: <SmileOutlined /> },
        { text: "感觉压力很大", icon: <ThunderboltOutlined /> },
        { text: "想要调节情绪", icon: <StarOutlined /> }
      ]
    },
    {
      title: "学习生活",
      icon: <BookOutlined />,
      color: "#4ecdc4",
      replies: [
        { text: "如何提高学习效率？", icon: <BookOutlined /> },
        { text: "学习压力太大怎么办？", icon: <BulbOutlined /> },
        { text: "如何保持专注？", icon: <ThunderboltOutlined /> },
        { text: "时间管理技巧", icon: <HistoryOutlined /> }
      ]
    },
    {
      title: "人际关系",
      icon: <UserOutlined />,
      color: "#45b7d1",
      replies: [
        { text: "与室友相处困难", icon: <UserOutlined /> },
        { text: "如何建立友谊？", icon: <HeartOutlined /> },
        { text: "社交焦虑问题", icon: <QuestionCircleOutlined /> },
        { text: "恋爱关系困惑", icon: <StarOutlined /> }
      ]
    }
  ];

  const scrollToBottom = (force = false) => {
    if (!messagesContainerRef.current) return;
    
    const container = messagesContainerRef.current;
    
    // 确保容器存在且有内容
    if (container.scrollHeight <= container.clientHeight) return;
    
    const isAtBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 100;
    
    // 只有在强制滚动或用户在底部且没有主动向上滚动时才自动滚动
    if (force || (isAtBottom && !isUserScrolling.current)) {
      // 使用容器的scrollTop属性，只滚动对话框内部，不影响页面滚动
      try {
        container.scrollTo({
          top: container.scrollHeight,
          behavior: 'smooth'
        });
      } catch (e) {
        // 降级处理：直接设置scrollTop
        container.scrollTop = container.scrollHeight;
      }
    }
  };

  // 检测用户是否在滚动
  const handleScroll = () => {
    if (!messagesContainerRef.current) return;
    
    const container = messagesContainerRef.current;
    const currentScrollTop = container.scrollTop;
    const scrollDifference = lastScrollTop.current - currentScrollTop;
    
    // 只有当用户明显向上滚动超过20px时，才认为是主动滚动
    if (scrollDifference > 20) {
      isUserScrolling.current = true;
    } else if (scrollDifference < -10) {
      // 如果用户向下滚动，检查是否接近底部
      const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 150;
      if (isNearBottom) {
        isUserScrolling.current = false;
      }
    }
    
    lastScrollTop.current = currentScrollTop;
  };

  // 智能滚动：只有在用户在底部附近或刚发送消息时才滚动
  useEffect(() => {
    // 组件首次加载时不自动滚动，避免页面刷新时的意外滚动
    if (!isComponentMounted.current) {
      isComponentMounted.current = true;
      return;
    }
    
    if (messages.length === 0) return;
    
    // 使用延迟确保DOM已更新
    const timeoutId = setTimeout(() => {
      const lastMessage = messages[messages.length - 1];
      // 如果是用户发送的消息，重置滚动状态并滚动到底部
      if (lastMessage.sender === 'user') {
        isUserScrolling.current = false;
        scrollToBottom(true);
      } else {
        // 如果是AI消息，只有在用户没有向上滚动时才滚动
        scrollToBottom();
      }
    }, 50); // 短暂延迟确保DOM更新完成

    return () => clearTimeout(timeoutId);
  }, [messages]);

  // 流式消息的滚动控制 - 只有用户在底部时才滚动
  useEffect(() => {
    if (isStreaming && currentStreamingMessage) {
      // 只有在用户没有主动向上滚动且在底部附近时才自动滚动
      if (!isUserScrolling.current && messagesContainerRef.current) {
        const container = messagesContainerRef.current;
        const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 150;
        
        if (isNearBottom) {
          // 使用防抖，减少滚动频率
          const timeoutId = setTimeout(() => {
            scrollToBottom();
          }, 200);
          
          return () => clearTimeout(timeoutId);
        }
      }
    }
  }, [currentStreamingMessage, isStreaming]);

  // WebSocket连接函数
  const connectWebSocket = useCallback(() => {
    console.log('=== 尝试连接WebSocket ===');
    if (webSocketRef.current?.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接，跳过重复连接');
      return;
    }

    if (webSocketRef.current && webSocketRef.current.readyState !== WebSocket.CLOSED) {
      console.log('关闭现有WebSocket连接');
      webSocketRef.current.close();
    }

    try {
      setConnectionStatus('connecting');
      console.log('创建新的WebSocket连接');
      const ws = new WebSocket('ws://localhost:8081/ws/chat');
      
      ws.onopen = () => {
        console.log('WebSocket连接成功');
        setConnectionStatus('connected');
        reconnectAttempts.current = 0;
      };

      ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data);
          console.log('WebSocket收到原始消息:', message);
          handleWebSocketMessage(message);
        } catch (error) {
          console.error('解析消息失败:', error);
        }
      };

      ws.onclose = (event) => {
        console.log('WebSocket连接关闭:', event.code, event.reason);
        setConnectionStatus('disconnected');
        setIsStreaming(false);
        setCurrentStreamingMessage('');
        
        if (reconnectAttempts.current < maxReconnectAttempts) {
          reconnectAttempts.current++;
          const delay = Math.min(1000 * Math.pow(2, reconnectAttempts.current), 30000);
          console.log(`${delay/1000}秒后尝试重连 (第${reconnectAttempts.current}次)`);
          
          reconnectTimeoutRef.current = setTimeout(() => {
            connectWebSocket();
          }, delay);
        }
      };

      ws.onerror = (error) => {
        console.error('WebSocket错误:', error);
        setConnectionStatus('disconnected');
      };

      webSocketRef.current = ws;
    } catch (error) {
      console.error('创建WebSocket连接失败:', error);
      setConnectionStatus('disconnected');
    }
  }, []);

  // 处理WebSocket消息
  const handleWebSocketMessage = useCallback((message) => {
    const { type, content, timestamp } = message;
    console.log('=== 处理WebSocket消息 ===', { type, content: content?.substring(0, 50), timestamp });
    
    switch (type) {
      case 'ai':
        console.log('处理完整AI消息:', content?.substring(0, 50));
        setMessages(prev => {
          console.log('添加完整AI消息到列表，当前消息数:', prev.length);
          return [...prev, {
            id: generateUniqueId(),
            text: content,
            sender: 'ai',
            timestamp: new Date(timestamp)
          }];
        });
        break;
        
      case 'ai_chunk':
        console.log('处理AI流式片段:', content, '当前流式状态:', isStreaming);
        setIsStreaming(true);
        setCurrentStreamingMessage(prev => {
          const newMessage = prev + content;
          console.log('更新流式消息，长度从', prev.length, '到', newMessage.length);
          return newMessage;
        });
        streamingEndProcessed.current = false;
        break;
        
      case 'ai_end':
        console.log('收到AI_END消息，防重复处理标志:', streamingEndProcessed.current);
        
        if (streamingEndProcessed.current) {
          console.log('ai_end已处理过，跳过重复处理');
          break;
        }
        streamingEndProcessed.current = true;
        
        setIsStreaming(false);
        setIsLoading(false);
        
        setCurrentStreamingMessage(prevMessage => {
          console.log('处理ai_end，流式消息内容长度:', prevMessage.length);
          if (prevMessage.trim()) {
            console.log('将流式消息添加到列表:', prevMessage.substring(0, 50) + '...');
            setMessages(prev => {
              console.log('ai_end添加消息前，当前消息数:', prev.length);
              
              const lastMessage = prev[prev.length - 1];
              if (lastMessage && lastMessage.sender === 'ai' && lastMessage.text === prevMessage) {
                console.log('检测到重复消息，跳过添加');
                return prev;
              }
              
              const newMessages = [...prev, {
                id: generateUniqueId(),
                text: prevMessage,
                sender: 'ai',
                timestamp: new Date()
              }];
              console.log('ai_end添加消息后，新消息数:', newMessages.length);
              return newMessages;
            });
          } else {
            console.log('流式消息为空，不添加到列表');
          }
          return '';
        });
        break;
        
      case 'error':
        setMessages(prev => [...prev, {
          id: generateUniqueId(),
          text: content,
          sender: 'ai',
          timestamp: new Date(),
          isError: true
        }]);
        setIsLoading(false);
        setIsStreaming(false);
        setCurrentStreamingMessage('');
        break;
        
      default:
        console.log('未知消息类型:', type);
    }
  }, []);

  // 发送消息
  const handleSendMessage = async (messageText = inputText) => {
    if (!messageText.trim() || connectionStatus !== 'connected' || isLoading) {
      return;
    }

    const userMessage = {
      id: generateUniqueId(),
      text: messageText,
      sender: 'user',
      timestamp: new Date()
    };
    
    setMessages(prev => [...prev, userMessage]);
    setShowQuickReplies(false);
    
    // 用户发送消息时重置滚动状态，确保能看到回复
    isUserScrolling.current = false;
    
    try {
      const messageToSend = {
        type: 'user',
        content: messageText,
        timestamp: Date.now()
      };
      
      webSocketRef.current.send(JSON.stringify(messageToSend));
      setInputText('');
      setIsLoading(true);
      setCurrentStreamingMessage('');
      streamingEndProcessed.current = false;
    } catch (error) {
      console.error('发送消息失败:', error);
      setMessages(prev => [...prev, {
        id: generateUniqueId(),
        text: '发送消息失败，请检查网络连接',
        sender: 'ai',
        timestamp: new Date(),
        isError: true
      }]);
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleQuickReply = (replyText) => {
    handleSendMessage(replyText);
  };

  const clearMessages = () => {
    setMessages([]);
    setCurrentStreamingMessage('');
    setShowQuickReplies(true);
    isUserScrolling.current = false;
  };

  const formatTime = (timestamp) => {
    return timestamp.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const getConnectionStatusText = () => {
    switch (connectionStatus) {
      case 'connecting':
        return '连接中...';
      case 'connected':
        return '心理助手在线';
      case 'disconnected':
        return '连接断开';
      default:
        return '未知状态';
    }
  };

  const getConnectionStatusColor = () => {
    switch (connectionStatus) {
      case 'connected':
        return 'var(--primary-green)';
      case 'connecting':
        return 'var(--accent-green)';
      case 'disconnected':
        return '#f44336';
      default:
        return '#9E9E9E';
    }
  };

  useEffect(() => {
    connectWebSocket();
    
    // 添加body类来防止页面滚动
    document.body.classList.add('ai-chat-page');
    
    return () => {
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      if (webSocketRef.current) {
        webSocketRef.current.close();
      }
      // 移除body类，恢复页面滚动
      document.body.classList.remove('ai-chat-page');
    };
  }, [connectWebSocket]);

  return (
    <div className="ai-chat-container">
      {/* 聊天头部 */}
      <div className="chat-header">
        <div className="header-left">
          <div className="ai-avatar">
            <RobotOutlined />
          </div>
          <div className="header-info">
            <h2>心理健康助手</h2>
            <div className="connection-status-display">
              <span 
                className="status-dot" 
                style={{ backgroundColor: getConnectionStatusColor() }}
              />
              <span className="status-text">{getConnectionStatusText()}</span>
            </div>
          </div>
        </div>
        <div className="header-actions">
          <button 
            className="action-button" 
            onClick={clearMessages}
            title="清空对话"
          >
            <ClearOutlined />
          </button>
          <button 
            className="action-button" 
            onClick={() => setShowQuickReplies(!showQuickReplies)}
            title="快捷回复"
          >
            <ThunderboltOutlined />
          </button>
          <button className="action-button" title="更多选项">
            <MoreOutlined />
          </button>
        </div>
      </div>

      {/* 主要内容区域 */}
      <div className="chat-main">
        {/* 消息容器 */}
        <div 
          className="messages-container"
          ref={messagesContainerRef}
          onScroll={handleScroll}
        >
          {/* 欢迎消息 */}
          {messages.length === 0 && !isLoading && (
            <div className="welcome-section">
              <div className="welcome-avatar">
                <RobotOutlined />
              </div>
              <div className="welcome-content">
                <h3>你好！我是你的专属心理健康助手</h3>
                <p>我是一个专业的AI心理咨询助手，可以为你提供情绪支持、学习指导和生活建议。无论你遇到什么困扰，都可以与我分享。</p>
                <div className="welcome-features">
                  <div className="feature-item">
                    <HeartOutlined />
                    <span>情绪疏导</span>
                  </div>
                  <div className="feature-item">
                    <BookOutlined />
                    <span>学习指导</span>
                  </div>
                  <div className="feature-item">
                    <UserOutlined />
                    <span>人际关系</span>
                  </div>
                  <div className="feature-item">
                    <StarOutlined />
                    <span>个人成长</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 消息列表 */}
          {messages.map((message) => (
            <div
              key={message.id}
              className={`message ${message.sender === 'user' ? 'user-message' : 'ai-message'} ${message.isError ? 'error-message' : ''}`}
            >
              {message.sender === 'ai' && (
                <div className="message-avatar">
                  <RobotOutlined />
                </div>
              )}
              <div className="message-content">
                <div className="message-text">
                  {message.sender === 'ai' ? (
                    <MarkdownRenderer content={message.text} />
                  ) : (
                    message.text
                  )}
                </div>
                <div className="message-time">{formatTime(message.timestamp)}</div>
              </div>
              {message.sender === 'user' && (
                <div className="message-avatar user-avatar">
                  <UserOutlined />
                </div>
              )}
            </div>
          ))}
          
          {/* 流式消息显示 */}
          {isStreaming && currentStreamingMessage && (
            <div key="streaming-message" className="message ai-message streaming-message">
              <div className="message-avatar">
                <RobotOutlined />
              </div>
              <div className="message-content">
                <div className="message-text">
                  <MarkdownRenderer content={currentStreamingMessage} />
                  <span className="cursor">|</span>
                </div>
              </div>
            </div>
          )}
          
          {/* 加载指示器 */}
          {isLoading && !isStreaming && (
            <div key="loading-indicator" className="message ai-message">
              <div className="message-avatar">
                <RobotOutlined />
              </div>
              <div className="message-content">
                <div className="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
          
          <div ref={messagesEndRef} />
        </div>

        {/* 快捷回复面板 */}
        {showQuickReplies && messages.length === 0 && connectionStatus === 'connected' && (
          <div className="quick-replies-panel">
            <div className="quick-replies-header">
              <h4>选择你想聊的话题</h4>
              <p>点击下方话题开始对话，或直接在输入框输入你的问题</p>
            </div>
            <div className="quick-replies-categories">
              {quickRepliesCategories.map((category, categoryIndex) => (
                <div key={categoryIndex} className="quick-reply-category">
                  <div className="category-header" style={{ color: category.color }}>
                    {category.icon}
                    <span>{category.title}</span>
                  </div>
                  <div className="category-replies">
                    {category.replies.map((reply, replyIndex) => (
                      <button
                        key={replyIndex}
                        className="quick-reply-button"
                        onClick={() => handleQuickReply(reply.text)}
                        style={{ borderColor: category.color }}
                      >
                        {reply.icon}
                        <span>{reply.text}</span>
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* 输入区域 */}
      <div className="input-container">
        <div className="input-wrapper">
          <textarea
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={connectionStatus === 'connected' ? "输入你想说的话..." : "等待连接..."}
            disabled={connectionStatus !== 'connected' || isLoading}
            rows="1"
          />
          <button
            onClick={() => handleSendMessage()}
            disabled={!inputText.trim() || connectionStatus !== 'connected' || isLoading}
            className={`send-button ${connectionStatus !== 'connected' ? 'disabled' : ''}`}
          >
            <SendOutlined />
          </button>
        </div>
        
        {connectionStatus === 'disconnected' && (
          <div className="connection-status">
            <span>连接已断开，正在尝试重连...</span>
            <button onClick={connectWebSocket} className="reconnect-button">
              <ReloadOutlined />
              <span>手动重连</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AIChat;