import React, {useState} from 'react';
import {Button, Form, Input, Select, Space, Row, Col, Card, Typography} from 'antd';
import {tagOptions} from '../data/post_data';
import "../css/post.css"
import CommunityHeader from "../components/CommunityHeader";
import Layout from "../components/Layout";
import { useNavigate } from 'react-router-dom';

// API服务导入注释
// import { createPost } from '../service/communityService';
import { ArrowLeftOutlined } from '@ant-design/icons';

const {TextArea} = Input;
const {Title} = Typography;

const SendCommunityPostPage = () => {
    const [form] = Form.useForm();
    const [tags, setTags] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onFinish = async (values) => {
        setLoading(true);
        
        // API调用注释：创建帖子
        // try {
        //   const postData = {
        //     title: values.title,
        //     content: values.content,
        //     tags: tags,
        //     category: '心情分享', // 可以根据标签或用户选择确定分类
        //     author: '当前用户', // 从用户状态获取
        //     authorAvatar: '👤' // 从用户状态获取
        //   };
        //   
        //   const response = await createPost(postData);
        //   console.log('帖子发布成功:', response.data);
        //   alert('帖子发布成功！');
        //   form.resetFields();
        //   setTags([]);
        //   navigate('/community'); // 跳转到社区页面
        // } catch (error) {
        //   console.error('发布帖子失败:', error);
        //   alert('发布失败，请重试');
        // } finally {
        //   setLoading(false);
        // }

        console.log('Submitted post:', {...values, tags});
        form.resetFields();
        setTags([]);
        setLoading(false);
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
                                    <Button 
                                        type="primary" 
                                        htmlType="submit"
                                        loading={loading}
                                    >
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