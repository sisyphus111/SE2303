/**
 * 聊天服务 - 处理群聊的HTTP请求
 * 负责获取历史消息和发送新消息
 */

import { get, post } from './common.js';

/**
 * 聊天服务类
 */
class ChatService {
  constructor() {
    // API路径前缀
    this.API_PREFIX = '/talk';
  }

  /**
   * 分页获取历史消息记录
   * @param {Object} options - 查询选项
   * @param {number} options.page - 页码，从0开始
   * @param {number} options.size - 每页大小，默认20
   * @param {number} options.beforeMessageId - 获取此消息ID之前的消息（用于滚动加载更多）
   * @returns {Promise<Object>} 返回消息列表和分页信息
   */
  async getHistoryMessages(options = {}) {
    const {
      page = 0,
      size = 10,
      beforeMessageId = null
    } = options;

    const params = {
      page,
      size
    };

    // 如果指定了beforeMessageId，则获取此消息之前的消息
    if (beforeMessageId) {
      params.beforeMessageId = beforeMessageId;
    }

    try {
      console.log('🔍 获取历史消息:', params);
      
      const response = await get(`${this.API_PREFIX}/history`, { params });
      
      // 转换后端PastMessage格式为前端需要的格式
      const messages = response.data.map(this.transformPastMessage);
      
      // 更精确的hasMore判断
      const hasMore = messages.length === size && response.hasMore !== false;
      
      console.log('✅ 历史消息获取成功:', {
        count: messages.length,
        page: params.page,
        size: params.size,
        hasMore,
        backendHasMore: response.hasMore
      });
      
      return {
        success: true,
        messages,
        hasMore,
        currentPage: page
      };
      
    } catch (error) {
      console.error('❌ 获取历史消息异常:', error);
      return {
        success: false,
        message: '网络错误，请稍后重试',
        messages: [],
        hasMore: false
      };
    }
  }

  /**
   * 发送消息
   * @param {string} content - 消息内容
   * @param {string} type - 消息类型，默认为'text'
   * @returns {Promise<Object>} 发送结果
   */
  async sendMessage(content, type = 'text') {
    if (!content || !content.trim()) {
      return {
        success: false,
        message: '消息内容不能为空'
      };
    }

    const messageData = {
      type: type,
      content: content.trim()
    };

    try {
      console.log('📤 发送消息:', messageData);
      
      const response = await post(`${this.API_PREFIX}/send`, messageData);
      
      if (response.success) {
        console.log('✅ 消息发送成功');
        return {
          success: true,
          message: '消息发送成功',
          data: response.data
        };
      } else {
        console.error('❌ 消息发送失败:', response.message);
        return {
          success: false,
          message: response.message || '消息发送失败'
        };
      }
    } catch (error) {
      console.error('❌ 消息发送异常:', error);
      return {
        success: false,
        message: '网络错误，请稍后重试'
      };
    }
  }

  /**
   * 转换后端PastMessage格式为前端消息格式
   * @param {Object} pastMessage - 后端PastMessage对象
   * @returns {Object} 前端消息对象
   */
  transformPastMessage(pastMessage) {
    return {
      id: pastMessage.id.toString(),
      userId: pastMessage.userId.toString(),
      username: pastMessage.username || `用户${pastMessage.userId}`,
      nickname: pastMessage.nickname || pastMessage.username || `用户${pastMessage.userId}`,
      avatar: pastMessage.avatar,
      content: pastMessage.content,
      createdAt: pastMessage.createdAt,
      messageType: 'history' // 标记为历史消息
    };
  }

  /**
   * 转换前端消息为发送格式
   * @param {string} content - 消息内容
   * @param {string} type - 消息类型
   * @returns {Object} 发送消息对象
   */
  createSentMessage(content, type = 'text') {
    return {
      type: type,
      content: content
    };
  }

  /**
   * 获取更多历史消息（向上滚动加载）
   * @param {Array} currentMessages - 当前已有的消息列表
   * @param {number} size - 每次加载的消息数量
   * @returns {Promise<Object>} 更多历史消息
   */
  async loadMoreMessages(currentMessages = [], size = 20) {
    // 找到最早的消息ID作为beforeMessageId
    let beforeMessageId = null;
    if (currentMessages.length > 0) {
      // 找到最早的历史消息（排除当前会话中新发送的消息）
      const historyMessages = currentMessages.filter(msg => msg.messageType === 'history');
      if (historyMessages.length > 0) {
        // 按时间排序，取最早的消息
        const sortedMessages = historyMessages.sort((a, b) => a.timestamp - b.timestamp);
        beforeMessageId = parseInt(sortedMessages[0].id);
      }
    }

    return await this.getHistoryMessages({
      page: 0,
      size,
      beforeMessageId
    });
  }

  /**
   * 初始化聊天室 - 获取最新的历史消息
   * @param {number} size - 初始加载的消息数量，默认10条
   * @returns {Promise<Object>} 初始消息列表
   */
  async initializeChatRoom(size = 10) {
    console.log('🚀 初始化聊天室...');
    return await this.getHistoryMessages({
      page: 0,
      size
    });
  }

  /**
   * 格式化消息时间显示
   * @param {Date} timestamp - 时间戳
   * @returns {string} 格式化的时间字符串
   */
  formatMessageTime(timestamp) {
    const now = new Date();
    const messageTime = new Date(timestamp);
    const diffMs = now - messageTime;
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
      // 今天：显示时间
      return messageTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    } else if (diffDays === 1) {
      // 昨天
      return `昨天 ${messageTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })}`;
    } else if (diffDays < 7) {
      // 一周内：显示星期
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return `${weekdays[messageTime.getDay()]} ${messageTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })}`;
    } else {
      // 超过一周：显示日期和时间
      return messageTime.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
}

// 创建并导出单例实例
const chatService = new ChatService();

export default chatService;

// 同时导出类，便于测试
export { ChatService };
