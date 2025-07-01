import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Alert } from 'antd';
import { 
  UserOutlined, 
  MessageOutlined, 
  FlagOutlined, 
  ExclamationCircleOutlined 
} from '@ant-design/icons';

const AdminDashboard = () => {
    const [stats, setStats] = useState({
        totalUsers: 0,
        pendingReviews: 0,
        highRiskUsers: 0,
        todayActivities: 0
    });

    useEffect(() => {
        // TODO: 从后端API获取统计数据
        // 这里使用模拟数据
        setStats({
            totalUsers: 1234,
            pendingReviews: 15,
            highRiskUsers: 3,
            todayActivities: 89
        });
    }, []);

    return (
        <div style={{ padding: '24px' }}>
            <h1 style={{ marginBottom: '24px' }}>管理面板</h1>
            
            {/* 重要提醒 */}
            {stats.highRiskUsers > 0 && (
                <Alert
                    message="重要提醒"
                    description={`当前有 ${stats.highRiskUsers} 名高危用户需要关注和回访，请及时处理。`}
                    type="warning"
                    showIcon
                    style={{ marginBottom: '24px' }}
                />
            )}

            {/* 统计数据卡片 */}
            <Row gutter={16} style={{ marginBottom: '24px' }}>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="总用户数"
                            value={stats.totalUsers}
                            prefix={<UserOutlined />}
                            valueStyle={{ color: '#3f8600' }}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="待审核内容"
                            value={stats.pendingReviews}
                            prefix={<FlagOutlined />}
                            valueStyle={{ color: '#1890ff' }}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="高危用户"
                            value={stats.highRiskUsers}
                            prefix={<ExclamationCircleOutlined />}
                            valueStyle={{ color: '#cf1322' }}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="今日活跃度"
                            value={stats.todayActivities}
                            prefix={<MessageOutlined />}
                            valueStyle={{ color: '#722ed1' }}
                        />
                    </Card>
                </Col>
            </Row>

            {/* 快速操作 */}
            <Row gutter={16}>
                <Col span={12}>
                    <Card title="最近活动" size="small">
                        <div style={{ color: '#666' }}>
                            <p>• 用户张三发布了新的心情日记</p>
                            <p>• 社区帖子"如何缓解焦虑"收到举报</p>
                            <p>• 用户李四的心理健康评分降低</p>
                            <p>• 新增3名用户注册</p>
                        </div>
                    </Card>
                </Col>
                <Col span={12}>
                    <Card title="系统状态" size="small">
                        <div style={{ color: '#666' }}>
                            <p>• 所有服务运行正常</p>
                            <p>• 数据库连接稳定</p>
                            <p>• AI服务响应正常</p>
                            <p>• 最后更新：刚刚</p>
                        </div>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default AdminDashboard; 