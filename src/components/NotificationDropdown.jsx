import React, { useState, useEffect, useRef } from 'react';
import { 
    BellOutlined, 
    MailOutlined,
    HeartOutlined, 
    MessageOutlined, 
    UserAddOutlined, 
    CheckOutlined,
    DeleteOutlined,
    MoreOutlined 
} from '@ant-design/icons';
import NotificationService from '../service/notificationService';
import '../css/NotificationDropdown.css';

const NotificationDropdown = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const dropdownRef = useRef(null);

    // 加载通知数据
    const loadNotifications = async () => {
        try {
            setLoading(true);
            setError(null);
            
            console.log('🔍 开始加载通知数据...');
            
            // 获取通知列表和未读数量
            const [notificationsResponse, unreadResponse] = await Promise.all([
                NotificationService.getUserNotifications(0, 10), // 获取最新10条
                NotificationService.getUnreadCount()
            ]);

            console.log('📥 API响应数据:', {
                notifications: notificationsResponse,
                unread: unreadResponse
            });

            // 注意：handleApiResponse已经处理了success判断，直接返回data部分
            if (notificationsResponse && notificationsResponse.content) {
                console.log('✅ 进入数据处理分支');
                console.log('📋 原始通知数据:', notificationsResponse);
                console.log('📋 通知内容数组:', notificationsResponse.content);
                
                // 转换后端数据格式
                const formattedNotifications = notificationsResponse.content.map(item => {
                    console.log('🔍 处理通知项:', item);
                    
                    const formatted = {
                        id: item.id,
                        type: item.type.toLowerCase(),
                        title: item.title,
                        content: item.content,
                        relatedId: item.relatedId,
                        isRead: item.read, // 后端字段名是read，前端转换为isRead
                        createdAt: item.createdAt,
                        fromUserId: item.fromUserId,
                        fromUsername: item.fromUsername,
                        avatar: item.fromUserId ? `https://api.dicebear.com/7.x/avataaars/svg?seed=${item.fromUserId}` : null
                    };
                    
                    return formatted;
                });
                
                console.log('✅ 格式化后的通知数据:', formattedNotifications);
                setNotifications(formattedNotifications);
                
                // 添加延迟检查状态是否更新
                setTimeout(() => {
                    console.log('🔍 状态更新后的通知数据:', notifications);
                    console.log('🔍 通知列表长度:', notifications.length);
                }, 100);
            } else {
                console.warn('⚠️ 通知数据格式异常:', notificationsResponse);
            }

            if (unreadResponse && typeof unreadResponse.unreadCount === 'number') {
                console.log('🔢 未读数量:', unreadResponse);
                setUnreadCount(unreadResponse.unreadCount);
            } else {
                console.warn('⚠️ 未读数量数据异常:', unreadResponse);
            }
        } catch (err) {
            console.error('❌ 加载通知失败:', err);
            setError('加载通知失败，请稍后重试');
            // 如果API调用失败，使用模拟数据作为后备
            loadMockData();
        } finally {
            setLoading(false);
        }
    };

    // 模拟数据作为后备
    const loadMockData = () => {
        const mockNotifications = [
            {
                id: 1,
                type: 'like',
                title: '新的点赞',
                content: '张三 点赞了你的帖子《分享一些减压的小技巧》',
                relatedId: 'post_123',
                isRead: false,
                createdAt: new Date().toISOString(),
                avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan'
            },
            {
                id: 2,
                type: 'comment',
                title: '新的评论',
                content: '李四 评论了你的帖子：这个建议很实用，谢谢分享！',
                relatedId: 'post_123',
                isRead: false,
                createdAt: new Date(Date.now() - 3600000).toISOString(),
                avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi'
            },
            {
                id: 3,
                type: 'follow',
                title: '新的关注',
                content: '王五 关注了你',
                relatedId: 'user_456',
                isRead: false,
                createdAt: new Date(Date.now() - 7200000).toISOString(),
                avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu'
            }
        ];
        
        setNotifications(mockNotifications);
        setUnreadCount(mockNotifications.filter(n => !n.isRead).length);
    };

    // 初始化数据
    useEffect(() => {
        loadNotifications();
        
        // 定期轮询新通知（每30秒）
        const interval = setInterval(() => {
            if (!isOpen) { // 只在下拉菜单关闭时轮询
                loadNotifications();
            }
        }, 30000);

        return () => clearInterval(interval);
    }, []);

    // 当下拉菜单打开时刷新数据
    useEffect(() => {
        if (isOpen) {
            loadNotifications();
        }
    }, [isOpen]);

    // 点击外部关闭下拉菜单
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };

        if (isOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isOpen]);

    // 获取通知类型图标
    const getNotificationIcon = (type) => {
        console.log('🎨 获取通知图标，类型:', type);
        
        switch (type) {
            case 'like':
                return <HeartOutlined className="notification-icon like" />;
            case 'comment':
                return <MessageOutlined className="notification-icon comment" />;
            case 'follow':
                return <UserAddOutlined className="notification-icon follow" />;
            default:
                console.log('⚠️ 未知通知类型，使用默认图标:', type);
                return <BellOutlined className="notification-icon" />;
        }
    };

    // 格式化时间
    const formatTime = (timestamp) => {
        return NotificationService.formatNotificationTime(timestamp);
    };

    // 标记单个通知为已读
    const markAsRead = async (notificationId) => {
        try {
            console.log('🔄 标记通知为已读:', notificationId);
            
            // 调用API标记为已读
            const response = await NotificationService.markAsRead(notificationId);
            console.log('📥 标记已读API响应:', response);
            
            // 注意：handleApiResponse已经处理了success判断，直接返回data部分
            // 如果能执行到这里，说明API调用成功
            setNotifications(prev => 
                prev.map(notification => 
                    notification.id === notificationId 
                        ? { ...notification, isRead: true }
                        : notification
                )
            );
            setUnreadCount(prev => Math.max(0, prev - 1));
            console.log('✅ 通知已标记为已读，UI已更新');
            
        } catch (error) {
            console.error('❌ 标记已读失败:', error);
        }
    };

    // 标记所有通知为已读
    const markAllAsRead = async () => {
        try {
            console.log('🔄 标记所有通知为已读');
            
            // 调用API标记所有为已读
            const response = await NotificationService.markAllAsRead();
            console.log('📥 标记所有已读API响应:', response);
            
            // 注意：handleApiResponse已经处理了success判断，直接返回data部分
            // 如果能执行到这里，说明API调用成功
            setNotifications(prev => 
                prev.map(notification => ({ ...notification, isRead: true }))
            );
            setUnreadCount(0);
            console.log('✅ 所有通知已标记为已读，UI已更新');
            
        } catch (error) {
            console.error('❌ 批量标记已读失败:', error);
        }
    };

    // 删除通知
    const deleteNotification = async (notificationId) => {
        try {
            console.log('🗑️ 删除通知:', notificationId);
            
            // 调用API删除通知
            const response = await NotificationService.deleteNotification(notificationId);
            console.log('📥 删除通知API响应:', response);
            
            // 注意：handleApiResponse已经处理了success判断，直接返回data部分
            // 如果能执行到这里，说明API调用成功
            const notification = notifications.find(n => n.id === notificationId);
            setNotifications(prev => prev.filter(n => n.id !== notificationId));
            
            // 如果删除的是未读通知，需要更新未读数量
            if (notification && !notification.isRead) {
                setUnreadCount(prev => Math.max(0, prev - 1));
            }
            console.log('✅ 通知已删除，UI已更新');
            
        } catch (error) {
            console.error('❌ 删除通知失败:', error);
        }
    };

    // 处理通知点击
    const handleNotificationClick = (notification) => {
        if (!notification.isRead) {
            markAsRead(notification.id);
        }
        
        // 这里可以添加跳转逻辑
        console.log('点击通知:', notification);
        // 根据 relatedId 和 type 进行页面跳转
    };

    return (
        <div className="notification-dropdown" ref={dropdownRef}>
            <button 
                className="notification-trigger"
                onClick={() => setIsOpen(!isOpen)}
            >
                <MailOutlined />
                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            {isOpen && (
                <div className="notification-panel">
                    <div className="notification-header">
                        <h3>消息通知</h3>
                        <div className="notification-actions">
                            {unreadCount > 0 && (
                                <button 
                                    className="mark-all-read-btn"
                                    onClick={markAllAsRead}
                                >
                                    <CheckOutlined />
                                    全部已读
                                </button>
                            )}
                        </div>
                    </div>

                    <div className="notification-list">
                        {(() => {
                            console.log('🎨 渲染条件检查:');
                            console.log('loading:', loading);
                            console.log('error:', error);
                            console.log('notifications.length:', notifications.length);
                            console.log('notifications:', notifications);
                            return null;
                        })()}
                        
                        {loading ? (
                            <div className="loading-notifications">
                                <div className="loading-spinner"></div>
                                <p>加载中...</p>
                            </div>
                        ) : error ? (
                            <div className="error-notifications">
                                <p className="error-message">{error}</p>
                                <button 
                                    className="retry-button"
                                    onClick={loadNotifications}
                                >
                                    重试
                                </button>
                            </div>
                        ) : notifications.length === 0 ? (
                            <div className="no-notifications">
                                <BellOutlined />
                                <p>暂无消息</p>
                            </div>
                        ) : (
                            notifications.map((notification, index) => {
                                console.log(`🎨 渲染通知项 [${index}]:`, notification);
                                
                                return (
                                    <div 
                                        key={notification.id}
                                        className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
                                        onClick={() => handleNotificationClick(notification)}
                                    >
                                        <div className="notification-avatar">
                                            {notification.avatar ? (
                                                <img 
                                                    src={notification.avatar} 
                                                    alt="头像"
                                                    className="avatar-image"
                                                />
                                            ) : (
                                                <div className="avatar-fallback">
                                                    {getNotificationIcon(notification.type)}
                                                </div>
                                            )}
                                        </div>
                                    
                                    <div className="notification-content">
                                        <div className="notification-main">
                                            <h4 className="notification-title">
                                                {notification.title}
                                            </h4>
                                            <p className="notification-text">
                                                {notification.content}
                                            </p>
                                        </div>
                                        
                                        <div className="notification-meta">
                                            <span className="notification-time">
                                                {formatTime(notification.createdAt)}
                                            </span>
                                            {!notification.isRead && (
                                                <span className="unread-dot"></span>
                                            )}
                                        </div>
                                    </div>

                                    <div className="notification-actions">
                                        <button 
                                            className="action-btn"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                deleteNotification(notification.id);
                                            }}
                                        >
                                            <DeleteOutlined />
                                        </button>
                                    </div>
                                </div>
                                );
                            })
                        )}
                    </div>

                    {notifications.length > 0 && (
                        <div className="notification-footer">
                            <button className="view-all-btn">
                                查看全部消息
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default NotificationDropdown;
