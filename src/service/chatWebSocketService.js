/**
 * AI聊天WebSocket服务
 * 负责WebSocket连接管理、消息发送接收、自动重连等功能
 */

import { WEBSOCKET_URL } from './common';

class ChatWebSocketService {
  constructor() {
    // WebSocket连接实例
    this.ws = null;
    
    // 连接配置
    this.config = {
      url: `${WEBSOCKET_URL}/ws/chat`,           // 通过网关连接WebSocket服务器地址（内网穿透）
      maxReconnectAttempts: 5,                   // 最大重连次数
      baseReconnectDelay: 1000,                  // 基础重连延迟(毫秒)
      maxReconnectDelay: 30000                   // 最大重连延迟(毫秒)
    };
    
    // 连接状态
    this.connectionStatus = 'disconnected';      // 'connecting', 'connected', 'disconnected'
    this.reconnectAttempts = 0;                  // 当前重连次数
    this.reconnectTimeout = null;                // 重连定时器
    
    // 事件回调函数
    this.callbacks = {
      onStatusChange: null,                      // 连接状态变化回调
      onMessage: null,                           // 消息接收回调
      onError: null                              // 错误处理回调
    };
  }

  /**
   * 设置事件回调函数
   * @param {string} event - 事件名称 ('statusChange', 'message', 'error')
   * @param {Function} callback - 回调函数
   */
  on(event, callback) {
    switch (event) {
      case 'statusChange':
        this.callbacks.onStatusChange = callback;
        break;
      case 'message':
        this.callbacks.onMessage = callback;
        break;
      case 'error':
        this.callbacks.onError = callback;
        break;
      default:
        console.warn(`未知事件类型: ${event}`);
    }
  }

  /**
   * 更新连接状态并触发回调
   * @param {string} status - 新的连接状态
   */
  updateStatus(status) {
    if (this.connectionStatus !== status) {
      this.connectionStatus = status;
      if (this.callbacks.onStatusChange) {
        this.callbacks.onStatusChange(status);
      }
      console.log(`WebSocket连接状态变更: ${status}`);
    }
  }

  /**
   * 建立WebSocket连接
   * @returns {Promise<void>}
   */
  connect() {
    return new Promise((resolve, reject) => {
      // 如果已经连接或正在连接，直接返回
      if (this.ws?.readyState === WebSocket.OPEN) {
        resolve();
        return;
      }

      try {
        this.updateStatus('connecting');
        console.log(`尝试连接WebSocket: ${this.config.url}`);
        
        // 创建WebSocket连接
        this.ws = new WebSocket(this.config.url);

        // 连接成功事件
        this.ws.onopen = () => {
          console.log('WebSocket连接建立成功');
          
          // 检查是否是重连成功
          const wasReconnecting = this.reconnectAttempts > 0;
          
          this.updateStatus('connected');
          this.reconnectAttempts = 0;  // 重置重连次数
          
          // 如果是重连成功，显示恢复连接的消息
          if (wasReconnecting && this.callbacks.onMessage) {
            this.callbacks.onMessage({
              type: 'ai',
              content: '连接已恢复，我又回来啦！有什么可以帮助你的吗？',
              timestamp: Date.now()
            });
          }
          
          resolve();
        };

        // 消息接收事件
        this.ws.onmessage = (event) => {
          try {
            const message = JSON.parse(event.data);
            console.log('收到WebSocket消息:', message);
            
            // 触发消息回调
            if (this.callbacks.onMessage) {
              this.callbacks.onMessage(message);
            }
          } catch (error) {
            console.error('解析WebSocket消息失败:', error, event.data);
            if (this.callbacks.onError) {
              this.callbacks.onError(new Error('消息解析失败'));
            }
          }
        };

        // 连接关闭事件
        this.ws.onclose = (event) => {
          console.log(`WebSocket连接关闭: 代码=${event.code}, 原因=${event.reason}`);
          this.updateStatus('disconnected');
          
          // 尝试自动重连
          this.attemptReconnect();
        };

        // 连接错误事件
        this.ws.onerror = (error) => {
          console.error('WebSocket连接错误:', error);
          this.updateStatus('disconnected');
          
          // 只有在非初次连接时才触发错误回调
          // 初次连接失败通过Promise的reject处理，不显示在聊天界面
          if (this.reconnectAttempts > 0 && this.callbacks.onError) {
            this.callbacks.onError(new Error('WebSocket连接错误'));
          }
          
          reject(error);
        };

      } catch (error) {
        console.error('创建WebSocket连接失败:', error);
        this.updateStatus('disconnected');
        
        if (this.callbacks.onError) {
          this.callbacks.onError(error);
        }
        
        reject(error);
      }
    });
  }

  /**
   * 尝试自动重连
   */
  attemptReconnect() {
    // 检查是否已达到最大重连次数
    if (this.reconnectAttempts >= this.config.maxReconnectAttempts) {
      console.log('已达到最大重连次数，停止重连');
      if (this.callbacks.onError) {
        this.callbacks.onError(new Error('连接失败，已达到最大重连次数'));
      }
      return;
    }

    // 计算重连延迟时间（指数退避算法）
    this.reconnectAttempts++;
    const delay = Math.min(
      this.config.baseReconnectDelay * Math.pow(2, this.reconnectAttempts - 1),
      this.config.maxReconnectDelay
    );

    console.log(`${delay / 1000}秒后进行第${this.reconnectAttempts}次重连`);

    // 设置重连定时器
    this.reconnectTimeout = setTimeout(() => {
      this.connect().catch((error) => {
        console.error('自动重连失败:', error);
        // 重连失败时不显示错误消息，让重连机制继续尝试
      });
    }, delay);
  }

  /**
   * 手动重连
   * @returns {Promise<void>}
   */
  reconnect() {
    console.log('执行手动重连');
    
    // 清除自动重连定时器
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
    
    // 重置重连次数
    this.reconnectAttempts = 0;
    
    // 关闭现有连接
    this.disconnect();
    
    // 重新连接
    return this.connect();
  }

  /**
   * 发送消息
   * @param {Object} message - 要发送的消息对象
   * @returns {boolean} 发送是否成功
   */
  sendMessage(message) {
    if (!this.isConnected()) {
      console.error('WebSocket未连接，无法发送消息');
      if (this.callbacks.onError) {
        this.callbacks.onError(new Error('消息发送失败：连接已断开，请等待重连后再试'));
      }
      return false;
    }

    try {
      const messageStr = JSON.stringify(message);
      this.ws.send(messageStr);
      console.log('发送WebSocket消息:', message);
      return true;
    } catch (error) {
      console.error('发送WebSocket消息失败:', error);
      if (this.callbacks.onError) {
        this.callbacks.onError(new Error('消息发送失败：网络异常，请重试'));
      }
      return false;
    }
  }

  /**
   * 检查是否已连接
   * @returns {boolean}
   */
  isConnected() {
    return this.ws?.readyState === WebSocket.OPEN;
  }

  /**
   * 获取当前连接状态
   * @returns {string}
   */
  getStatus() {
    return this.connectionStatus;
  }

  /**
   * 断开连接
   */
  disconnect() {
    console.log('主动断开WebSocket连接');
    
    // 清除重连定时器
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
    
    // 关闭WebSocket连接
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    
    this.updateStatus('disconnected');
  }

  /**
   * 清理资源
   */
  destroy() {
    console.log('销毁WebSocket服务');
    this.disconnect();
    
    // 清除所有回调
    this.callbacks = {
      onStatusChange: null,
      onMessage: null,
      onError: null
    };
  }
}

// 创建单例实例
const chatWebSocketService = new ChatWebSocketService();

export default chatWebSocketService; 