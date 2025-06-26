/**
 * 聊天消息显示组件
 * 负责渲染单条聊天消息，包括用户消息、AI消息、流式消息等
 */

import React from 'react';
import { formatTime } from '../utils/messageUtils';

/**
 * 单条消息组件
 * @param {Object} props - 组件属性
 * @param {Object} props.message - 消息对象
 * @param {string} props.message.id - 消息唯一ID
 * @param {string} props.message.text - 消息文本内容
 * @param {string} props.message.sender - 发送者类型 ('user' | 'ai')
 * @param {Date} props.message.timestamp - 消息时间戳
 * @param {boolean} props.message.isError - 是否为错误消息
 * @param {boolean} props.isStreaming - 是否为流式消息（可选）
 * @returns {JSX.Element} 消息组件
 */
const ChatMessage = ({ message, isStreaming = false }) => {
  // 解构消息属性
  const { text, sender, timestamp, isError } = message;
  
  // 构建CSS类名
  const messageClasses = [
    'message',                                    // 基础消息样式
    sender === 'user' ? 'user-message' : 'ai-message',  // 发送者类型样式
    isError ? 'error-message' : '',              // 错误消息样式
    isStreaming ? 'streaming-message' : ''       // 流式消息样式
  ].filter(Boolean).join(' ');  // 过滤空字符串并连接

  return (
    <div className={messageClasses}>
      <div className="message-content">
        {/* 消息文本内容 */}
        <div className="message-text">
          {text}
          {/* 流式消息的光标动画 */}
          {isStreaming && <span className="cursor">|</span>}
        </div>
        
        {/* 消息时间戳 */}
        <div className="message-time">
          {formatTime(timestamp)}
        </div>
      </div>
    </div>
  );
};

/**
 * 流式消息组件
 * 用于显示AI正在实时生成的消息内容
 * @param {Object} props - 组件属性
 * @param {string} props.content - 流式消息内容
 * @returns {JSX.Element} 流式消息组件
 */
export const StreamingMessage = ({ content }) => {
  // 如果没有内容，不渲染组件
  if (!content) return null;
  
  // 创建临时消息对象用于显示
  const streamingMessage = {
    id: 'streaming',
    text: content,
    sender: 'ai',
    timestamp: new Date(),
    isError: false
  };

  return <ChatMessage message={streamingMessage} isStreaming={true} />;
};

/**
 * 加载指示器组件
 * 显示AI正在思考时的动画效果
 * @returns {JSX.Element} 加载指示器组件
 */
export const LoadingIndicator = () => {
  return (
    <div className="message ai-message">
      <div className="message-content">
        {/* 打字动画指示器：三个跳跃的点 */}
        <div className="typing-indicator">
          <span></span>  {/* 第一个点 */}
          <span></span>  {/* 第二个点 */}
          <span></span>  {/* 第三个点 */}
        </div>
      </div>
    </div>
  );
};

/**
 * 消息列表组件
 * 渲染所有聊天消息，包括普通消息、流式消息和加载指示器
 * @param {Object} props - 组件属性
 * @param {Array} props.messages - 消息列表
 * @param {string} props.currentStreamingMessage - 当前流式消息内容
 * @param {boolean} props.isStreaming - 是否正在接收流式消息
 * @param {boolean} props.isLoading - 是否正在加载（等待AI回复）
 * @returns {JSX.Element} 消息列表组件
 */
export const MessageList = ({ 
  messages, 
  currentStreamingMessage, 
  isStreaming, 
  isLoading 
}) => {
  return (
    <>
      {/* 渲染所有已完成的消息 */}
      {messages.map((message) => (
        <ChatMessage 
          key={message.id} 
          message={message} 
        />
      ))}
      
      {/* 渲染流式消息（AI正在实时生成的内容） */}
      {isStreaming && currentStreamingMessage && (
        <StreamingMessage content={currentStreamingMessage} />
      )}
      
      {/* 渲染加载指示器（等待AI回复时显示） */}
      {isLoading && !isStreaming && (
        <LoadingIndicator />
      )}
    </>
  );
};

export default ChatMessage; 