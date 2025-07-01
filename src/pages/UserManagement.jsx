import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Input, Select } from 'antd';
import { SearchOutlined, UserOutlined, StopOutlined, CheckOutlined } from '@ant-design/icons';

const { Search } = Input;
const { Option } = Select;

const UserManagement = () => {
    const [loading, setLoading] = useState(false);
    const [users, setUsers] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [filterStatus, setFilterStatus] = useState('all');

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        setLoading(true);
        try {
            // TODO: 调用后端API获取用户列表
            // 这里使用模拟数据
            const mockUsers = [
                {
                    id: 1,
                    username: '张三',
                    email: 'zhangsan@example.com',
                    status: 'active',
                    riskLevel: 'low',
                    lastLogin: '2024-01-15 14:30',
                    joinDate: '2024-01-01'
                },
                {
                    id: 2,
                    username: '李四',
                    email: 'lisi@example.com',
                    status: 'disabled',
                    riskLevel: 'medium',
                    lastLogin: '2024-01-14 09:15',
                    joinDate: '2023-12-20'
                },
                {
                    id: 3,
                    username: '王五',
                    email: 'wangwu@example.com',
                    status: 'active',
                    riskLevel: 'high',
                    lastLogin: '2024-01-15 16:45',
                    joinDate: '2024-01-10'
                }
            ];
            setUsers(mockUsers);
        } catch (error) {
            message.error('获取用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = (userId, newStatus) => {
        Modal.confirm({
            title: `确认${newStatus === 'active' ? '启用' : '禁用'}用户？`,
            content: `此操作将${newStatus === 'active' ? '启用' : '禁用'}该用户的账户`,
            onOk: async () => {
                try {
                    // TODO: 调用后端API
                    message.success(`用户已${newStatus === 'active' ? '启用' : '禁用'}`);
                    loadUsers();
                } catch (error) {
                    message.error('操作失败');
                }
            }
        });
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
            filteredValue: searchText ? [searchText] : null,
            onFilter: (value, record) =>
                record.username.toLowerCase().includes(value.toLowerCase()) ||
                record.email.toLowerCase().includes(value.toLowerCase()),
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => (
                <Tag color={status === 'active' ? 'green' : 'red'}>
                    {status === 'active' ? '正常' : '已禁用'}
                </Tag>
            ),
        },
        {
            title: '风险等级',
            dataIndex: 'riskLevel',
            key: 'riskLevel',
            render: (level) => {
                const config = {
                    low: { color: 'green', text: '低风险' },
                    medium: { color: 'orange', text: '中风险' },
                    high: { color: 'red', text: '高风险' }
                };
                return (
                    <Tag color={config[level]?.color}>
                        {config[level]?.text}
                    </Tag>
                );
            },
        },
        {
            title: '最后登录',
            dataIndex: 'lastLogin',
            key: 'lastLogin',
        },
        {
            title: '注册时间',
            dataIndex: 'joinDate',
            key: 'joinDate',
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        size="small" 
                        icon={<UserOutlined />}
                        onClick={() => {
                            // TODO: 查看用户详情
                            message.info('用户详情功能待开发');
                        }}
                    >
                        详情
                    </Button>
                    {record.status === 'active' ? (
                        <Button 
                            size="small" 
                            danger
                            icon={<StopOutlined />}
                            onClick={() => handleStatusChange(record.id, 'disabled')}
                        >
                            禁用
                        </Button>
                    ) : (
                        <Button 
                            size="small" 
                            type="primary"
                            icon={<CheckOutlined />}
                            onClick={() => handleStatusChange(record.id, 'active')}
                        >
                            启用
                        </Button>
                    )}
                </Space>
            ),
        },
    ];

    const filteredUsers = users.filter(user => {
        if (filterStatus === 'all') return true;
        return user.status === filterStatus;
    });

    return (
        <div style={{ padding: '24px' }}>
            <h1 style={{ marginBottom: '24px' }}>用户管理</h1>
            
            {/* 搜索和筛选 */}
            <div style={{ marginBottom: '16px', display: 'flex', gap: '16px' }}>
                <Search
                    placeholder="搜索用户名或邮箱"
                    allowClear
                    onSearch={setSearchText}
                    style={{ width: 300 }}
                    prefix={<SearchOutlined />}
                />
                <Select
                    value={filterStatus}
                    onChange={setFilterStatus}
                    style={{ width: 120 }}
                >
                    <Option value="all">全部状态</Option>
                    <Option value="active">正常</Option>
                    <Option value="disabled">已禁用</Option>
                </Select>
            </div>

            {/* 用户表格 */}
            <Table
                columns={columns}
                dataSource={filteredUsers}
                rowKey="id"
                loading={loading}
                pagination={{
                    total: filteredUsers.length,
                    pageSize: 10,
                    showTotal: (total) => `共 ${total} 名用户`,
                }}
            />
        </div>
    );
};

export default UserManagement; 