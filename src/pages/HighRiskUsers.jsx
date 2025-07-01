import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Input, DatePicker, Card } from 'antd';
import { PhoneOutlined, MessageOutlined, ExclamationCircleOutlined, CheckOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';

const { TextArea } = Input;

const HighRiskUsers = () => {
    const [loading, setLoading] = useState(false);
    const [highRiskUsers, setHighRiskUsers] = useState([]);
    const [contactModalVisible, setContactModalVisible] = useState(false);
    const [currentUser, setCurrentUser] = useState(null);
    const [contactForm, setContactForm] = useState({
        method: '',
        notes: '',
        nextFollowUp: null
    });

    useEffect(() => {
        loadHighRiskUsers();
    }, []);

    const loadHighRiskUsers = async () => {
        setLoading(true);
        try {
            // TODO: 调用后端API获取高危用户列表
            // 这里使用模拟数据
            const mockUsers = [
                {
                    id: 1,
                    username: '张三',
                    email: 'zhangsan@example.com',
                    riskLevel: 'high',
                    riskScore: 85,
                    lastActivity: '2024-01-15 14:30',
                    riskFactors: ['情绪低落', '睡眠问题', '社交回避'],
                    lastContact: '2024-01-10 09:00',
                    contactStatus: 'pending',
                    priority: 'urgent'
                },
                {
                    id: 2,
                    username: '李四',
                    email: 'lisi@example.com',
                    riskLevel: 'high',
                    riskScore: 78,
                    lastActivity: '2024-01-14 16:45',
                    riskFactors: ['焦虑倾向', '压力过大'],
                    lastContact: '2024-01-12 14:30',
                    contactStatus: 'contacted',
                    priority: 'high'
                },
                {
                    id: 3,
                    username: '王五',
                    email: 'wangwu@example.com',
                    riskLevel: 'medium',
                    riskScore: 65,
                    lastActivity: '2024-01-15 10:20',
                    riskFactors: ['学业压力'],
                    lastContact: null,
                    contactStatus: 'pending',
                    priority: 'medium'
                }
            ];
            setHighRiskUsers(mockUsers);
        } catch (error) {
            message.error('获取高危用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    const showContactModal = (user) => {
        setCurrentUser(user);
        setContactModalVisible(true);
        setContactForm({
            method: '',
            notes: '',
            nextFollowUp: null
        });
    };

    const handleContact = async () => {
        if (!contactForm.method || !contactForm.notes) {
            message.error('请填写联系方式和回访记录');
            return;
        }

        try {
            // TODO: 调用后端API记录回访
            message.success('回访记录已保存');
            setContactModalVisible(false);
            loadHighRiskUsers();
        } catch (error) {
            message.error('保存回访记录失败');
        }
    };

    const markAsResolved = (userId) => {
        Modal.confirm({
            title: '确认标记为已处理？',
            content: '此操作将把该用户标记为已处理，请确认已完成必要的回访和干预。',
            onOk: async () => {
                try {
                    // TODO: 调用后端API
                    message.success('用户已标记为已处理');
                    loadHighRiskUsers();
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
        },
        {
            title: '风险等级',
            dataIndex: 'riskLevel',
            key: 'riskLevel',
            render: (level, record) => {
                const config = {
                    high: { color: 'red', text: '高风险' },
                    medium: { color: 'orange', text: '中风险' }
                };
                return (
                    <Space>
                        <Tag color={config[level]?.color}>
                            {config[level]?.text}
                        </Tag>
                        <span style={{ fontSize: '12px', color: '#666' }}>
                            ({record.riskScore}分)
                        </span>
                    </Space>
                );
            },
        },
        {
            title: '优先级',
            dataIndex: 'priority',
            key: 'priority',
            render: (priority) => {
                const config = {
                    urgent: { color: 'red', text: '紧急' },
                    high: { color: 'orange', text: '高' },
                    medium: { color: 'blue', text: '中' }
                };
                return (
                    <Tag color={config[priority]?.color}>
                        {config[priority]?.text}
                    </Tag>
                );
            },
        },
        {
            title: '风险因素',
            dataIndex: 'riskFactors',
            key: 'riskFactors',
            render: (factors) => (
                <div>
                    {factors?.map((factor, index) => (
                        <Tag key={index} size="small" color="volcano">
                            {factor}
                        </Tag>
                    ))}
                </div>
            ),
        },
        {
            title: '最后活动',
            dataIndex: 'lastActivity',
            key: 'lastActivity',
        },
        {
            title: '回访状态',
            dataIndex: 'contactStatus',
            key: 'contactStatus',
            render: (status) => {
                const config = {
                    pending: { color: 'red', text: '待回访' },
                    contacted: { color: 'green', text: '已联系' },
                    resolved: { color: 'blue', text: '已处理' }
                };
                return (
                    <Tag color={config[status]?.color}>
                        {config[status]?.text}
                    </Tag>
                );
            },
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        size="small" 
                        type="primary"
                        icon={<PhoneOutlined />}
                        onClick={() => showContactModal(record)}
                    >
                        回访
                    </Button>
                    {record.contactStatus !== 'resolved' && (
                        <Button 
                            size="small" 
                            icon={<CheckOutlined />}
                            onClick={() => markAsResolved(record.id)}
                        >
                            标记处理
                        </Button>
                    )}
                </Space>
            ),
        },
    ];

    // 按优先级排序
    const sortedUsers = [...highRiskUsers].sort((a, b) => {
        const priorityOrder = { urgent: 3, high: 2, medium: 1 };
        return priorityOrder[b.priority] - priorityOrder[a.priority];
    });

    const urgentCount = highRiskUsers.filter(u => u.priority === 'urgent').length;
    const pendingCount = highRiskUsers.filter(u => u.contactStatus === 'pending').length;

    return (
        <div style={{ padding: '24px' }}>
            <h1 style={{ marginBottom: '24px' }}>危险用户回访</h1>
            
            {/* 统计信息 */}
            <div style={{ marginBottom: '24px' }}>
                <Space size="large">
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#cf1322', fontSize: '24px', fontWeight: 'bold' }}>
                            {urgentCount}
                        </div>
                        <div>紧急关注</div>
                    </Card>
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#fa8c16', fontSize: '24px', fontWeight: 'bold' }}>
                            {pendingCount}
                        </div>
                        <div>待回访</div>
                    </Card>
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#1890ff', fontSize: '24px', fontWeight: 'bold' }}>
                            {highRiskUsers.length}
                        </div>
                        <div>总计</div>
                    </Card>
                </Space>
            </div>

            {urgentCount > 0 && (
                <div style={{ marginBottom: '16px' }}>
                    <Tag color="red" icon={<ExclamationCircleOutlined />}>
                        有 {urgentCount} 名用户需要紧急关注，请优先处理
                    </Tag>
                </div>
            )}

            {/* 用户表格 */}
            <Table
                columns={columns}
                dataSource={sortedUsers}
                rowKey="id"
                loading={loading}
                rowClassName={(record) => 
                    record.priority === 'urgent' ? 'urgent-row' : ''
                }
                pagination={{
                    pageSize: 10,
                    showTotal: (total) => `共 ${total} 名高危用户`,
                }}
            />

            {/* 回访记录模态框 */}
            <Modal
                title="用户回访记录"
                visible={contactModalVisible}
                onCancel={() => setContactModalVisible(false)}
                onOk={handleContact}
                width={600}
            >
                {currentUser && (
                    <div>
                        <Card size="small" style={{ marginBottom: '16px' }}>
                            <h4>{currentUser.username}</h4>
                            <p><strong>风险等级：</strong>
                                <Tag color="red">高风险</Tag>
                                ({currentUser.riskScore}分)
                            </p>
                            <p><strong>风险因素：</strong>
                                {currentUser.riskFactors.map((factor, index) => (
                                    <Tag key={index} size="small" color="volcano">
                                        {factor}
                                    </Tag>
                                ))}
                            </p>
                            <p><strong>最后活动：</strong>{currentUser.lastActivity}</p>
                        </Card>

                        <div style={{ marginBottom: '16px' }}>
                            <label style={{ display: 'block', marginBottom: '8px' }}>
                                联系方式：
                            </label>
                            <Space>
                                <Button 
                                    type={contactForm.method === 'phone' ? 'primary' : 'default'}
                                    onClick={() => setContactForm({...contactForm, method: 'phone'})}
                                >
                                    电话
                                </Button>
                                <Button 
                                    type={contactForm.method === 'message' ? 'primary' : 'default'}
                                    onClick={() => setContactForm({...contactForm, method: 'message'})}
                                >
                                    短信
                                </Button>
                                <Button 
                                    type={contactForm.method === 'email' ? 'primary' : 'default'}
                                    onClick={() => setContactForm({...contactForm, method: 'email'})}
                                >
                                    邮件
                                </Button>
                                <Button 
                                    type={contactForm.method === 'face' ? 'primary' : 'default'}
                                    onClick={() => setContactForm({...contactForm, method: 'face'})}
                                >
                                    面谈
                                </Button>
                            </Space>
                        </div>

                        <div style={{ marginBottom: '16px' }}>
                            <label style={{ display: 'block', marginBottom: '8px' }}>
                                回访记录：
                            </label>
                            <TextArea
                                rows={4}
                                value={contactForm.notes}
                                onChange={(e) => setContactForm({...contactForm, notes: e.target.value})}
                                placeholder="请详细记录回访情况、用户反馈、采取的措施等..."
                            />
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '8px' }}>
                                下次回访时间：
                            </label>
                            <DatePicker
                                showTime
                                value={contactForm.nextFollowUp}
                                onChange={(date) => setContactForm({...contactForm, nextFollowUp: date})}
                                placeholder="选择下次回访时间"
                            />
                        </div>
                    </div>
                )}
            </Modal>

            <style jsx>{`
                .urgent-row {
                    background-color: #fff1f0 !important;
                }
            `}</style>
        </div>
    );
};

export default HighRiskUsers; 