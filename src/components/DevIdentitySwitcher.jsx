import React, { useState } from 'react';
import { Button, Select, Space, notification } from 'antd';
import { UserOutlined, SettingOutlined } from '@ant-design/icons';
import { useUser } from '../lib/UserContext';

const { Option } = Select;

const DevIdentitySwitcher = () => {
    const { identity, setUserData } = useUser();
    const [selectedIdentity, setSelectedIdentity] = useState(identity || 'user');

    // 仅在开发环境显示
    if (process.env.NODE_ENV === 'production') {
        return null;
    }

    const handleSwitchIdentity = () => {
        // 模拟不同身份的用户数据
        const identityData = {
            user: {
                profile: {
                    username: '普通用户',
                    email: 'user@example.com',
                    nickname: '学生A'
                },
                status: {
                    identity: 'user',
                    valid: true
                }
            },
            admin: {
                profile: {
                    username: '管理员',
                    email: 'admin@example.com',
                    nickname: '管理员'
                },
                status: {
                    identity: 'admin',
                    valid: true
                }
            },
            school: {
                profile: {
                    username: '校园端',
                    email: 'school@example.com',
                    nickname: '校园管理员'
                },
                status: {
                    identity: 'school',
                    valid: true
                }
            }
        };

        const data = identityData[selectedIdentity];
        if (data) {
            setUserData(data.profile, data.status);
            notification.success({
                message: '身份切换成功',
                description: `已切换为${selectedIdentity === 'user' ? '普通用户' : selectedIdentity === 'admin' ? '管理员' : '校园端'}`,
                duration: 2
            });
        }
    };

    return (
        <div style={{
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            zIndex: 9999,
            background: '#fff',
            padding: '12px',
            borderRadius: '8px',
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            border: '2px solid #1890ff'
        }}>
            <div style={{ marginBottom: '8px', fontSize: '12px', color: '#666' }}>
                🔧 开发工具 - 身份切换
            </div>
            <Space>
                <Select
                    value={selectedIdentity}
                    onChange={setSelectedIdentity}
                    style={{ width: 120 }}
                    size="small"
                >
                    <Option value="user">
                        <UserOutlined /> 普通用户
                    </Option>
                
                    <Option value="school">
                        <SettingOutlined /> 校园端
                    </Option>
                </Select>
                <Button
                    type="primary"
                    size="small"
                    onClick={handleSwitchIdentity}
                >
                    切换
                </Button>
            </Space>
            <div style={{ marginTop: '4px', fontSize: '10px', color: '#999' }}>
                当前: {identity === 'user' ? '普通用户' : identity === 'admin' ? '管理员' : identity === 'school' ? '校园端' : '未知'}
            </div>
        </div>
    );
};

export default DevIdentitySwitcher; 