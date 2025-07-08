/**
 * 聊天状态管理Hook
 * 管理消息列表、输入状态、流式消息等聊天相关状态
 */

import { useState, useEffect, useCallback, useRef } from 'react';
import chatWebSocketService from '../service/chatWebSocketService';
import { createUserMessage, createAIMessage, createWebSocketMessage } from '../utils/messageUtils';

/**
 * 聊天状态管理Hook
 * @returns {Object} 聊天状态和操作方法
 */
export const useChatState = () => {
  // ==================== 状态定义 ====================
  
  // 消息列表：存储所有聊天消息
  const [messages, setMessages] = useState([]);
  
  // 输入框文本内容
  const [inputText, setInputText] = useState('');
  
  // 加载状态：用户发送消息后等待AI回复时为true
  const [isLoading, setIsLoading] = useState(false);
  
  // WebSocket连接状态：'connecting' | 'connected' | 'disconnected'
  const [connectionStatus, setConnectionStatus] = useState('disconnected');
  
  // 当前流式消息内容：AI正在逐字回复时的临时内容
  const [currentStreamingMessage, setCurrentStreamingMessage] = useState('');
  
  // 流式消息状态：AI是否正在进行流式回复
  const [isStreaming, setIsStreaming] = useState(false);
  
  // 消息发送超时定时器引用
  const messageTimeoutRef = useRef(null);

  // ==================== 消息处理函数 ====================

  /**
   * 处理接收到的WebSocket消息
   * @param {Object} message - WebSocket消息对象
   */
  const handleWebSocketMessage = useCallback((message) => {
    const { type, content, timestamp } = message;
    
    console.log(`处理WebSocket消息: type=${type}, content=${content}`);
    
    switch (type) {
      case 'ai':
        // 完整的AI消息（如欢迎消息）
        console.log('收到完整AI消息');
        setMessages(prev => [...prev, createAIMessage(content, timestamp)]);
        break;
        
      case 'ai_chunk':
        // 流式消息片段：AI正在逐字生成回复
        console.log('收到AI流式消息片段');
        setIsStreaming(true);
        setCurrentStreamingMessage(prev => prev + content);
        
        // 清除之前的超时定时器
        if (messageTimeoutRef.current) {
          clearTimeout(messageTimeoutRef.current);
        }
        
        // 设置新的超时定时器：如果30秒内没有收到新的chunk或end消息，自动结束流式响应
        messageTimeoutRef.current = setTimeout(() => {
          console.warn('流式消息超时，自动结束');
          setIsStreaming(false);
          setCurrentStreamingMessage(prevMessage => {
            if (prevMessage.trim()) {
              setMessages(prev => [...prev, createAIMessage(prevMessage + ' [响应超时]')]);
            }
            return '';
          });
          setIsLoading(false);
        }, 30000); // 30秒超时
        break;
        
      case 'ai_end':
        // 流式消息结束：将流式内容转为完整消息
        console.log('AI流式消息结束');
        
        // 清除超时定时器
        if (messageTimeoutRef.current) {
          clearTimeout(messageTimeoutRef.current);
          messageTimeoutRef.current = null;
        }
        
        setIsStreaming(false);
        setCurrentStreamingMessage(prevMessage => {
          if (prevMessage.trim()) {
            // 将流式消息添加到消息列表
            setMessages(prev => [...prev, createAIMessage(prevMessage)]);
          }
          return ''; // 清空流式消息
        });
        setIsLoading(false);
        break;
        
      case 'error':
        // 错误消息：显示错误提示
        console.log('收到错误消息');
        
        // 清除超时定时器
        if (messageTimeoutRef.current) {
          clearTimeout(messageTimeoutRef.current);
          messageTimeoutRef.current = null;
        }
        
        setMessages(prev => [...prev, createAIMessage(content, Date.now(), true)]);
        setIsLoading(false);
        setIsStreaming(false);
        setCurrentStreamingMessage('');
        break;
        
      default:
        console.warn(`未知的消息类型: ${type}`);
    }
  }, []);

  /**
   * 处理WebSocket连接状态变化
   * @param {string} status - 新的连接状态
   */
  const handleStatusChange = useCallback((status) => {
    console.log(`连接状态变化: ${status}`);
    setConnectionStatus(status);
    
    // 连接断开时重置相关状态
    if (status === 'disconnected') {
      // 清除超时定时器
      if (messageTimeoutRef.current) {
        clearTimeout(messageTimeoutRef.current);
        messageTimeoutRef.current = null;
      }
      
      setIsLoading(false);
      setIsStreaming(false);
      setCurrentStreamingMessage('');
    }
  }, []);

  /**
   * 处理WebSocket错误
   * @param {Error} error - 错误对象
   */
  const handleWebSocketError = useCallback((error) => {
    console.error('WebSocket错误:', error);
    
    // 只有在特定情况下才显示错误消息给用户
    // 避免在正常的连接建立过程中显示错误
    if (error.message.includes('连接失败，已达到最大重连次数') || 
        error.message.includes('消息发送失败') ||
        error.message.includes('消息解析失败')) {
      
      // 添加错误消息到聊天列表
      setMessages(prev => [...prev, createAIMessage(
        `${error.message}`,
        Date.now(),
        true
      )]);
    }
    
    setIsLoading(false);
  }, []);

  // ==================== 初始化WebSocket连接 ====================
  
  useEffect(() => {
    console.log('初始化聊天状态管理');
    
    // 获取当前用户ID（从localStorage、context或其他地方）
    const getCurrentUserId = () => {
      // 这里需要根据你的用户认证系统来获取用户ID
      // 例如从localStorage、context、或API调用获取
      const userProfile = localStorage.getItem('userProfile');
      if (userProfile) {
        const user = JSON.parse(userProfile);
        return user.userId || user.id;
      }
      return null;
    };

    const userId = getCurrentUserId();
    console.log('当前用户ID:', userId);

    // 设置WebSocket事件监听器
    chatWebSocketService.on('statusChange', handleStatusChange);
    chatWebSocketService.on('message', handleWebSocketMessage);
    chatWebSocketService.on('error', handleWebSocketError);

    // 建立WebSocket连接，传递用户ID
    chatWebSocketService.connect(userId).catch(error => {
      console.error('WebSocket连接失败:', error);
      setConnectionStatus('disconnected');
    });

    // 清理函数
    return () => {
      chatWebSocketService.destroy();
    };
  }, []);

  // ==================== 操作方法 ====================

  /**
   * 发送消息到AI
   * @param {string} messageText - 要发送的消息内容（可选，默认使用inputText）
   */
  const sendMessage = useCallback((messageText = inputText) => {
    // 验证输入
    if (!messageText.trim()) {
      console.warn('消息内容为空，不发送');
      return;
    }
    
    if (connectionStatus !== 'connected') {
      console.warn('WebSocket未连接，无法发送消息');
      return;
    }
    
    if (isLoading) {
      console.warn('正在处理上一条消息，请稍候');
      return;
    }

    console.log(`发送消息: ${messageText}`);
    
    // 创建用户消息并添加到消息列表
    const userMessage = createUserMessage(messageText);
    setMessages(prev => [...prev, userMessage]);
    
    // 发送消息到WebSocket服务器
    const success = chatWebSocketService.sendMessage(createWebSocketMessage(messageText));
    
    if (success) {
      // 发送成功：清空输入框，设置加载状态
      setInputText('');
      setIsLoading(true);
      setCurrentStreamingMessage(''); // 清空之前的流式消息
    } else {
      // 发送失败：添加错误消息
      setMessages(prev => [...prev, createAIMessage(
        '消息发送失败，请检查网络连接后重试',
        Date.now(),
        true
      )]);
    }
  }, [inputText, connectionStatus, isLoading]);

  /**
   * 手动重连WebSocket
   */
  const reconnect = useCallback(() => {
    console.log('手动重连WebSocket');
    chatWebSocketService.reconnect();
  }, []);

  /**
   * 清空聊天记录
   */
  const clearMessages = useCallback(() => {
    console.log('清空聊天记录');
    setMessages([]);
    setCurrentStreamingMessage('');
    setIsStreaming(false);
  }, []);

  // ==================== 返回状态和方法 ====================
  
  return {
    // 聊天状态
    messages,                    // 消息列表
    inputText,                   // 输入框内容
    isLoading,                   // 是否正在等待AI回复
    connectionStatus,            // WebSocket连接状态
    currentStreamingMessage,     // 当前流式消息内容
    isStreaming,                 // 是否正在接收流式消息
    
    // 状态更新方法
    setInputText,               // 更新输入框内容
    
    // 操作方法
    sendMessage,                // 发送消息
    reconnect,                  // 手动重连
    clearMessages,              // 清空聊天记录
    
    // 辅助方法
    isConnected: connectionStatus === 'connected',  // 是否已连接
    canSendMessage: connectionStatus === 'connected' && !isLoading  // 是否可以发送消息
  };
}; 