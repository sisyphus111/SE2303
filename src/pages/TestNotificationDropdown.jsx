import React from 'react';
import NotificationDropdown from '../components/NotificationDropdown';

// 简单的测试组件
const TestNotificationDropdown = () => {
    return (
        <div style={{ 
            padding: '20px', 
            backgroundColor: '#f5f5f5', 
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
        }}>
            <div style={{ 
                backgroundColor: 'white', 
                padding: '20px', 
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
            }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>消息通知测试</h2>
                <div style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    gap: '20px'
                }}>
                    <span>邮件图标：</span>
                    <NotificationDropdown />
                </div>
                <p style={{ marginTop: '20px', textAlign: 'center', color: '#666' }}>
                    点击邮件图标查看消息通知
                </p>
            </div>
        </div>
    );
};

export default TestNotificationDropdown;
