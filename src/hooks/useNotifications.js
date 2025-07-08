import { useState, useEffect, useCallback } from 'react';
import NotificationService from '../service/notificationService';

/**
 * 通知自定义 Hook
 * 提供通知相关的状态管理和操作函数
 */
const useNotifications = () => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // 轮询间隔（毫秒）
    const POLL_INTERVAL = 30000; // 30秒

    /**
     * 加载未读通知数量
     */
    const loadUnreadCount = useCallback(async () => {
        try {
            const response = await NotificationService.getUnreadCount();
            setUnreadCount(response.unreadCount || 0);
            setError(null);
        } catch (err) {
            console.error('加载未读通知数量失败:', err);
            setError('加载未读通知数量失败');
        }
    }, []);

    /**
     * 加载通知列表
     */
    const loadNotifications = useCallback(async (page = 0, size = 20) => {
        setLoading(true);
        try {
            const response = await NotificationService.getUserNotifications(page, size);
            setNotifications(response.content || []);
            setError(null);
        } catch (err) {
            console.error('加载通知列表失败:', err);
            setError('加载通知列表失败');
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * 轮询最新通知
     */
    const pollNotifications = useCallback(async (page = 0, size = 20) => {
        try {
            const response = await NotificationService.pollUserNotifications(page, size);
            setNotifications(response.content || []);
            setError(null);
        } catch (err) {
            console.error('轮询通知失败:', err);
            setError('轮询通知失败');
        }
    }, []);

    /**
     * 标记通知为已读
     */
    const markAsRead = useCallback(async (notificationId) => {
        try {
            await NotificationService.markAsRead(notificationId);
            
            // 更新本地状态
            setNotifications(prev => 
                prev.map(n => 
                    n.id === notificationId ? { ...n, read: true } : n
                )
            );
            
            // 重新加载未读数量
            await loadUnreadCount();
            setError(null);
        } catch (err) {
            console.error('标记通知已读失败:', err);
            setError('标记通知已读失败');
        }
    }, [loadUnreadCount]);

    /**
     * 批量标记所有通知为已读
     */
    const markAllAsRead = useCallback(async () => {
        try {
            await NotificationService.markAllAsRead();
            
            // 更新本地状态
            setNotifications(prev => 
                prev.map(n => ({ ...n, read: true }))
            );
            setUnreadCount(0);
            setError(null);
        } catch (err) {
            console.error('标记所有通知已读失败:', err);
            setError('标记所有通知已读失败');
        }
    }, []);

    /**
     * 删除通知
     */
    const deleteNotification = useCallback(async (notificationId) => {
        try {
            await NotificationService.deleteNotification(notificationId);
            
            // 更新本地状态
            setNotifications(prev => 
                prev.filter(n => n.id !== notificationId)
            );
            
            // 重新加载未读数量
            await loadUnreadCount();
            setError(null);
        } catch (err) {
            console.error('删除通知失败:', err);
            setError('删除通知失败');
        }
    }, [loadUnreadCount]);

    /**
     * 开始轮询
     */
    const startPolling = useCallback(() => {
        // 立即加载一次
        loadUnreadCount();
        
        // 设置定时器
        const pollId = setInterval(() => {
            loadUnreadCount();
        }, POLL_INTERVAL);
        
        return pollId;
    }, [loadUnreadCount]);

    /**
     * 停止轮询
     */
    const stopPolling = useCallback((pollId) => {
        if (pollId) {
            clearInterval(pollId);
        }
    }, []);

    /**
     * 刷新通知
     */
    const refreshNotifications = useCallback(async () => {
        await Promise.all([
            loadNotifications(),
            loadUnreadCount()
        ]);
    }, [loadNotifications, loadUnreadCount]);

    /**
     * 清除错误
     */
    const clearError = useCallback(() => {
        setError(null);
    }, []);

    // 组件挂载时开始轮询
    useEffect(() => {
        const pollId = startPolling();
        
        return () => {
            stopPolling(pollId);
        };
    }, [startPolling, stopPolling]);

    return {
        // 状态
        notifications,
        unreadCount,
        loading,
        error,
        
        // 操作函数
        loadNotifications,
        pollNotifications,
        markAsRead,
        markAllAsRead,
        deleteNotification,
        refreshNotifications,
        clearError,
        
        // 工具函数
        startPolling,
        stopPolling
    };
};

export default useNotifications;
