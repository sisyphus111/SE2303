// AI聊天相关API服务
import { request } from './apiUtils';

/**
 * 发送消息给AI
 * @param {Object} messageData - 消息数据
 * @param {string} messageData.text - 消息内容（根据页面使用text字段）
 * @param {string} messageData.sessionId - 会话ID（可选）
 * @returns {Promise<Object>} AI回复
 */
export const sendMessageToAI = async (messageData) => {
  return request('/ai/chat', {
    method: 'POST',
    body: JSON.stringify(messageData),
  });
};

/**
 * 获取聊天历史
 * @param {string} sessionId - 会话ID
 * @param {number} page - 页码
 * @param {number} size - 每页大小
 * @returns {Promise<Object>} 聊天历史
 */
export const getChatHistory = async (sessionId, page = 1, size = 20) => {
  return request(`/ai/chat/history/${sessionId}?page=${page}&size=${size}`);
};

/**
 * 创建新的聊天会话
 * @returns {Promise<Object>} 会话信息
 */
export const createChatSession = async () => {
  return request('/ai/chat/session', {
    method: 'POST',
  });
};

/**
 * 生成AI回复（模拟函数，用于前端测试）
 * @param {string} userMessage - 用户消息
 * @returns {string} AI回复内容
 */
export const generateAIResponse = (userMessage) => {
  const responses = [
    "我理解你的感受，能具体说说吗？",
    "这确实是一个值得思考的问题。",
    "听起来很有趣，请继续分享。",
    "我在这里陪伴着你，随时可以聊天。",
    "你的想法很有见地，我很想听听更多。",
    "生活中总会有起起落落，重要的是保持积极的心态。",
    "每个人都有自己的节奏，不用着急。",
    "你的感受是正常的，我完全理解。",
    "让我们一起面对这个挑战吧。",
    "你的坚持让我很感动。"
  ];
  
  // 根据用户消息内容选择更合适的回复
  if (userMessage.includes('难过') || userMessage.includes('伤心') || userMessage.includes('不开心')) {
    return "我感受到你现在的心情不太好。记住，每个人都会有情绪低落的时候，这是完全正常的。你想聊聊具体发生了什么吗？我在这里倾听。";
  } else if (userMessage.includes('开心') || userMessage.includes('高兴') || userMessage.includes('快乐')) {
    return "太好了！看到你心情不错我也很开心。能分享是什么让你这么开心吗？";
  } else if (userMessage.includes('压力') || userMessage.includes('焦虑') || userMessage.includes('紧张')) {
    return "压力确实会让人感到困扰。试着深呼吸，一步一步来。你愿意和我分享是什么让你感到压力吗？";
  } else if (userMessage.includes('谢谢') || userMessage.includes('感谢')) {
    return "不用谢！能陪伴你是我的荣幸。希望我们的对话能给你带来一些温暖和力量。";
  } else {
    return responses[Math.floor(Math.random() * responses.length)];
  }
}; 