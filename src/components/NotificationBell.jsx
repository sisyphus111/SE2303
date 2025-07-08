import React, { useState, useEffect } from 'react';
import NotificationService from '../service/notificationService';
import '../css/NotificationBell.css';

/**
 * 通知铃铛组件
 * 显示未读通知数量和通知下拉列表
 */
const NotificationBell = () => {
    const [unreadCount, setUnreadCount] = useState(0);
    const [notifications, setNotifications] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isPolling, setIsPolling] = useState(false);

    // 轮询间隔（毫秒）
    const POLL_INTERVAL = 30000; // 30秒

    useEffect(() => {
        // 初始化加载
        loadUnreadCount();
        
        // 开始轮询
        startPolling();
        
        // 清理函数
        return () => {
            stopPolling();
        };
    }, []);

    /**
     * 加载未读通知数量
     */
    const loadUnreadCount = async () => {
        try {
            const response = await NotificationService.getUnreadCount();
            setUnreadCount(response.unreadCount || 0);
        } catch (error) {
            console.error('加载未读通知数量失败:', error);
        }
    };

    /**
     * 加载通知列表
     */
    const loadNotifications = async () => {
        if (loading) return;
        
        setLoading(true);
        try {
            const response = await NotificationService.getUserNotifications(0, 10);
            setNotifications(response.content || []);
        } catch (error) {
            console.error('加载通知列表失败:', error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * 开始轮询
     */
    const startPolling = () => {
        setIsPolling(true);
        const pollId = setInterval(async () => {
            await loadUnreadCount();
        }, POLL_INTERVAL);
        
        // 保存轮询ID以便清理
        window.notificationPollId = pollId;
    };

    /**
     * 停止轮询
     */
    const stopPolling = () => {
        setIsPolling(false);
        if (window.notificationPollId) {
            clearInterval(window.notificationPollId);
            window.notificationPollId = null;
        }
    };

    /**
     * 切换下拉菜单显示状态
     */
    const toggleDropdown = () => {
        const newShowState = !showDropdown;
        setShowDropdown(newShowState);
        
        // 如果打开下拉菜单，加载通知列表
        if (newShowState) {
            loadNotifications();
        }
    };

    /**
     * 标记通知为已读
     */
    const markAsRead = async (notificationId) => {
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
        } catch (error) {
            console.error('标记通知已读失败:', error);
        }
    };

    /**
     * 标记所有通知为已读
     */
    const markAllAsRead = async () => {
        try {
            await NotificationService.markAllAsRead();
            
            // 更新本地状态
            setNotifications(prev => 
                prev.map(n => ({ ...n, read: true }))
            );
            setUnreadCount(0);
        } catch (error) {
            console.error('标记所有通知已读失败:', error);
        }
    };

    /**
     * 删除通知
     */
    const deleteNotification = async (notificationId) => {
        try {
            await NotificationService.deleteNotification(notificationId);
            
            // 更新本地状态
            setNotifications(prev => 
                prev.filter(n => n.id !== notificationId)
            );
            
            // 重新加载未读数量
            await loadUnreadCount();
        } catch (error) {
            console.error('删除通知失败:', error);
        }
    };

    /**
     * 处理通知点击
     */
    const handleNotificationClick = (notification) => {
        // 如果通知未读，标记为已读
        if (!notification.read) {
            markAsRead(notification.id);
        }
        
        // TODO: 根据通知类型和relatedId跳转到相应页面
        console.log('点击通知:', notification);
    };

    return (
        <div className="notification-bell">
            <button 
                className="notification-bell-button"
                onClick={toggleDropdown}
                aria-label="通知"
            >
                <i className="fas fa-bell"></i>
                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            {showDropdown && (
                <div className="notification-dropdown">
                    <div className="notification-header">
                        <h3>通知</h3>
                        <div className="notification-actions">
                            {unreadCount > 0 && (
                                <button 
                                    className="mark-all-read-btn"
                                    onClick={markAllAsRead}
                                >
                                    全部已读
                                </button>
                            )}
                            <button 
                                className="close-btn"
                                onClick={() => setShowDropdown(false)}
                            >
                                <i className="fas fa-times"></i>
                            </button>
                        </div>
                    </div>

                    <div className="notification-content">
                        {loading ? (
                            <div className="notification-loading">
                                <i className="fas fa-spinner fa-spin"></i>
                                <span>加载中...</span>
                            </div>
                        ) : notifications.length === 0 ? (
                            <div className="notification-empty">
                                <i className="fas fa-bell-slash"></i>
                                <span>暂无通知</span>
                            </div>
                        ) : (
                            <div className="notification-list">
                                {notifications.map(notification => (
                                    <div 
                                        key={notification.id}
                                        className={`notification-item ${notification.read ? 'read' : 'unread'}`}
                                        onClick={() => handleNotificationClick(notification)}
                                    >
                                        <div className="notification-content">
                                            <div className="notification-title">
                                                {notification.title}
                                            </div>
                                            <div className="notification-text">
                                                {notification.content}
                                            </div>
                                            <div className="notification-meta">
                                                <span className="notification-type">
                                                    {NotificationService.getNotificationTypeText(notification.type)}
                                                </span>
                                                <span className="notification-time">
                                                    {NotificationService.formatNotificationTime(notification.createdAt)}
                                                </span>
                                            </div>
                                        </div>
                                        <div className="notification-actions">
                                            {!notification.read && (
                                                <button 
                                                    className="mark-read-btn"
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        markAsRead(notification.id);
                                                    }}
                                                    title="标记为已读"
                                                >
                                                    <i className="fas fa-check"></i>
                                                </button>
                                            )}
                                            <button 
                                                className="delete-btn"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    deleteNotification(notification.id);
                                                }}
                                                title="删除"
                                            >
                                                <i className="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="notification-footer">
                        <button 
                            className="view-all-btn"
                            onClick={() => {
                                setShowDropdown(false);
                                // TODO: 跳转到通知页面
                                console.log('查看所有通知');
                            }}
                        >
                            查看所有通知
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
