import React, { useState, useRef, useEffect, useCallback } from 'react';
import '../css/AIChat.css';

// 生成唯一ID的函数
let messageIdCounter = 0;
const generateUniqueId = () => {
  return `msg_${Date.now()}_${++messageIdCounter}`;
};

const AIChat = () => {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [connectionStatus, setConnectionStatus] = useState('connecting'); // 'connecting', 'connected', 'disconnected'
  const [currentStreamingMessage, setCurrentStreamingMessage] = useState('');
  const [isStreaming, setIsStreaming] = useState(false);
  
  const messagesEndRef = useRef(null);
  const webSocketRef = useRef(null);
  const reconnectTimeoutRef = useRef(null);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 5;

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, currentStreamingMessage]);

  // WebSocket连接函数
  const connectWebSocket = useCallback(() => {
    if (webSocketRef.current?.readyState === WebSocket.OPEN) {
      return;
    }

    try {
      setConnectionStatus('connecting');
      const ws = new WebSocket('ws://localhost:8081/ws/chat');
      
      ws.onopen = () => {
        console.log('WebSocket连接成功');
        setConnectionStatus('connected');
        reconnectAttempts.current = 0;
      };

      ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data);
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
        
        // 自动重连
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
  const handleWebSocketMessage = (message) => {
    const { type, content, timestamp } = message;
    
    switch (type) {
      case 'ai':
        // 完整的AI消息（比如欢迎消息）
        setMessages(prev => [...prev, {
          id: generateUniqueId(),
          text: content,
          sender: 'ai',
          timestamp: new Date(timestamp)
        }]);
        break;
        
      case 'ai_chunk':
        // 流式消息片段
        setIsStreaming(true);
        setCurrentStreamingMessage(prev => prev + content);
        break;
        
      case 'ai_end':
        // 流式消息结束
        setIsStreaming(false);
        setCurrentStreamingMessage(prevMessage => {
          if (prevMessage) {
            setMessages(prev => [...prev, {
              id: generateUniqueId(),
              text: prevMessage,
              sender: 'ai',
              timestamp: new Date()
            }]);
          }
          return '';
        });
        setIsLoading(false);
        break;
        
      case 'error':
        // 错误消息
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
  };

  // 发送消息
  const handleSendMessage = async () => {
    if (!inputText.trim() || connectionStatus !== 'connected' || isLoading) {
      return;
    }

    const userMessage = {
      id: generateUniqueId(),
      text: inputText,
      sender: 'user',
      timestamp: new Date()
    };

    // 添加用户消息到界面
    setMessages(prev => [...prev, userMessage]);
    
    // 发送到WebSocket
    try {
      const messageToSend = {
        type: 'user',
        content: inputText,
        timestamp: Date.now()
      };
      
      webSocketRef.current.send(JSON.stringify(messageToSend));
      setInputText('');
      setIsLoading(true);
      setCurrentStreamingMessage('');
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

  const formatTime = (timestamp) => {
    return timestamp.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  // 连接状态显示
  const getConnectionStatusText = () => {
    switch (connectionStatus) {
      case 'connecting':
        return '连接中...';
      case 'connected':
        return '在线 • 随时陪伴';
      case 'disconnected':
        return '连接断开 • 尝试重连中...';
      default:
        return '未知状态';
    }
  };

  const getConnectionStatusColor = () => {
    switch (connectionStatus) {
      case 'connected':
        return '#4CAF50';
      case 'connecting':
        return '#FF9800';
      case 'disconnected':
        return '#F44336';
      default:
        return '#9E9E9E';
    }
  };

  // 组件挂载时连接WebSocket
  useEffect(() => {
    connectWebSocket();
    
    // 组件卸载时清理连接
    return () => {
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      if (webSocketRef.current) {
        webSocketRef.current.close();
      }
    };
  }, [connectWebSocket]);

  return (
    <div className="ai-chat-container">
      <div className="chat-header">
        <div className="ai-avatar">
          <span>🤖</span>
        </div>
        <div className="header-info">
          <h2>AI陪伴助手</h2>
          <p style={{ color: getConnectionStatusColor() }}>
            {getConnectionStatusText()}
          </p>
        </div>
      </div>

      <div className="messages-container">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`message ${message.sender === 'user' ? 'user-message' : 'ai-message'} ${message.isError ? 'error-message' : ''}`}
          >
            <div className="message-content">
              <div className="message-text">{message.text}</div>
              <div className="message-time">{formatTime(message.timestamp)}</div>
            </div>
          </div>
        ))}
        
        {/* 流式消息显示 */}
        {isStreaming && currentStreamingMessage && (
          <div key="streaming-message" className="message ai-message streaming-message">
            <div className="message-content">
              <div className="message-text">
                {currentStreamingMessage}
                <span className="cursor">|</span>
              </div>
            </div>
          </div>
        )}
        
        {/* 加载指示器 */}
        {isLoading && !isStreaming && (
          <div key="loading-indicator" className="message ai-message">
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

      <div className="input-container">
        <div className="input-wrapper">
          <textarea
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={connectionStatus === 'connected' ? "输入你的消息..." : "等待连接..."}
            disabled={connectionStatus !== 'connected' || isLoading}
            rows="1"
          />
          <button
            onClick={handleSendMessage}
            disabled={!inputText.trim() || connectionStatus !== 'connected' || isLoading}
            className={`send-button ${connectionStatus !== 'connected' ? 'disabled' : ''}`}
          >
            <span>发送</span>
          </button>
        </div>
        
        {connectionStatus === 'disconnected' && (
          <div className="connection-status">
            <span>连接已断开，正在尝试重连...</span>
            <button onClick={connectWebSocket} className="reconnect-button">
              手动重连
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AIChat;