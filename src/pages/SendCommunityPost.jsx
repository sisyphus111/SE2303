import React, {useState} from 'react';
import {Button, Form, Input, Select, Space, Row, Col, Card, Typography} from 'antd';
import {tagOptions} from '../data/post_data';
import "../css/post.css"
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const {TextArea} = Input;
const {Title} = Typography;

const SendCommunityPostPage = () => {
    const [form] = Form.useForm();
    const [tags, setTags] = useState([]);
    const navigate = useNavigate();

    const onFinish = (values) => {
        console.log('Submitted post:', {...values, tags});
        form.resetFields();
        setTags([]);
    };

    return (
        <div className="send-post-center-wrapper">
            <div style={{ maxWidth: 900, margin: '0 auto', width: '100%' }}>
                <Button
                    type="link"
                    icon={<ArrowLeftOutlined />}
                    onClick={() => navigate('/community')}
                    style={{ margin: '16px 0' }}
                >
                    返回主页
                </Button>
            </div>
            <Card className="card-container">
                <div className="send-post-inner-box">
                    <Title level={3} style={{textAlign: 'center', marginBottom: '50px'}}>
                        分享一下你的心情吧
                    </Title>
                    <Form form={form} name="send_post" onFinish={onFinish} layout="vertical">
                        <Form.Item
                            name="title"
                            rules={[{required: true, message: '请输入帖子标题'}]}
                        >
                            <Input placeholder="帖子的主题是什么呢"/>
                        </Form.Item>

                        <Form.Item
                            name="tags"
                            rules={[{required: true, message: '请选择至少一个标签'}]}
                        >
                            <Select
                                mode="multiple"
                                placeholder="请选择标签"
                                value={tags}
                                onChange={setTags}
                                options={tagOptions}
                            />
                        </Form.Item>

                        <Form.Item
                            name="content"
                            rules={[{required: true, message: '请输入帖子内容'}]}
                        >
                            <TextArea rows={4} placeholder="输入你此刻的心情吧"/>
                        </Form.Item>

                        <Form.Item>
                            <Row justify="end">
                                <Col>
                                    <Button type="primary" htmlType="submit">
                                        发布
                                    </Button>
                                </Col>
                            </Row>
                        </Form.Item>
                    </Form>
                </div>
            </Card>
        </div>
    );
};

export default SendCommunityPostPage;