import { useCallback, useEffect, useRef } from 'react';
import { get, post } from '../service/common'; // 引入common.js的get和post方法
import { PREFIX, WEBSOCKET_URL } from '../service/common'; // 引入PREFIX和WEBSOCKET_URL

// WebSocketService 作为一个类，管理WebSocket连接和相关逻辑
const WebSocketService = {
    ws: null, // WebSocket实例
    heartTimer: null, // 心跳定时器
    wsIp: WEBSOCKET_URL, // 通过网关连接WebSocket，使用内网穿透域名
    apiGateway: PREFIX, // 使用common.js的PREFIX
    
    // WebSocket连接配置
    config: {
        // 是否使用新的API端点（当后端实现新API时设置为true）
        useNewAPI: false,
        // 连接重试次数
        maxRetries: 3,
        // 重试延迟（毫秒）
        retryDelay: 2000
    },

    // 初始化WebSocket连接
    initialize: (userId, onMessage, onError, onConnect, onDisconnect, onNewMessage, onMessageUpdate) => {
        if (!userId) {
            onError('未提供userId，无法建立WebSocket连接');
            return;
        }

        if (WebSocketService.ws && WebSocketService.ws.readyState === WebSocket.OPEN) {
            console.log('WebSocket已连接，无需重复初始化');
            return;
        }

        // 根据配置选择连接地址
        const wsUrl = WebSocketService.config.useNewAPI 
            ? `${WebSocketService.wsIp}/websocket?x-userId=${userId}`  // 新的API端点
            : `${WebSocketService.wsIp}/ws/talk/conn?x-userId=${userId}`;  // 原来的端点

        console.log(`尝试连接WebSocket: ${wsUrl}`);
        WebSocketService.ws = new WebSocket(wsUrl);

        WebSocketService.ws.onopen = () => {
            console.log('WebSocket连接已建立');
            WebSocketService.startHeartBeat();
            if (onConnect) onConnect();
        };

        WebSocketService.ws.onmessage = async (event) => {
            try {
                const message = JSON.parse(event.data);
                console.log('收到WebSocket消息:', message);
                
                switch (message.type) {
                    case 'text':
                        // 原有的实时消息处理
                        console.log('📨 处理实时消息:', message);
                        if (onMessage) onMessage(message);
                        break;
                        
                    case 'new_message':
                        // 新消息通知 - 触发增量加载
                        console.log('🆕 收到新消息通知:', message);
                        if (onNewMessage) onNewMessage(message);
                        break;
                        
                    case 'message_update':
                        // 消息更新通知 - 更新本地消息
                        console.log('🔄 收到消息更新通知:', message);
                        if (onMessageUpdate) onMessageUpdate(message);
                        break;
                        
                    default:
                        console.warn('❓ 未知消息类型:', message.type, message);
                }
            } catch (err) {
                console.error('处理WebSocket消息失败:', err);
                if (onError) onError('接收消息失败');
            }
        };

        WebSocketService.ws.onclose = () => {
            console.log('WebSocket连接已关闭');
            clearInterval(WebSocketService.heartTimer);
            if (onDisconnect) onDisconnect();
            // 5秒后尝试重连
            setTimeout(() => WebSocketService.initialize(userId, onMessage, onError, onConnect, onDisconnect, onNewMessage, onMessageUpdate), 5000);
        };

        WebSocketService.ws.onerror = (err) => {
            console.error('WebSocket连接错误:', err);
            if (onError) onError('WebSocket连接错误');
        };
    },

    // 发送心跳包
    startHeartBeat: () => {
        WebSocketService.heartTimer = setInterval(() => {
            if (WebSocketService.ws && WebSocketService.ws.readyState === WebSocket.OPEN) {
                WebSocketService.ws.send('heart');
                console.log('发送心跳包');
            }
        }, 9900);
    },

    // 发送消息（通过HTTP，使用common.js的post方法）
    sendMessage: async (content, onError) => {
        if (!content.trim()) {
            onError('消息内容不能为空');
            return false;
        }

        const sentMessage = {
            type: 'text',
            content
        };

        try {
            const result = await post('/talk/messages', sentMessage);
            if (result.success) {
                return true;
            } else {
                onError(result.message || '发送消息失败');
                return false;
            }
        } catch (err) {
            console.error('发送消息失败:', err);
            onError('发送消息失败');
            return false;
        }
    },

    // 获取历史消息（通过HTTP，使用common.js的get方法）
    // 支持增量查询：after参数用于获取指定时间戳之后的新消息
    fetchPastMessages: async (page, size, options = {}, getUserCommunityInfoById, onError) => {
        try {
            const params = { page, size, ...options };
            
            // 根据配置和参数选择API端点
            let apiEndpoint;
            if (WebSocketService.config.useNewAPI && options.after) {
                // 使用新的增量API
                apiEndpoint = '/talk/messages/incremental';
            } else {
                // 使用原有的API
                apiEndpoint = '/talk/messages';
                // 如果使用原有API但有after参数，在控制台提醒但继续使用原API
                if (options.after) {
                    console.warn('⚠️ 检测到增量查询请求，但后端尚未启用新API，将使用原有API');
                }
            }
            
            console.log('🔍 发送历史消息请求:', { params, url: apiEndpoint, useNewAPI: WebSocketService.config.useNewAPI });
            
            const result = await get(apiEndpoint, { params });
            console.log('📦 后端返回原始数据:', result);

            // 处理后端返回的数据格式（兼容新旧两种格式）
            let messagesData;
            if (result.data && Array.isArray(result.data)) {
                // 新API格式: {success: true, data: [...]}
                messagesData = result.data;
            } else if (Array.isArray(result)) {
                // 原有API格式: [...]
                messagesData = result;
            } else {
                console.error('❌ 无法识别的数据格式:', result);
                messagesData = [];
            }
            
            const pastMessages = await Promise.all(
                messagesData.map(async (msg) => {
                    const senderInfo = await getUserCommunityInfoById(msg.userId);
                    return {
                        id: msg.id,
                        userId: msg.userId,
                        username: senderInfo.username || `用户${msg.userId}`,
                        nickname: senderInfo.nickname,
                        avatar: senderInfo.avatar,
                        content: msg.content,
                        createdAt: msg.createdAt
                    };
                })
            );
            
            console.log('✅ 转换后的消息数据:', pastMessages.map(m => ({ 
                id: m.id, 
                createdAt: m.createdAt, 
                content: m.content?.substring(0, 30) 
            })));
            
            return pastMessages;
            
        } catch (err) {
            console.error('获取历史消息失败:', err);
            onError('获取历史失败');
            return [];
        }
    },

    // 断开WebSocket连接
    disconnect: () => {
        if (WebSocketService.ws) {
            WebSocketService.ws.close();
            WebSocketService.ws = null;
        }
        clearInterval(WebSocketService.heartTimer);
    },

    // 更新配置（用于启用新API功能）
    updateConfig: (newConfig) => {
        WebSocketService.config = { ...WebSocketService.config, ...newConfig };
        console.log('🔧 WebSocket配置已更新:', WebSocketService.config);
    },

    // 启用新API功能
    enableNewAPI: () => {
        WebSocketService.updateConfig({ useNewAPI: true });
        console.log('✅ 已启用新API功能，下次连接将使用新的端点');
    },

    // 禁用新API功能（回退到原有API）
    disableNewAPI: () => {
        WebSocketService.updateConfig({ useNewAPI: false });
        console.log('⬇️ 已禁用新API功能，使用原有API端点');
    }
};

// React Hook 用于在组件中管理WebSocket
export const useWebSocket = (userId, getUserCommunityInfoById) => {
    const onMessageRef = useRef(null);
    const onErrorRef = useRef(null);
    const onConnectRef = useRef(null);
    const onDisconnectRef = useRef(null);
    const onNewMessageRef = useRef(null);
    const onMessageUpdateRef = useRef(null);

    const setOnMessage = useCallback((callback) => {
        onMessageRef.current = callback;
    }, []);

    const setOnError = useCallback((callback) => {
        onErrorRef.current = callback;
    }, []);

    const setOnConnect = useCallback((callback) => {
        onConnectRef.current = callback;
    }, []);

    const setOnDisconnect = useCallback((callback) => {
        onDisconnectRef.current = callback;
    }, []);

    const setOnNewMessage = useCallback((callback) => {
        onNewMessageRef.current = callback;
    }, []);

    const setOnMessageUpdate = useCallback((callback) => {
        onMessageUpdateRef.current = callback;
    }, []);

    useEffect(() => {
        if (userId && getUserCommunityInfoById) {
            WebSocketService.initialize(
                userId,
                (message) => onMessageRef.current?.(message),
                (error) => onErrorRef.current?.(error),
                () => onConnectRef.current?.(),
                () => onDisconnectRef.current?.(),
                (message) => onNewMessageRef.current?.(message),
                (message) => onMessageUpdateRef.current?.(message)
            );
        }

        return () => {
            WebSocketService.disconnect();
        };
    }, [userId, getUserCommunityInfoById]);

    return {
        sendMessage: (content) => WebSocketService.sendMessage(content, (error) => onErrorRef.current?.(error)),
        fetchPastMessages: (page, size, options = {}) =>
            WebSocketService.fetchPastMessages(page, size, options, getUserCommunityInfoById, (error) =>
                onErrorRef.current?.(error)
            ),
        setOnMessage,
        setOnError,
        setOnConnect,
        setOnDisconnect,
        setOnNewMessage,
        setOnMessageUpdate
    };
};

export default WebSocketService;