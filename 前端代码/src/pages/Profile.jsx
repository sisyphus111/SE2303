import React, { useState, useEffect } from "react";
import { 
  Typography, 
  Form, 
  Input, 
  Button, 
  Switch, 
  Radio, 
  Select, 
  Upload, 
  Avatar, 
  Row,
  Col,
  message,
  Card,
  Divider,
  Tag
} from "antd";
import {
  UserOutlined,
  UploadOutlined,
  GlobalOutlined,
  MobileOutlined,
  EyeInvisibleOutlined,
  EditOutlined,
  SaveOutlined,
  CameraOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import "../css/Profile.css";

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;

function Profile() {
  const { user, updateUser, isAuthenticated } = useUser();
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [form] = Form.useForm();

  // 初始化表单数据
  const [userData, setUserData] = useState({
    username: user?.username || '张三',
    email: user?.email || 'zhangsan@example.com',
    nickname: user?.nickname || '张三',
    phone: '10000000000',
    gender: '男',
    birthday: '1998-05-15',
    location: '北京市',
    bio: '在校园心理社区寻找内心的平静与成长',
    avatar: user?.avatar || null,
    tags: ['积极', '乐观', '善于倾听'],
    
    // 隐私设置
    profileVisibility: 'public',
    showOnlineStatus: true,
    allowDirectMessage: true,
    showEmail: false,
    showPhone: false,
  });

  const tagOptions = [
    { label: '积极', value: '积极' },
    { label: '乐观', value: '乐观' },
    { label: '耐心', value: '耐心' },
    { label: '细心', value: '细心' },
    { label: '善解人意', value: '善解人意' },
    { label: '善于倾听', value: '善于倾听' },
    { label: '热心助人', value: '热心助人' },
    { label: '温暖', value: '温暖' }
  ];

  useEffect(() => {
    if (user) {
      const updatedData = {
        ...userData,
        username: user.username || userData.username,
        email: user.email || userData.email,
        nickname: user.nickname || userData.nickname,
        avatar: user.avatar || userData.avatar,
      };
      setUserData(updatedData);
      form.setFieldsValue(updatedData);
    }
  }, [user]);

  const handleSave = async (values) => {
    setLoading(true);
    try {
      // 更新本地状态
      const updatedData = { ...userData, ...values };
      setUserData(updatedData);
      
      // 更新用户上下文
      await updateUser({
        username: values.username,
        nickname: values.nickname,
        email: values.email,
        avatar: updatedData.avatar,
      });
      
      message.success('个人资料保存成功！');
      setEditing(false);
    } catch (error) {
      message.error('保存失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleAvatarChange = (info) => {
    if (info.file.status === 'done' || info.file.status === 'uploading') {
      const reader = new FileReader();
      reader.onload = e => {
        const newAvatar = e.target.result;
        setUserData(prev => ({ ...prev, avatar: newAvatar }));
        updateUser({ avatar: newAvatar });
      };
      reader.readAsDataURL(info.file.originFileObj);
    }
  };

  const handlePrivacyChange = async (key, value) => {
    const updatedData = { ...userData, [key]: value };
    setUserData(updatedData);
    message.success('隐私设置已更新');
  };

  const getUserAvatar = () => {
    if (userData.avatar) {
      return userData.avatar;
    }
    const username = userData.username || userData.email || 'User';
    return username.charAt(0).toUpperCase();
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* 头部信息卡片 */}
        <Card className="profile-header-card">
          <div className="profile-header">
            <div className="avatar-section">
              <div className="avatar-container">
                <Avatar 
                  size={120} 
                  src={userData.avatar} 
                  icon={<UserOutlined />}
                  className="profile-avatar"
                >
                  {!userData.avatar && getUserAvatar()}
                </Avatar>
                <Upload 
                  showUploadList={false} 
                  beforeUpload={() => false} 
                  onChange={handleAvatarChange} 
                  accept="image/*"
                  className="avatar-upload"
                >
                  <Button 
                    type="primary" 
                    icon={<CameraOutlined />} 
                    className="avatar-upload-btn"
                    size="small"
                  />
                </Upload>
              </div>
            </div>
            
            <div className="profile-info">
              <div className="profile-name">
                <Title level={2} className="username">{userData.nickname || userData.username}</Title>
                <Text className="email" type="secondary">@{userData.username}</Text>
              </div>
              
              <div className="profile-bio">
                <Text>{userData.bio}</Text>
              </div>
              
              <div className="profile-tags">
                {userData.tags.map(tag => (
                  <Tag key={tag} color="green">{tag}</Tag>
                ))}
              </div>
              
              <div className="profile-actions">
                <Button 
                  type="primary" 
                  icon={editing ? <SaveOutlined /> : <EditOutlined />}
                  onClick={() => setEditing(!editing)}
                >
                  {editing ? '保存' : '编辑资料'}
                </Button>
              </div>
            </div>
          </div>
        </Card>

        <div className="profile-content">
          <Row gutter={24}>
            {/* 左侧：个人信息编辑 */}
            <Col span={16}>
              <Card title="个人信息" className="profile-card">
                <Form 
                  form={form}
                  layout="vertical" 
                  initialValues={userData} 
                  onFinish={handleSave}
                  disabled={!editing}
                >
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item 
                        label="用户名" 
                        name="username" 
                        rules={[{ required: true, message: '请输入用户名' }]}
                      >
                        <Input prefix={<UserOutlined />} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item 
                        label="昵称" 
                        name="nickname"
                      >
                        <Input prefix={<UserOutlined />} />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item 
                        label="邮箱（不会公开显示）" 
                        name="email" 
                        rules={[
                          { required: true, message: '请输入邮箱' }, 
                          { type: 'email', message: '邮箱格式不正确' }
                        ]}
                      >
                        <Input prefix={<GlobalOutlined />} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item 
                        label="手机号（不会公开显示）" 
                        name="phone"
                      >
                        <Input prefix={<MobileOutlined />} />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item label="性别" name="gender">
                        <Radio.Group>
                          <Radio value="男">男</Radio>
                          <Radio value="女">女</Radio>
                          <Radio value="保密">保密</Radio>
                        </Radio.Group>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item label="生日" name="birthday">
                        <Input type="date" />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Form.Item label="所在地" name="location">
                    <Input />
                  </Form.Item>

                  <Form.Item label="个人简介" name="bio">
                    <TextArea 
                      rows={4} 
                      placeholder="介绍一下自己，让其他用户更好地了解你" 
                      maxLength={200} 
                      showCount 
                    />
                  </Form.Item>

                  <Form.Item label="个人标签" name="tags">
                    <Select 
                      mode="multiple" 
                      allowClear 
                      placeholder="选择符合你的标签" 
                      options={tagOptions} 
                      maxTagCount={5} 
                    />
                  </Form.Item>

                  {editing && (
                    <Form.Item>
                      <Button 
                        type="primary" 
                        htmlType="submit" 
                        loading={loading}
                        size="large"
                      >
                        保存个人信息
                      </Button>
                      <Button 
                        style={{ marginLeft: 8 }}
                        onClick={() => {
                          setEditing(false);
                          form.resetFields();
                        }}
                      >
                        取消
                      </Button>
                    </Form.Item>
                  )}
                </Form>
              </Card>
            </Col>

            {/* 右侧：隐私设置 */}
            <Col span={8}>
              <Card 
                title={
                  <span>
                    <EyeInvisibleOutlined style={{ marginRight: 8 }} />
                    隐私设置
                  </span>
                } 
                className="profile-card"
              >
                <div className="privacy-settings">
                  <div className="privacy-item">
                    <div className="privacy-info">
                      <div className="privacy-title">个人资料可见性</div>
                      <div className="privacy-desc">控制其他用户对您个人资料的查看权限</div>
                    </div>
                    <Select 
                      value={userData.profileVisibility}
                      onChange={(value) => handlePrivacyChange('profileVisibility', value)}
                      style={{ width: '100%' }}
                    >
                      <Option value="public">公开</Option>
                      <Option value="private">私密</Option>
                    </Select>
                  </div>

                  <Divider />

                </div>
              </Card>

              {/* 账户信息卡片 */}
              <Card title="账户信息" className="profile-card" style={{ marginTop: 16 }}>
                <div className="account-info">
                  <div className="info-item">
                    <Text type="secondary">注册时间</Text>
                    <Text>{user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '2024-01-01'}</Text>
                  </div>
                  <div className="info-item">
                    <Text type="secondary">用户ID</Text>
                    <Text>{user?.id || 'demo-user'}</Text>
                  </div>
                  <div className="info-item">
                    <Text type="secondary">账户状态</Text>
                    <Tag color="green">正常</Tag>
                  </div>
                </div>
              </Card>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
}

export default Profile; 