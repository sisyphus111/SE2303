import { useCallback, useEffect, useRef } from 'react';
import { get, post } from '../service/common'; // 引入common.js的get和post方法
import { PREFIX, WEBSOCKET_URL } from '../service/common'; // 引入PREFIX和WEBSOCKET_URL

// WebSocketService 作为一个类，管理WebSocket连接和相关逻辑
const WebSocketService = {
    ws: null, // WebSocket实例
    heartTimer: null, // 心跳定时器
    wsIp: WEBSOCKET_URL, // 通过网关连接WebSocket，使用内网穿透域名
    apiGateway: PREFIX, // 使用common.js的PREFIX

    // 初始化WebSocket连接
    initialize: (userId, onMessage, onError, onConnect, onDisconnect) => {
        if (!userId) {
            onError('未提供userId，无法建立WebSocket连接');
            return;
        }

        if (WebSocketService.ws && WebSocketService.ws.readyState === WebSocket.OPEN) {
            console.log('WebSocket已连接，无需重复初始化');
            return;
        }

        WebSocketService.ws = new WebSocket(`${WebSocketService.wsIp}/ws/talk?x-userId=${userId}`);

        WebSocketService.ws.onopen = () => {
            console.log('WebSocket连接已建立');
            WebSocketService.startHeartBeat();
            if (onConnect) onConnect();
        };

        WebSocketService.ws.onmessage = async (event) => {
            try {
                const message = JSON.parse(event.data);
                if (message.type === 'text') {
                    if (onMessage) onMessage(message);
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
            setTimeout(() => WebSocketService.initialize(userId, onMessage, onError, onConnect, onDisconnect), 5000);
        };

        WebSocketService.ws.onerror = (err) => {
            console.error('WebSocket错误:', err);
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
    fetchPastMessages: async (page, size, getUserCommunityInfoById, onError) => {
        try {
            const result = await get('/talk/messages', { params: { page, size } });

            const pastMessages = await Promise.all(
                result.map(async (msg) => {
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
    }
};

// React Hook 用于在组件中管理WebSocket
export const useWebSocket = (userId, getUserCommunityInfoById) => {
    const onMessageRef = useRef(null);
    const onErrorRef = useRef(null);
    const onConnectRef = useRef(null);
    const onDisconnectRef = useRef(null);

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

    useEffect(() => {
        if (userId && getUserCommunityInfoById) {
            WebSocketService.initialize(
                userId,
                (message) => onMessageRef.current?.(message),
                (error) => onErrorRef.current?.(error),
                () => onConnectRef.current?.(),
                () => onDisconnectRef.current?.()
            );
        }

        return () => {
            WebSocketService.disconnect();
        };
    }, [userId, getUserCommunityInfoById]);

    return {
        sendMessage: (content) => WebSocketService.sendMessage(content, (error) => onErrorRef.current?.(error)),
        fetchPastMessages: (page, size) =>
            WebSocketService.fetchPastMessages(page, size, getUserCommunityInfoById, (error) =>
                onErrorRef.current?.(error)
            ),
        setOnMessage,
        setOnError,
        setOnConnect,
        setOnDisconnect
    };
};

export default WebSocketService;