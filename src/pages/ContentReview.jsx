import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Tabs, Card } from 'antd';
import { CheckOutlined, CloseOutlined, EyeOutlined } from '@ant-design/icons';

const { TabPane } = Tabs;

const ContentReview = () => {
    const [loading, setLoading] = useState(false);
    const [pendingPosts, setPendingPosts] = useState([]);
    const [pendingComments, setPendingComments] = useState([]);
    const [reviewModalVisible, setReviewModalVisible] = useState(false);
    const [currentItem, setCurrentItem] = useState(null);

    useEffect(() => {
        loadPendingContent();
    }, []);

    const loadPendingContent = async () => {
        setLoading(true);
        try {
            // TODO: 调用后端API获取待审核内容
            // 这里使用模拟数据
            const mockPosts = [
                {
                    id: 1,
                    title: '关于压力的一些想法',
                    author: '张三',
                    content: '最近工作压力很大，想找个地方倾诉一下...',
                    type: 'post',
                    reportCount: 2,
                    submitTime: '2024-01-15 14:30',
                    category: '心情分享'
                },
                {
                    id: 2,
                    title: '如何应对焦虑情绪',
                    author: '李四',
                    content: '分享一些我应对焦虑的方法...',
                    type: 'post',
                    reportCount: 1,
                    submitTime: '2024-01-15 10:15',
                    category: '经验分享'
                }
            ];

            const mockComments = [
                {
                    id: 101,
                    content: '我也有同样的感受，我们可以互相支持',
                    author: '王五',
                    postTitle: '关于压力的一些想法',
                    type: 'comment',
                    reportCount: 1,
                    submitTime: '2024-01-15 15:00'
                }
            ];

            setPendingPosts(mockPosts);
            setPendingComments(mockComments);
        } catch (error) {
            message.error('获取待审核内容失败');
        } finally {
            setLoading(false);
        }
    };

    const handleReview = (item, action) => {
        const actionText = action === 'approve' ? '通过' : '拒绝';
        Modal.confirm({
            title: `确认${actionText}这条内容？`,
            content: `此操作将${actionText}该${item.type === 'post' ? '帖子' : '评论'}`,
            onOk: async () => {
                try {
                    // TODO: 调用后端API
                    message.success(`内容已${actionText}`);
                    loadPendingContent();
                } catch (error) {
                    message.error('操作失败');
                }
            }
        });
    };

    const showReviewModal = (item) => {
        setCurrentItem(item);
        setReviewModalVisible(true);
    };

    const postColumns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '标题',
            dataIndex: 'title',
            key: 'title',
            ellipsis: true,
        },
        {
            title: '作者',
            dataIndex: 'author',
            key: 'author',
        },
        {
            title: '分类',
            dataIndex: 'category',
            key: 'category',
            render: (category) => <Tag color="blue">{category}</Tag>
        },
        {
            title: '举报次数',
            dataIndex: 'reportCount',
            key: 'reportCount',
            render: (count) => (
                <Tag color={count > 1 ? 'red' : 'orange'}>
                    {count} 次
                </Tag>
            )
        },
        {
            title: '提交时间',
            dataIndex: 'submitTime',
            key: 'submitTime',
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        size="small" 
                        icon={<EyeOutlined />}
                        onClick={() => showReviewModal(record)}
                    >
                        查看
                    </Button>
                    <Button 
                        size="small" 
                        type="primary"
                        icon={<CheckOutlined />}
                        onClick={() => handleReview(record, 'approve')}
                    >
                        通过
                    </Button>
                    <Button 
                        size="small" 
                        danger
                        icon={<CloseOutlined />}
                        onClick={() => handleReview(record, 'reject')}
                    >
                        拒绝
                    </Button>
                </Space>
            ),
        },
    ];

    const commentColumns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '评论内容',
            dataIndex: 'content',
            key: 'content',
            ellipsis: true,
        },
        {
            title: '作者',
            dataIndex: 'author',
            key: 'author',
        },
        {
            title: '所属帖子',
            dataIndex: 'postTitle',
            key: 'postTitle',
            ellipsis: true,
        },
        {
            title: '举报次数',
            dataIndex: 'reportCount',
            key: 'reportCount',
            render: (count) => (
                <Tag color={count > 1 ? 'red' : 'orange'}>
                    {count} 次
                </Tag>
            )
        },
        {
            title: '提交时间',
            dataIndex: 'submitTime',
            key: 'submitTime',
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        size="small" 
                        icon={<EyeOutlined />}
                        onClick={() => showReviewModal(record)}
                    >
                        查看
                    </Button>
                    <Button 
                        size="small" 
                        type="primary"
                        icon={<CheckOutlined />}
                        onClick={() => handleReview(record, 'approve')}
                    >
                        通过
                    </Button>
                    <Button 
                        size="small" 
                        danger
                        icon={<CloseOutlined />}
                        onClick={() => handleReview(record, 'reject')}
                    >
                        拒绝
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ padding: '24px' }}>
            <h1 style={{ marginBottom: '24px' }}>内容审核</h1>
            
            <Tabs defaultActiveKey="posts">
                <TabPane tab={`待审核帖子 (${pendingPosts.length})`} key="posts">
                    <Table
                        columns={postColumns}
                        dataSource={pendingPosts}
                        rowKey="id"
                        loading={loading}
                        pagination={{
                            pageSize: 10,
                            showTotal: (total) => `共 ${total} 条待审核帖子`,
                        }}
                    />
                </TabPane>
                <TabPane tab={`待审核评论 (${pendingComments.length})`} key="comments">
                    <Table
                        columns={commentColumns}
                        dataSource={pendingComments}
                        rowKey="id"
                        loading={loading}
                        pagination={{
                            pageSize: 10,
                            showTotal: (total) => `共 ${total} 条待审核评论`,
                        }}
                    />
                </TabPane>
            </Tabs>

            {/* 内容详情模态框 */}
            <Modal
                title="内容详情"
                visible={reviewModalVisible}
                onCancel={() => setReviewModalVisible(false)}
                footer={[
                    <Button key="cancel" onClick={() => setReviewModalVisible(false)}>
                        关闭
                    </Button>,
                    <Button 
                        key="approve" 
                        type="primary"
                        onClick={() => {
                            handleReview(currentItem, 'approve');
                            setReviewModalVisible(false);
                        }}
                    >
                        通过
                    </Button>,
                    <Button 
                        key="reject" 
                        danger
                        onClick={() => {
                            handleReview(currentItem, 'reject');
                            setReviewModalVisible(false);
                        }}
                    >
                        拒绝
                    </Button>
                ]}
            >
                {currentItem && (
                    <div>
                        <Card size="small" style={{ marginBottom: '16px' }}>
                            <p><strong>作者：</strong>{currentItem.author}</p>
                            <p><strong>提交时间：</strong>{currentItem.submitTime}</p>
                            <p><strong>举报次数：</strong>{currentItem.reportCount}</p>
                            {currentItem.type === 'post' && (
                                <p><strong>标题：</strong>{currentItem.title}</p>
                            )}
                        </Card>
                        <Card title="内容" size="small">
                            <p>{currentItem.content}</p>
                        </Card>
                    </div>
                )}
            </Modal>
        </div>
    );
};

export default ContentReview; 