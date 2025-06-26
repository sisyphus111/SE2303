import React from 'react';
import { Avatar, Tag, Typography, Row, Col, Button, Space, Divider } from 'antd';
import { HeartOutlined, ShareAltOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import { useNavigate } from 'react-router-dom';
import '../css/CommunityPostDetail.css';
const { Title, Text, Paragraph } = Typography;

const PostDetail = ({ postData }) => {
    const navigate = useNavigate();
    return (
        <div className="community-post-detail-container">
            <Button
                type="link"
                icon={<ArrowLeftOutlined />}
                onClick={() => navigate(-1)}
                className="community-post-detail-back"
            >
                返回
            </Button>
            <Row justify="space-between" align="middle" className="community-post-detail-header">
                <Col>
                    <Title level={2} className="community-post-detail-title">{postData.title}</Title>
                    <Space size="small" className="community-post-detail-tags">
                        {postData.tags.map(tag => <Tag color="blue" key={tag}>{tag}</Tag>)}
                    </Space>
                </Col>
                <Col><Text type="secondary" className="community-post-detail-time">{postData.time}</Text></Col>
            </Row>
            <Divider />
            <Row className="community-post-detail-user">
                <Col>
                    <Space>
                        <Avatar src={postData.user.avatar} />
                        <Text style={{ fontSize: '16px' }}>{postData.user.name}</Text>
                    </Space>
                </Col>
            </Row>
            <Row className="community-post-detail-content">
                <Col>
                    <Paragraph>{postData.content}</Paragraph>
                </Col>
            </Row>
            <Divider />
            <Row justify="space-between" align="middle" className="community-post-detail-footer">
                <Col>
                    <Text type="secondary">(浏览量: {postData.views}) 点赞数: {postData.likes}</Text>
                </Col>
                <Col>
                    <Space>
                        <Button icon={<HeartOutlined />} className="community-post-detail-like">点赞</Button>
                        <Button icon={<ShareAltOutlined />} className="community-post-detail-share">分享</Button>
                    </Space>
                </Col>
            </Row>
        </div>
    );
};

export default PostDetail;