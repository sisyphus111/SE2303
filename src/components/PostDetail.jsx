import React from 'react';
import { Avatar, Tag, Typography, Row, Col, Button, Space, Divider } from 'antd';
import { HeartOutlined, ShareAltOutlined } from "@ant-design/icons";
const { Title, Text, Paragraph } = Typography;

const PostDetail = ({ postData }) => {
    return (
        <div style={{ padding: '20px' }}>
            <Row justify="space-between" align="middle" style={{ background: '#f0f0f0', padding: '10px', borderRadius: '4px' }}>
                <Col>
                    <Title level={2}>{postData.title}</Title>
                    <Space size="small">
                        {postData.tags.map(tag => <Tag color="blue" key={tag}>{tag}</Tag>)}
                    </Space>
                </Col>
                <Col><Text type="secondary">{postData.time}</Text></Col>
            </Row>
            <Divider />
            <Row style={{ marginTop: '20px' }}>
                <Col>
                    <Space>
                        <Avatar src={postData.user.avatar} />
                        <Text style={{ fontSize: '16px' }}>{postData.user.name}</Text>
                    </Space>
                </Col>
            </Row>
            <Row style={{ marginTop: '20px' }}>
                <Col>
                    <Paragraph style={{ fontSize: '16px', color: 'black' }}>{postData.content}</Paragraph>
                </Col>
            </Row>
            <Divider />
            <Row justify="space-between" align="middle" style={{ marginTop: '20px' }}>
                <Col>
                    <Text type="secondary">(浏览量: {postData.views}) 点赞数: {postData.likes}</Text>
                </Col>
                <Col>
                    <Space>
                        <Button icon={<HeartOutlined />} style={{ marginRight: '10px', background: '#fff0f6', color: '#eb2f96' }}>点赞</Button>
                        <Button icon={<ShareAltOutlined />} style={{ background: '#eb2f96', borderColor: '#eb2f96', color: 'white' }}>分享</Button>
                    </Space>
                </Col>
            </Row>
        </div>
    );
};

export default PostDetail;