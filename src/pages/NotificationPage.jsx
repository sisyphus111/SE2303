import React, { useState, useEffect } from 'react';
import NotificationService from '../service/notificationService';
import '../css/NotificationPage.css';

/**
 * 通知页面组件
 * 显示用户的所有通知，支持分页、筛选和操作
 */
const NotificationPage = () => {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [filter, setFilter] = useState('ALL'); // ALL, UNREAD, READ
    const [typeFilter, setTypeFilter] = useState('ALL'); // ALL, COMMENT, LIKE, FOLLOW, SYSTEM
    const [pageSize] = useState(20);

    useEffect(() => {
        loadNotifications();
    }, [currentPage, filter, typeFilter]);

    /**
     * 加载通知列表
     */
    const loadNotifications = async () => {
        setLoading(true);
        try {
            const response = await NotificationService.getUserNotifications(currentPage, pageSize);
            
            let filteredNotifications = response.content || [];
            
            // 应用已读/未读筛选
            if (filter === 'UNREAD') {
                filteredNotifications = filteredNotifications.filter(n => !n.read);
            } else if (filter === 'READ') {
                filteredNotifications = filteredNotifications.filter(n => n.read);
            }
            
            // 应用类型筛选
            if (typeFilter !== 'ALL') {
                filteredNotifications = filteredNotifications.filter(n => n.type === typeFilter);
            }
            
            setNotifications(filteredNotifications);
            setTotalPages(response.totalPages || 0);
            setTotalElements(response.totalElements || 0);
        } catch (error) {
            console.error('加载通知失败:', error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * 标记通知为已读
     */
    const markAsRead = async (notificationId) => {
        try {
            await NotificationService.markAsRead(notificationId);
            setNotifications(prev => 
                prev.map(n => 
                    n.id === notificationId ? { ...n, read: true } : n
                )
            );
        } catch (error) {
            console.error('标记通知已读失败:', error);
        }
    };

    /**
     * 批量标记所有通知为已读
     */
    const markAllAsRead = async () => {
        try {
            await NotificationService.markAllAsRead();
            setNotifications(prev => 
                prev.map(n => ({ ...n, read: true }))
            );
        } catch (error) {
            console.error('批量标记通知已读失败:', error);
        }
    };

    /**
     * 删除通知
     */
    const deleteNotification = async (notificationId) => {
        if (!window.confirm('确定要删除这条通知吗？')) {
            return;
        }
        
        try {
            await NotificationService.deleteNotification(notificationId);
            setNotifications(prev => 
                prev.filter(n => n.id !== notificationId)
            );
            setTotalElements(prev => prev - 1);
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

    /**
     * 处理分页
     */
    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
    };

    /**
     * 刷新通知列表
     */
    const refreshNotifications = () => {
        setCurrentPage(0);
        loadNotifications();
    };

    return (
        <div className="notification-page">
            <div className="notification-page-header">
                <h1>我的通知</h1>
                <div className="notification-stats">
                    <span>共 {totalElements} 条通知</span>
                </div>
            </div>

            <div className="notification-filters">
                <div className="filter-group">
                    <label>状态筛选：</label>
                    <select 
                        value={filter} 
                        onChange={(e) => setFilter(e.target.value)}
                    >
                        <option value="ALL">全部</option>
                        <option value="UNREAD">未读</option>
                        <option value="READ">已读</option>
                    </select>
                </div>

                <div className="filter-group">
                    <label>类型筛选：</label>
                    <select 
                        value={typeFilter} 
                        onChange={(e) => setTypeFilter(e.target.value)}
                    >
                        <option value="ALL">全部类型</option>
                        <option value="COMMENT">评论</option>
                        <option value="LIKE">点赞</option>
                        <option value="FOLLOW">关注</option>
                        <option value="SYSTEM">系统通知</option>
                    </select>
                </div>

                <div className="filter-actions">
                    <button 
                        className="refresh-btn"
                        onClick={refreshNotifications}
                        disabled={loading}
                    >
                        <i className="fas fa-sync-alt"></i>
                        刷新
                    </button>
                    <button 
                        className="mark-all-read-btn"
                        onClick={markAllAsRead}
                        disabled={loading}
                    >
                        <i className="fas fa-check-double"></i>
                        全部已读
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
                        <h3>暂无通知</h3>
                        <p>您还没有收到任何通知</p>
                    </div>
                ) : (
                    <div className="notification-list">
                        {notifications.map(notification => (
                            <div 
                                key={notification.id}
                                className={`notification-item ${notification.read ? 'read' : 'unread'}`}
                                data-type={notification.type}
                                onClick={() => handleNotificationClick(notification)}
                            >
                                <div className="notification-indicator">
                                    {!notification.read && <span className="unread-dot"></span>}
                                </div>
                                
                                <div className="notification-content">
                                    <div className="notification-header">
                                        <h3 className="notification-title">
                                            {notification.title}
                                        </h3>
                                        <span className="notification-time">
                                            {NotificationService.formatNotificationTime(notification.createdAt)}
                                        </span>
                                    </div>
                                    
                                    <div className="notification-text">
                                        {notification.content}
                                    </div>
                                    
                                    <div className="notification-meta">
                                        <span className="notification-type">
                                            {NotificationService.getNotificationTypeText(notification.type)}
                                        </span>
                                        {notification.relatedId && (
                                            <span className="notification-related">
                                                相关ID: {notification.relatedId}
                                            </span>
                                        )}
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

            {/* 分页器 */}
            {totalPages > 1 && (
                <div className="notification-pagination">
                    <button 
                        className="page-btn"
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 0}
                    >
                        <i className="fas fa-chevron-left"></i>
                        上一页
                    </button>
                    
                    <span className="page-info">
                        第 {currentPage + 1} 页，共 {totalPages} 页
                    </span>
                    
                    <button 
                        className="page-btn"
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage >= totalPages - 1}
                    >
                        下一页
                        <i className="fas fa-chevron-right"></i>
                    </button>
                </div>
            )}
        </div>
    );
};

export default NotificationPage;
