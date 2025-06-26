/**
 * 消息处理工具函数
 * 包含消息ID生成、格式化等功能
 */

// 消息ID计数器，用于生成唯一ID
let messageIdCounter = 0;

/**
 * 生成唯一的消息ID
 * @returns {string} 格式：msg_时间戳_计数器
 */
export const generateUniqueId = () => {
  return `msg_${Date.now()}_${++messageIdCounter}`;
};

/**
 * 格式化时间戳为显示格式
 * @param {Date} timestamp - 时间戳对象
 * @returns {string} 格式化后的时间字符串 (HH:MM)
 */
export const formatTime = (timestamp) => {
  return timestamp.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
};

/**
 * 创建用户消息对象
 * @param {string} content - 消息内容
 * @returns {Object} 消息对象
 */
export const createUserMessage = (content) => ({
  id: generateUniqueId(),
  text: content,
  sender: 'user',
  timestamp: new Date()
});

/**
 * 创建AI消息对象
 * @param {string} content - 消息内容
 * @param {number} timestamp - 时间戳（可选）
 * @param {boolean} isError - 是否为错误消息（可选）
 * @returns {Object} 消息对象
 */
export const createAIMessage = (content, timestamp = Date.now(), isError = false) => ({
  id: generateUniqueId(),
  text: content,
  sender: 'ai',
  timestamp: new Date(timestamp),
  isError
});

/**
 * 创建WebSocket发送消息格式
 * @param {string} content - 消息内容
 * @returns {Object} WebSocket消息对象
 */
export const createWebSocketMessage = (content) => ({
  type: 'user',
  content,
  timestamp: Date.now()
}); 