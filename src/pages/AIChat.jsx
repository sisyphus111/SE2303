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
  const streamingEndProcessed = useRef(false); // 标志：是否已处理流式消息结束

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, currentStreamingMessage]);

  // WebSocket连接函数
  const connectWebSocket = useCallback(() => {
    console.log('=== 尝试连接WebSocket ===');
    if (webSocketRef.current?.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接，跳过重复连接');
      return;
    }

    // 先关闭现有连接（如果存在）
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
  const handleWebSocketMessage = useCallback((message) => {
    const { type, content, timestamp } = message;
    console.log('=== 处理WebSocket消息 ===', { type, content: content?.substring(0, 50), timestamp });
    
    switch (type) {
      case 'ai':
        // 完整的AI消息（比如欢迎消息）
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
        // 流式消息片段
        console.log('处理AI流式片段:', content, '当前流式状态:', isStreaming);
        setIsStreaming(true);
        setCurrentStreamingMessage(prev => {
          const newMessage = prev + content;
          console.log('更新流式消息，长度从', prev.length, '到', newMessage.length);
          return newMessage;
        });
        streamingEndProcessed.current = false; // 重置结束处理标志
        break;
        
      case 'ai_end':
        // 流式消息结束：将流式内容转为完整消息
        console.log('收到AI_END消息，防重复处理标志:', streamingEndProcessed.current);
        
        // 防止重复处理结束消息
        if (streamingEndProcessed.current) {
          console.log('ai_end已处理过，跳过重复处理');
          break;
        }
        streamingEndProcessed.current = true;
        
        setIsStreaming(false);
        setIsLoading(false);
        
        // 使用函数式更新确保获取到最新的流式消息内容并同步清空
        setCurrentStreamingMessage(prevMessage => {
          console.log('处理ai_end，流式消息内容长度:', prevMessage.length);
          if (prevMessage.trim()) {
            console.log('将流式消息添加到列表:', prevMessage.substring(0, 50) + '...');
            setMessages(prev => {
              console.log('ai_end添加消息前，当前消息数:', prev.length);
              
              // 检查是否已经存在相同内容的AI消息（防止重复添加）
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
          return ''; // 立即清空流式消息
        });
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
  }, []);

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
      streamingEndProcessed.current = false; // 重置结束处理标志
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