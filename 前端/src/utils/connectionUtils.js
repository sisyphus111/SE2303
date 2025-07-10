/**
 * 连接状态工具函数
 * 处理WebSocket连接状态显示、颜色等UI相关逻辑
 */

/**
 * 获取连接状态的显示文本
 * @param {string} status - 连接状态 ('connecting' | 'connected' | 'disconnected')
 * @returns {string} 显示给用户的状态文本
 */
export const getConnectionStatusText = (status) => {
  switch (status) {
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

/**
 * 获取连接状态对应的颜色
 * @param {string} status - 连接状态 ('connecting' | 'connected' | 'disconnected')
 * @returns {string} CSS颜色值
 */
export const getConnectionStatusColor = (status) => {
  switch (status) {
    case 'connected':
      return '#4CAF50';    // 绿色 - 连接正常
    case 'connecting':
      return '#FF9800';    // 橙色 - 正在连接
    case 'disconnected':
      return '#F44336';    // 红色 - 连接断开
    default:
      return '#9E9E9E';    // 灰色 - 未知状态
  }
};

/**
 * 检查是否可以发送消息
 * @param {string} connectionStatus - 连接状态
 * @param {boolean} isLoading - 是否正在加载
 * @param {string} inputText - 输入文本
 * @returns {boolean} 是否可以发送消息
 */
export const canSendMessage = (connectionStatus, isLoading, inputText) => {
  return connectionStatus === 'connected' && 
         !isLoading && 
         inputText.trim().length > 0;
};

/**
 * 获取输入框的占位符文本
 * @param {string} connectionStatus - 连接状态
 * @returns {string} 占位符文本
 */
export const getInputPlaceholder = (connectionStatus) => {
  switch (connectionStatus) {
    case 'connected':
      return '输入你的消息...';
    case 'connecting':
      return '连接中，请稍候...';
    case 'disconnected':
      return '连接断开，请等待重连...';
    default:
      return '等待连接...';
  }
};

/**
 * 检查是否应该禁用输入框
 * @param {string} connectionStatus - 连接状态
 * @param {boolean} isLoading - 是否正在加载
 * @returns {boolean} 是否禁用输入框
 */
export const shouldDisableInput = (connectionStatus, isLoading) => {
  return connectionStatus !== 'connected' || isLoading;
}; 