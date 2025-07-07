import React, { useState } from 'react';
import NotificationService from '../service/notificationService';

const NotificationTester = () => {
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    const createTestNotifications = async () => {
        setLoading(true);
        setMessage('');
        
        try {
            const testNotifications = [
                {
                    userId: NotificationService.getCurrentUserId(),
                    type: 'COMMENT',
                    title: '新的评论',
                    content: '有人评论了你的帖子：这个建议很实用！',
                    relatedId: 'post_123',
                    fromUserId: 2,
                    fromUsername: '张三'
                },
                {
                    userId: NotificationService.getCurrentUserId(),
                    type: 'LIKE',
                    title: '新的点赞',
                    content: '有人点赞了你的帖子《心理健康小贴士》',
                    relatedId: 'post_456',
                    fromUserId: 3,
                    fromUsername: '李四'
                },
                {
                    userId: NotificationService.getCurrentUserId(),
                    type: 'FOLLOW',
                    title: '新的关注',
                    content: '王五 关注了你',
                    relatedId: 'user_789',
                    fromUserId: 4,
                    fromUsername: '王五'
                },
                {
                    userId: NotificationService.getCurrentUserId(),
                    type: 'SYSTEM',
                    title: '系统通知',
                    content: '欢迎使用心晴校园！请完善您的个人资料以获得更好的体验。',
                    relatedId: null,
                    fromUserId: null,
                    fromUsername: '系统'
                }
            ];

            const results = [];
            for (const notification of testNotifications) {
                try {
                    const result = await NotificationService.createNotification(notification);
                    results.push(result);
                } catch (error) {
                    console.error('创建通知失败:', error);
                    results.push({ success: false, error: error.message });
                }
            }

            const successCount = results.filter(r => r.success).length;
            setMessage(`成功创建 ${successCount}/${testNotifications.length} 条测试通知`);
        } catch (error) {
            setMessage('创建测试通知失败: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    const clearAllNotifications = async () => {
        setLoading(true);
        setMessage('');
        
        try {
            // 这里可以添加清空所有通知的API调用
            setMessage('清空功能需要后端API支持');
        } catch (error) {
            setMessage('清空失败: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ 
            padding: '20px', 
            backgroundColor: '#f5f5f5', 
            minHeight: '100vh' 
        }}>
            <div style={{ 
                maxWidth: '600px', 
                margin: '0 auto', 
                backgroundColor: 'white', 
                padding: '30px', 
                borderRadius: '12px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
            }}>
                <h2 style={{ textAlign: 'center', marginBottom: '30px' }}>
                    通知系统测试工具
                </h2>
                
                <div style={{ marginBottom: '20px' }}>
                    <h3>创建测试通知</h3>
                    <p style={{ color: '#666', marginBottom: '16px' }}>
                        点击下方按钮创建一些测试通知，然后查看右上角的邮件图标变化。
                    </p>
                    <button
                        onClick={createTestNotifications}
                        disabled={loading}
                        style={{
                            backgroundColor: '#1890ff',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            padding: '12px 24px',
                            fontSize: '14px',
                            cursor: loading ? 'not-allowed' : 'pointer',
                            marginRight: '12px',
                            opacity: loading ? 0.6 : 1
                        }}
                    >
                        {loading ? '创建中...' : '创建测试通知'}
                    </button>
                    
                    <button
                        onClick={clearAllNotifications}
                        disabled={loading}
                        style={{
                            backgroundColor: '#ff4d4f',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            padding: '12px 24px',
                            fontSize: '14px',
                            cursor: loading ? 'not-allowed' : 'pointer',
                            opacity: loading ? 0.6 : 1
                        }}
                    >
                        {loading ? '清空中...' : '清空通知'}
                    </button>
                </div>

                {message && (
                    <div style={{
                        padding: '12px',
                        backgroundColor: message.includes('失败') ? '#fff2f0' : '#f6ffed',
                        border: `1px solid ${message.includes('失败') ? '#ffccc7' : '#b7eb8f'}`,
                        borderRadius: '6px',
                        marginTop: '16px'
                    }}>
                        <p style={{ 
                            margin: 0, 
                            color: message.includes('失败') ? '#ff4d4f' : '#52c41a' 
                        }}>
                            {message}
                        </p>
                    </div>
                )}

                <div style={{ marginTop: '30px', paddingTop: '20px', borderTop: '1px solid #f0f0f0' }}>
                    <h3>测试步骤</h3>
                    <ol style={{ color: '#666', lineHeight: '1.6' }}>
                        <li>确保已登录系统</li>
                        <li>点击"创建测试通知"按钮</li>
                        <li>查看页面右上角的邮件图标，应该会显示红色数字徽章</li>
                        <li>点击邮件图标，查看通知列表</li>
                        <li>测试标记已读、删除等功能</li>
                    </ol>
                </div>

                <div style={{ marginTop: '20px', padding: '16px', backgroundColor: '#f8f9fa', borderRadius: '6px' }}>
                    <h4 style={{ margin: '0 0 8px 0' }}>当前用户ID</h4>
                    <p style={{ margin: 0, fontFamily: 'monospace', color: '#666' }}>
                        {NotificationService.getCurrentUserId() || '未登录'}
                    </p>
                </div>
            </div>
        </div>
    );
};

export default NotificationTester;
