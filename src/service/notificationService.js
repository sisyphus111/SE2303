import { get, post, put, del } from './common';

/**
 * 通知服务
 * 提供与后端通知微服务的接口
 */
class NotificationService {
    // API 基础路径
    static API_BASE = '/informs';

    /**
     * 获取用户通知列表（分页）
     * @param {number} page - 页码（从0开始）
     * @param {number} size - 每页大小
     * @returns {Promise} 通知列表
     */
    static async getUserNotifications(page = 0, size = 20) {
        const userId = this.getCurrentUserId();
        if (!userId) {
            throw new Error('用户未登录');
        }
        
        return await get(`${this.API_BASE}/user/${userId}?page=${page}&size=${size}`);
    }

    /**
     * 轮询获取用户最新通知
     * @param {number} page - 页码
     * @param {number} size - 每页大小
     * @returns {Promise} 最新通知列表
     */
    static async pollUserNotifications(page = 0, size = 20) {
        const userId = this.getCurrentUserId();
        if (!userId) {
            throw new Error('用户未登录');
        }
        
        return await get(`${this.API_BASE}/user/${userId}/poll?page=${page}&size=${size}`);
    }

    /**
     * 获取用户未读通知数量
     * @returns {Promise} 未读通知数量
     */
    static async getUnreadCount() {
        const userId = this.getCurrentUserId();
        if (!userId) {
            return { unreadCount: 0 };
        }
        
        return await get(`${this.API_BASE}/user/${userId}/unread-count`);
    }

    /**
     * 标记通知为已读
     * @param {number} notificationId - 通知ID
     * @returns {Promise} 操作结果
     */
    static async markAsRead(notificationId) {
        const userId = this.getCurrentUserId();
        if (!userId) {
            throw new Error('用户未登录');
        }
        
        return await put(`${this.API_BASE}/${notificationId}/read?userId=${userId}`);
    }

    /**
     * 批量标记所有通知为已读
     * @returns {Promise} 操作结果
     */
    static async markAllAsRead() {
        const userId = this.getCurrentUserId();
        if (!userId) {
            throw new Error('用户未登录');
        }
        
        return await put(`${this.API_BASE}/user/${userId}/read-all`);
    }

    /**
     * 删除通知
     * @param {number} notificationId - 通知ID
     * @returns {Promise} 操作结果
     */
    static async deleteNotification(notificationId) {
        const userId = this.getCurrentUserId();
        if (!userId) {
            throw new Error('用户未登录');
        }
        
        return await del(`${this.API_BASE}/${notificationId}?userId=${userId}`);
    }

    /**
     * 创建通知（用于测试）
     * @param {Object} notificationData - 通知数据
     * @returns {Promise} 创建结果
     */
    static async createNotification(notificationData) {
        return await post(`${this.API_BASE}`, notificationData);
    }

    /**
     * 获取当前用户ID
     * @returns {number|null} 用户ID
     */
    static getCurrentUserId() {
        try {
            const userStr = localStorage.getItem('user');
            console.log('📱 localStorage中的用户数据:', userStr);
            
            if (userStr) {
                const user = JSON.parse(userStr);
                console.log('👤 解析后的用户对象:', user);
                const userId = user.id || user.userId;
                console.log('🆔 提取的用户ID:', userId);
                return userId;
            }
            
            console.warn('⚠️ localStorage中没有用户数据，返回测试用户ID: 1');
            return 1; // 临时返回测试用户ID
        } catch (error) {
            console.error('❌ 获取用户ID失败:', error);
            return 1; // 临时返回测试用户ID
        }
    }

    /**
     * 通知类型映射
     */
    static NOTIFICATION_TYPES = {
        COMMENT: '评论',
        LIKE: '点赞',
        FOLLOW: '关注',
        SYSTEM: '系统通知'
    };

    /**
     * 获取通知类型文本
     * @param {string} type - 通知类型
     * @returns {string} 类型文本
     */
    static getNotificationTypeText(type) {
        return this.NOTIFICATION_TYPES[type] || type;
    }

    /**
     * 格式化通知时间
     * @param {string} timestamp - 时间戳
     * @returns {string} 格式化后的时间
     */
    static formatNotificationTime(timestamp) {
        const now = new Date();
        const time = new Date(timestamp);
        const diff = now - time;
        
        const minutes = Math.floor(diff / (1000 * 60));
        const hours = Math.floor(diff / (1000 * 60 * 60));
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        
        if (minutes < 1) {
            return '刚刚';
        } else if (minutes < 60) {
            return `${minutes}分钟前`;
        } else if (hours < 24) {
            return `${hours}小时前`;
        } else if (days < 7) {
            return `${days}天前`;
        } else {
            return time.toLocaleDateString();
        }
    }
}

export default NotificationService;
