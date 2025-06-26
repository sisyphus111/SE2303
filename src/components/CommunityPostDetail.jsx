import React, { useState } from 'react';
import { Avatar, Typography, Row, Col, Button, Space, Divider, Tag } from 'antd';
import { HeartOutlined, HeartFilled, ShareAltOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import { useNavigate } from 'react-router-dom';
import '../css/CommunityPostDetail.css';
const { Title, Text, Paragraph } = Typography;

// 侧边栏所有标签
const allTags = ['全部', '情感', '学习', '生活', '健康', '成长'];

const PostDetail = ({ postData }) => {
    const navigate = useNavigate();
    const [liked, setLiked] = useState(false);

    const handleLike = () => {
        setLiked(l => !l);
    };

    return (
        <div style={{ position: 'relative', width: '100%' }}>
            {/* <div className="community-post-detail-backhome-wrapper">
                <Button
                    type="link"
                    className="community-post-detail-backhome-btn"
                    onClick={() => navigate('/community')}
                >
                    返回社区主页
                </Button>
            </div> */}
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
                            {postData.tags.map(tag => (
                                allTags.includes(tag) ? (
                                    <Tag
                                        key={tag}
                                        style={{ cursor: 'pointer' }}
                                        // onClick={() => {
                                        //     navigate(`/community?tag=${encodeURIComponent(tag)}`);
                                        // }}
                                    >
                                        {tag}
                                    </Tag>
                                ) : (
                                    <Tag key={tag}>{tag}</Tag>
                                )
                            ))}
                        </Space>
                    </Col>
                    <Col>
                      <div style={{display:'flex',flexDirection:'column',alignItems:'center',gap:4}}>
                        <svg className="community-post-detail-banner-illu" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <circle cx="40" cy="40" r="18" fill="#FFD93B"/>
                          <g stroke="#FFD93B" strokeWidth="4" strokeLinecap="round">
                            <line x1="40" y1="8" x2="40" y2="22"/>
                            <line x1="40" y1="58" x2="40" y2="72"/>
                            <line x1="8" y1="40" x2="22" y2="40"/>
                            <line x1="58" y1="40" x2="72" y2="40"/>
                            <line x1="17.6" y1="17.6" x2="27.7" y2="27.7"/>
                            <line x1="52.3" y1="52.3" x2="62.4" y2="62.4"/>
                            <line x1="17.6" y1="62.4" x2="27.7" y2="52.3"/>
                            <line x1="52.3" y1="27.7" x2="62.4" y2="17.6"/>
                          </g>
                        </svg>
                        <Text type="secondary" className="community-post-detail-time">{postData.time}</Text>
                      </div>
                    </Col>
                </Row>
                <Divider />
                <Row className="community-post-detail-user">
                    <Col>
                        <Space>
                            <Avatar src={postData.user.avatar} />
                            <span className="community-post-detail-user-name">{postData.user.name}</span>
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
                        <Text type="secondary">(浏览量: {postData.views}) 点赞数: {postData.likes + (liked ? 1 : 0)}</Text>
                    </Col>
                    <Col>
                        <Space>
                            <Button
                                icon={liked ? <HeartFilled /> : <HeartOutlined />} 
                                className={`community-post-detail-like${liked ? ' liked' : ''}`}
                                onClick={handleLike}
                            >
                                {liked ? '已点赞' : '点赞'}
                            </Button>
                            <Button icon={<ShareAltOutlined />} className="community-post-detail-share">分享</Button>
                        </Space>
                    </Col>
                </Row>
            </div>
        </div>
    );
};

export default PostDetail;