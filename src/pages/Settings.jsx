import React, { useState } from "react";
import { 
  Typography, 
  Tabs, 
  Form, 
  Input, 
  Button, 
  Switch, 
  Radio, 
  Select, 
  Upload, 
  Avatar, 
  Divider,
  Row,
  Col,
  Alert,
  Modal,
  message
} from "antd";
import {
  UserOutlined,
  SafetyOutlined,
  EyeInvisibleOutlined,
  SettingOutlined,
  HeartOutlined,
  TeamOutlined,
  DatabaseOutlined,
  UploadOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  BellOutlined,
  LockOutlined,
  GlobalOutlined,
  MobileOutlined
} from '@ant-design/icons';
import "../css/Settings.css";

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { confirm } = Modal;

function Settings() {
  const [activeTab, setActiveTab] = useState('profile');
  const [loading, setLoading] = useState(false);
  const [userData, setUserData] = useState({
    username: '张三',
    email: 'zhangsan@example.com',
    phone: '10000000000',
    gender: '男',
    birthday: '1998-05-15',
    location: '北京市',
    bio: '在校园心理社区寻找内心的平静与成长',
    avatar: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=120&q=80',
    tags: ['积极', '乐观', '善于倾听'],
    
    // 隐私设置
    profileVisibility: 'public',
    showOnlineStatus: true,
    allowDirectMessage: true,
    showEmail: false,
    showPhone: false,
    postVisibility: 'public',
    
    // 通知设置
    emailNotifications: true,
    pushNotifications: true,
    commentNotifications: true,
    likeNotifications: false,
    mentionNotifications: true,
    systemNotifications: true,
    
    // 个人偏好
    theme: 'light',
    language: 'zh-CN',
    fontSize: 'medium',
    autoPlayMedia: false,
    
    // 心理健康设置
    crisisInterventionEnabled: true,
    moodTrackingEnabled: true,
    anonymousPosting: false,
    mentalHealthDataSharing: false,
    
    // 社区交互设置
    autoFollow: false,
    showPostStats: true,
    enablePostRecommendation: true,
    blockSensitiveContent: true
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

  const handleSave = async (section, values) => {
    setLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      setUserData(prev => ({ ...prev, ...values }));
      message.success('设置保存成功！');
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
        setUserData(prev => ({ ...prev, avatar: e.target.result }));
      };
      reader.readAsDataURL(info.file.originFileObj);
    }
  };

  const handleDeleteAccount = () => {
    confirm({
      title: '确认删除账户',
      icon: <ExclamationCircleOutlined />,
      content: '删除账户将永久删除您的所有数据，此操作不可恢复。',
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        message.success('账户删除请求已提交');
      },
    });
  };

  // 个人信息设置
  const ProfileSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <UserOutlined className="section-icon" />
        <div>
          <h3>个人信息</h3>
          <p>管理您的个人资料和基本信息</p>
        </div>
      </div>
      
      <Form layout="vertical" initialValues={userData} onFinish={(values) => handleSave('profile', values)}>
        <Row gutter={24}>
          <Col span={24}>
            <Form.Item label="头像">
              <div className="avatar-upload-section">
                <Avatar size={80} src={userData.avatar} icon={<UserOutlined />} />
                <div className="avatar-upload-info">
                  <Upload showUploadList={false} beforeUpload={() => false} onChange={handleAvatarChange} accept="image/*">
                    <Button icon={<UploadOutlined />}>更换头像</Button>
                  </Upload>
                  <Text type="secondary">支持 JPG、PNG 格式，不超过 2MB</Text>
                </div>
              </div>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input prefix={<UserOutlined />} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="性别" name="gender">
              <Radio.Group>
                <Radio value="男">男</Radio>
                <Radio value="女">女</Radio>
                <Radio value="保密">保密</Radio>
              </Radio.Group>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="邮箱" name="email" rules={[{ required: true, message: '请输入邮箱' }, { type: 'email', message: '邮箱格式不正确' }]}>
              <Input prefix={<GlobalOutlined />} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="手机号" name="phone" rules={[{ required: true, message: '请输入手机号' }]}>
              <Input prefix={<MobileOutlined />} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="生日" name="birthday">
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="所在地" name="location">
              <Input />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item label="个人简介" name="bio">
          <TextArea rows={4} placeholder="介绍一下自己，让其他用户更好地了解你" maxLength={200} showCount />
        </Form.Item>

        <Form.Item label="个人标签" name="tags">
          <Select mode="multiple" allowClear placeholder="选择符合你的标签" options={tagOptions} maxTagCount={5} />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>保存个人信息</Button>
        </Form.Item>
      </Form>
    </div>
  );

  // 隐私设置
  const PrivacySettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <EyeInvisibleOutlined className="section-icon" />
        <div>
          <h3>隐私设置</h3>
          <p>控制您的信息对其他用户的可见性</p>
        </div>
      </div>

      <Form layout="vertical" initialValues={userData} onFinish={(values) => handleSave('privacy', values)}>
        <div className="privacy-group">
          <h4>个人资料可见性</h4>
          <Form.Item name="profileVisibility">
            <Radio.Group>
              <Radio value="public">公开 - 所有人可见</Radio>
              <Radio value="friends">好友 - 仅好友可见</Radio>
              <Radio value="private">私密 - 仅自己可见</Radio>
            </Radio.Group>
          </Form.Item>
        </div>

        <div className="privacy-group">
          <h4>联系方式可见性</h4>
          <div className="privacy-switches">
            <div className="privacy-switch-item">
              <span>显示邮箱地址</span>
              <Switch checked={userData.showEmail} onChange={(checked) => setUserData(prev => ({...prev, showEmail: checked}))} />
            </div>
            <div className="privacy-switch-item">
              <span>显示手机号码</span>
              <Switch checked={userData.showPhone} onChange={(checked) => setUserData(prev => ({...prev, showPhone: checked}))} />
            </div>
            <div className="privacy-switch-item">
              <span>显示在线状态</span>
              <Switch checked={userData.showOnlineStatus} onChange={(checked) => setUserData(prev => ({...prev, showOnlineStatus: checked}))} />
            </div>
          </div>
        </div>

        <div className="privacy-group">
          <h4>社交互动</h4>
          <div className="privacy-switches">
            <div className="privacy-switch-item">
              <span>允许陌生人私信</span>
              <Switch checked={userData.allowDirectMessage} onChange={(checked) => setUserData(prev => ({...prev, allowDirectMessage: checked}))} />
            </div>
          </div>
        </div>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>保存隐私设置</Button>
        </Form.Item>
      </Form>
    </div>
  );

  // 心理健康设置
  const MentalHealthSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <HeartOutlined className="section-icon" />
        <div>
          <h3>心理健康设置</h3>
          <p>管理您的心理健康相关功能</p>
        </div>
      </div>

      <Alert
        message="重要提醒"
        description="这些设置关系到您的心理健康数据安全和隐私保护，请仔细选择。"
        type="info"
        showIcon
        style={{ marginBottom: 24 }}
      />

      <div className="mental-health-switches">
        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <CheckCircleOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>危机干预功能</strong>
            </div>
            <span>当检测到心理危机信号时，系统将主动提供帮助资源</span>
          </div>
          <Switch checked={userData.crisisInterventionEnabled} onChange={(checked) => setUserData(prev => ({...prev, crisisInterventionEnabled: checked}))} />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <HeartOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>情绪追踪</strong>
            </div>
            <span>允许系统记录和分析您的情绪数据，提供个性化建议</span>
          </div>
          <Switch checked={userData.moodTrackingEnabled} onChange={(checked) => setUserData(prev => ({...prev, moodTrackingEnabled: checked}))} />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <EyeInvisibleOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>匿名发帖</strong>
            </div>
            <span>允许您匿名发布敏感的心理健康相关内容</span>
          </div>
          <Switch checked={userData.anonymousPosting} onChange={(checked) => setUserData(prev => ({...prev, anonymousPosting: checked}))} />
        </div>
      </div>

      <Button type="primary" onClick={() => handleSave('mentalHealth', userData)} loading={loading} style={{ marginTop: 24 }}>
        保存心理健康设置
      </Button>
    </div>
  );

  // 数据管理
  const DataManagement = () => (
    <div className="settings-section">
      <div className="section-header">
        <DatabaseOutlined className="section-icon" />
        <div>
          <h3>数据管理</h3>
          <p>管理您的个人数据</p>
        </div>
      </div>

      <div className="data-management-actions">
        <div className="data-action-item">
          <div className="data-action-info">
            <div className="data-action-title">
              <DatabaseOutlined className="data-action-icon" />
              <strong>导出数据</strong>
            </div>
            <p>下载您在平台上的所有数据，包括帖子、评论、个人信息等</p>
          </div>
          <Button type="primary" ghost onClick={() => message.success('数据导出请求已提交')}>导出数据</Button>
        </div>

        <Divider />

        <div className="data-action-item danger">
          <div className="data-action-info">
            <div className="data-action-title">
              <DeleteOutlined className="data-action-icon danger" />
              <strong>删除账户</strong>
            </div>
            <p>永久删除您的账户和所有相关数据。此操作不可恢复，请谨慎操作。</p>
          </div>
          <Button type="primary" danger onClick={handleDeleteAccount}>删除账户</Button>
        </div>
      </div>
    </div>
  );

  const tabItems = [
    {
      key: 'profile',
      label: (<span><UserOutlined />个人信息</span>),
      children: <ProfileSettings />
    },
    {
      key: 'privacy',
      label: (<span><EyeInvisibleOutlined />隐私设置</span>),
      children: <PrivacySettings />
    },
    {
      key: 'mental-health',
      label: (<span><HeartOutlined />心理健康</span>),
      children: <MentalHealthSettings />
    },
    {
      key: 'data',
      label: (<span><DatabaseOutlined />数据管理</span>),
      children: <DataManagement />
    }
  ];

  return (
    <div className="settings-page">
      <div className="settings-container">
        <div className="settings-header">
          <Title level={2} className="settings-title">设置中心</Title>
          <Text type="secondary" className="settings-subtitle">管理您的账户设置和个人偏好</Text>
        </div>

        <div className="settings-content">
          <Tabs
            activeKey={activeTab}
            onChange={setActiveTab}
            tabPosition="left"
            size="large"
            items={tabItems}
            className="settings-tabs"
          />
        </div>
      </div>
    </div>
  );
}

export default Settings;