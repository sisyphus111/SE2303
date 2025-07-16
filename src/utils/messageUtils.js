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
 * 安全地解析时间字符串或对象为Date对象
 * @param {string|Date|Object} timeValue - 时间值
 * @returns {Date} Date对象
 */
export const parseDateTime = (timeValue) => {
  if (!timeValue) {
    return new Date();
  }
  
  if (timeValue instanceof Date) {
    return timeValue;
  }
  
  if (typeof timeValue === 'string') {
    // 处理ISO字符串格式
    const date = new Date(timeValue);
    if (!isNaN(date.getTime())) {
      return date;
    }
  }
  
  if (typeof timeValue === 'object' && Array.isArray(timeValue)) {
    // 处理Jackson数组格式 [year, month-1, day, hour, minute, second, nano]
    const [year, month, day, hour = 0, minute = 0, second = 0] = timeValue;
    return new Date(year, month - 1, day, hour, minute, second);
  }
  
  // 兜底：返回当前时间
  console.warn('无法解析时间格式:', timeValue);
  return new Date();
};

/**
 * 格式化时间戳为显示格式（聊天室专用）
 * @param {string|Date|Object} timestamp - 时间戳对象
 * @returns {string} 格式化后的时间字符串 (HH:MM)
 */
export const formatTime = (timestamp) => {
  const date = parseDateTime(timestamp);
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
};

/**
 * 格式化时间戳为聊天室消息时间格式
 * @param {string|Date|Object} timestamp - 时间戳
 * @returns {string} 格式化后的时间字符串
 */
export const formatChatTime = (timestamp) => {
  const date = parseDateTime(timestamp);
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  
  if (messageDate.getTime() === today.getTime()) {
    // 今天的消息只显示时间
    return date.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  } else if (messageDate.getTime() === today.getTime() - 24 * 60 * 60 * 1000) {
    // 昨天的消息
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  } else {
    // 更早的消息显示日期和时间
    return date.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
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