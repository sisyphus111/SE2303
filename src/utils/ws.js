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

        // 使用旧API端点连接
        const wsUrl = `${WebSocketService.wsIp}/ws/talk/conn?x-userId=${userId}`;

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
            
            // 使用标准API端点
            const apiEndpoint = '/talk/messages';
            
            console.log('🔍 发送历史消息请求:', { params, url: apiEndpoint });
            
            const result = await get(apiEndpoint, { params });
            console.log('📦 后端返回原始数据:', result);

            // 处理后端返回的数据格式
            let messagesData;
            if (result.data && Array.isArray(result.data)) {
                // API格式: {success: true, data: [...]}
                messagesData = result.data;
            } else if (Array.isArray(result)) {
                // 直接数组格式: [...]
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

    // 更新配置
    updateConfig: (newConfig) => {
        WebSocketService.config = { ...WebSocketService.config, ...newConfig };
        console.log('🔧 WebSocket配置已更新:', WebSocketService.config);
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

// 全局调试工具 - 用于测试WebSocket连接
if (typeof window !== 'undefined') {
    window.WSDebug = {
        // 显示帮助信息
        help() {
            console.log(`
🔧 WebSocket调试工具帮助:

1. WSDebug.getConfig()       - 查看当前配置
2. WSDebug.testConnection()  - 测试连接配置
3. WSDebug.forceReconnect()  - 强制重新连接
4. WSDebug.getStatus()       - 查看连接状态
5. WSDebug.help()           - 显示此帮助信息

当前连接路径: /ws/talk/conn
            `);
        },

        // 查看当前配置
        getConfig() {
            console.log('📋 当前WebSocket配置:', WebSocketService.config);
            console.log('🔗 WebSocket状态:', WebSocketService.ws?.readyState);
            console.log('📡 连接地址:', WebSocketService.wsIp);
            return WebSocketService.config;
        },



        // 测试连接配置
        testConnection() {
            const testUrl = `${WebSocketService.wsIp}/ws/talk/conn?x-userId=test123`;
            
            console.log('🧪 测试WebSocket连接配置:');
            console.log('📍 测试地址:', testUrl);
            
            // 创建测试连接（不影响现有连接）
            const testWs = new WebSocket(testUrl);
            
            testWs.onopen = () => {
                console.log('✅ 测试连接成功');
                testWs.close();
            };
            
            testWs.onerror = (error) => {
                console.error('❌ 测试连接失败:', error);
            };
            
            testWs.onclose = (event) => {
                console.log('🔌 测试连接已关闭:', event.code, event.reason);
            };
            
            return '测试连接已启动，请查看控制台输出';
        },

        // 强制重新连接
        forceReconnect() {
            console.log('🔄 强制重新连接WebSocket...');
            WebSocketService.disconnect();
            setTimeout(() => {
                console.log('🔗 开始重新连接...');
                // 这里需要用户自己调用初始化方法
                console.log('⚠️ 请在组件中手动重新初始化WebSocket连接');
            }, 1000);
            return '已断开连接，请在组件中重新初始化';
        },

        // 查看连接状态
        getStatus() {
            const ws = WebSocketService.ws;
            if (!ws) {
                console.log('❌ WebSocket未初始化');
                return 'not-initialized';
            }
            
            const states = {
                0: 'CONNECTING',
                1: 'OPEN', 
                2: 'CLOSING',
                3: 'CLOSED'
            };
            
            const status = states[ws.readyState] || 'UNKNOWN';
            console.log('📡 WebSocket状态:', status);
            console.log('🔗 连接地址:', ws.url);
            
            return {
                readyState: ws.readyState,
                status: status,
                url: ws.url
            };
        }
    };

    // 在控制台显示调试工具可用信息
    console.log('🔧 WebSocket调试工具已加载！输入 WSDebug.help() 查看帮助');
}

export default WebSocketService;