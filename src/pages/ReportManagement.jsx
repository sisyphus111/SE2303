import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Tabs, Select, Input, Card, Descriptions } from 'antd';
import { 
    EyeOutlined, 
    CheckOutlined, 
    CloseOutlined, 
    WarningOutlined,
    UserOutlined,
    MessageOutlined,
    SearchOutlined 
} from '@ant-design/icons';

const { TabPane } = Tabs;
const { Option } = Select;
const { Search } = Input;

const ReportManagement = () => {
    const [loading, setLoading] = useState(false);
    const [reports, setReports] = useState([]);
    const [filteredReports, setFilteredReports] = useState([]);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [currentReport, setCurrentReport] = useState(null);
    const [filterStatus, setFilterStatus] = useState('all');
    const [filterType, setFilterType] = useState('all');
    const [searchText, setSearchText] = useState('');

    useEffect(() => {
        loadReports();
    }, []);

    useEffect(() => {
        // 应用筛选
        let filtered = reports;
        
        if (filterStatus !== 'all') {
            filtered = filtered.filter(report => report.status === filterStatus);
        }
        
        if (filterType !== 'all') {
            filtered = filtered.filter(report => report.contentType === filterType);
        }
        
        if (searchText) {
            filtered = filtered.filter(report => 
                report.reportedContent.toLowerCase().includes(searchText.toLowerCase()) ||
                report.reportedUser.toLowerCase().includes(searchText.toLowerCase()) ||
                report.reporter.toLowerCase().includes(searchText.toLowerCase())
            );
        }
        
        setFilteredReports(filtered);
    }, [reports, filterStatus, filterType, searchText]);

    const loadReports = async () => {
        setLoading(true);
        try {
            // TODO: 调用后端API获取举报列表
            // 这里使用模拟数据
            const mockReports = [
                {
                    id: 1,
                    type: 'inappropriate_content',
                    reason: '不当内容',
                    contentType: 'post',
                    contentId: 101,
                    reportedContent: '这是一条可能不当的帖子内容...',
                    reportedUser: '张三',
                    reporter: '李四',
                    reportTime: '2024-01-15 14:30',
                    status: 'pending',
                    priority: 'medium',
                    description: '用户发布了不当言论，可能影响社区氛围'
                },
                {
                    id: 2,
                    type: 'harassment',
                    reason: '骚扰他人',
                    contentType: 'comment',
                    contentId: 201,
                    reportedContent: '这是一条评论内容...',
                    reportedUser: '王五',
                    reporter: '赵六',
                    reportTime: '2024-01-15 16:20',
                    status: 'pending',
                    priority: 'high',
                    description: '该用户在评论中骚扰其他用户'
                },
                {
                    id: 3,
                    type: 'spam',
                    reason: '垃圾信息',
                    contentType: 'post',
                    contentId: 102,
                    reportedContent: '广告内容或垃圾信息...',
                    reportedUser: '刘七',
                    reporter: '陈八',
                    reportTime: '2024-01-14 10:15',
                    status: 'resolved',
                    priority: 'low',
                    description: '发布垃圾广告信息',
                    resolution: '已删除相关内容并警告用户'
                }
            ];
            setReports(mockReports);
        } catch (error) {
            message.error('获取举报列表失败');
        } finally {
            setLoading(false);
        }
    };

    const showDetailModal = (report) => {
        setCurrentReport(report);
        setDetailModalVisible(true);
    };

    const handleReport = (reportId, action) => {
        const actionText = action === 'accept' ? '处理' : '驳回';
        Modal.confirm({
            title: `确认${actionText}这条举报？`,
            content: `此操作将${actionText}该举报`,
            onOk: async () => {
                try {
                    // TODO: 调用后端API
                    message.success(`举报已${actionText}`);
                    loadReports();
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
            title: '举报类型',
            dataIndex: 'reason',
            key: 'reason',
            render: (reason, record) => (
                <Space direction="vertical" size="small">
                    <Tag color="orange">{reason}</Tag>
                    <span style={{ fontSize: '12px', color: '#666' }}>
                        {record.contentType === 'post' ? '帖子' : '评论'}
                    </span>
                </Space>
            )
        },
        {
            title: '被举报用户',
            dataIndex: 'reportedUser',
            key: 'reportedUser',
            render: (user) => (
                <Space>
                    <UserOutlined />
                    {user}
                </Space>
            )
        },
        {
            title: '举报人',
            dataIndex: 'reporter',
            key: 'reporter',
        },
        {
            title: '优先级',
            dataIndex: 'priority',
            key: 'priority',
            render: (priority) => {
                const config = {
                    high: { color: 'red', text: '高' },
                    medium: { color: 'orange', text: '中' },
                    low: { color: 'blue', text: '低' }
                };
                return (
                    <Tag color={config[priority]?.color}>
                        {config[priority]?.text}
                    </Tag>
                );
            },
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => {
                const config = {
                    pending: { color: 'orange', text: '待处理' },
                    processing: { color: 'blue', text: '处理中' },
                    resolved: { color: 'green', text: '已处理' },
                    rejected: { color: 'gray', text: '已驳回' }
                };
                return (
                    <Tag color={config[status]?.color}>
                        {config[status]?.text}
                    </Tag>
                );
            },
        },
        {
            title: '举报时间',
            dataIndex: 'reportTime',
            key: 'reportTime',
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        size="small" 
                        icon={<EyeOutlined />}
                        onClick={() => showDetailModal(record)}
                    >
                        查看
                    </Button>
                    {record.status === 'pending' && (
                        <>
                            <Button 
                                size="small" 
                                type="primary"
                                icon={<CheckOutlined />}
                                onClick={() => handleReport(record.id, 'accept')}
                            >
                                处理
                            </Button>
                            <Button 
                                size="small" 
                                danger
                                icon={<CloseOutlined />}
                                onClick={() => handleReport(record.id, 'reject')}
                            >
                                驳回
                            </Button>
                        </>
                    )}
                </Space>
            ),
        },
    ];

    // 按优先级和时间排序
    const sortedReports = [...filteredReports].sort((a, b) => {
        const priorityOrder = { high: 3, medium: 2, low: 1 };
        if (a.priority !== b.priority) {
            return priorityOrder[b.priority] - priorityOrder[a.priority];
        }
        return new Date(b.reportTime) - new Date(a.reportTime);
    });

    const pendingCount = reports.filter(r => r.status === 'pending').length;
    const highPriorityCount = reports.filter(r => r.priority === 'high' && r.status === 'pending').length;

    return (
        <div style={{ padding: '24px' }}>
            <h1 style={{ marginBottom: '24px' }}>举报管理</h1>
            
            {/* 统计信息 */}
            <div style={{ marginBottom: '24px' }}>
                <Space size="large">
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#fa8c16', fontSize: '24px', fontWeight: 'bold' }}>
                            {pendingCount}
                        </div>
                        <div>待处理举报</div>
                    </Card>
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#cf1322', fontSize: '24px', fontWeight: 'bold' }}>
                            {highPriorityCount}
                        </div>
                        <div>高优先级</div>
                    </Card>
                    <Card size="small" style={{ textAlign: 'center' }}>
                        <div style={{ color: '#1890ff', fontSize: '24px', fontWeight: 'bold' }}>
                            {reports.length}
                        </div>
                        <div>总举报数</div>
                    </Card>
                </Space>
            </div>

            {highPriorityCount > 0 && (
                <div style={{ marginBottom: '16px' }}>
                    <Tag color="red" icon={<WarningOutlined />}>
                        有 {highPriorityCount} 条高优先级举报需要优先处理
                    </Tag>
                </div>
            )}

            {/* 搜索和筛选 */}
            <div style={{ marginBottom: '16px', display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                <Search
                    placeholder="搜索举报内容、用户名"
                    allowClear
                    onSearch={setSearchText}
                    style={{ width: 300 }}
                    prefix={<SearchOutlined />}
                />
                <Select
                    value={filterStatus}
                    onChange={setFilterStatus}
                    style={{ width: 120 }}
                    placeholder="状态筛选"
                >
                    <Option value="all">全部状态</Option>
                    <Option value="pending">待处理</Option>
                    <Option value="processing">处理中</Option>
                    <Option value="resolved">已处理</Option>
                    <Option value="rejected">已驳回</Option>
                </Select>
                <Select
                    value={filterType}
                    onChange={setFilterType}
                    style={{ width: 120 }}
                    placeholder="内容类型"
                >
                    <Option value="all">全部类型</Option>
                    <Option value="post">帖子</Option>
                    <Option value="comment">评论</Option>
                </Select>
            </div>

            {/* 举报表格 */}
            <Table
                columns={columns}
                dataSource={sortedReports}
                rowKey="id"
                loading={loading}
                rowClassName={(record) => 
                    record.priority === 'high' && record.status === 'pending' ? 'urgent-row' : ''
                }
                pagination={{
                    pageSize: 10,
                    showTotal: (total) => `共 ${total} 条举报`,
                }}
            />

            {/* 举报详情模态框 */}
            <Modal
                title="举报详情"
                visible={detailModalVisible}
                onCancel={() => setDetailModalVisible(false)}
                footer={[
                    <Button key="close" onClick={() => setDetailModalVisible(false)}>
                        关闭
                    </Button>,
                    currentReport?.status === 'pending' && [
                        <Button 
                            key="accept" 
                            type="primary"
                            onClick={() => {
                                handleReport(currentReport.id, 'accept');
                                setDetailModalVisible(false);
                            }}
                        >
                            处理举报
                        </Button>,
                        <Button 
                            key="reject" 
                            danger
                            onClick={() => {
                                handleReport(currentReport.id, 'reject');
                                setDetailModalVisible(false);
                            }}
                        >
                            驳回举报
                        </Button>
                    ]
                ]}
                width={700}
            >
                {currentReport && (
                    <div>
                        <Descriptions column={2} size="small" bordered>
                            <Descriptions.Item label="举报ID">{currentReport.id}</Descriptions.Item>
                            <Descriptions.Item label="举报时间">{currentReport.reportTime}</Descriptions.Item>
                            <Descriptions.Item label="举报类型">
                                <Tag color="orange">{currentReport.reason}</Tag>
                            </Descriptions.Item>
                            <Descriptions.Item label="优先级">
                                <Tag color={
                                    currentReport.priority === 'high' ? 'red' : 
                                    currentReport.priority === 'medium' ? 'orange' : 'blue'
                                }>
                                    {currentReport.priority === 'high' ? '高' : 
                                     currentReport.priority === 'medium' ? '中' : '低'}
                                </Tag>
                            </Descriptions.Item>
                            <Descriptions.Item label="被举报用户">{currentReport.reportedUser}</Descriptions.Item>
                            <Descriptions.Item label="举报人">{currentReport.reporter}</Descriptions.Item>
                            <Descriptions.Item label="内容类型">
                                {currentReport.contentType === 'post' ? '帖子' : '评论'}
                            </Descriptions.Item>
                            <Descriptions.Item label="当前状态">
                                <Tag color={
                                    currentReport.status === 'pending' ? 'orange' :
                                    currentReport.status === 'resolved' ? 'green' : 'gray'
                                }>
                                    {currentReport.status === 'pending' ? '待处理' :
                                     currentReport.status === 'resolved' ? '已处理' : '已驳回'}
                                </Tag>
                            </Descriptions.Item>
                        </Descriptions>

                        <div style={{ marginTop: '16px' }}>
                            <h4>举报描述：</h4>
                            <Card size="small" style={{ background: '#fafafa' }}>
                                {currentReport.description}
                            </Card>
                        </div>

                        <div style={{ marginTop: '16px' }}>
                            <h4>被举报内容：</h4>
                            <Card size="small" style={{ background: '#fff2f0', border: '1px solid #ffccc7' }}>
                                {currentReport.reportedContent}
                            </Card>
                        </div>

                        {currentReport.resolution && (
                            <div style={{ marginTop: '16px' }}>
                                <h4>处理结果：</h4>
                                <Card size="small" style={{ background: '#f6ffed', border: '1px solid #b7eb8f' }}>
                                    {currentReport.resolution}
                                </Card>
                            </div>
                        )}
                    </div>
                )}
            </Modal>
        </div>
    );
};

export default ReportManagement; 