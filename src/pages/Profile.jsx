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
  Tag,
  DatePicker
} from "antd";
import dayjs from 'dayjs';
import {
  UserOutlined,
  UploadOutlined,
  GlobalOutlined,
  MobileOutlined,
  EyeInvisibleOutlined,
  EditOutlined,
  CameraOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { uploadUserAvatar } from '../service/userService';
import "../css/Profile.css";

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;

// 辅助函数：安全地将字符串转换为dayjs对象
const safeStringToDate = (dateString) => {
  if (!dateString || dateString === '' || dateString === 'null' || dateString === 'undefined') {
    return undefined; // 使用 undefined 而不是 null，更适合 Ant Design
  }
  try {
    const date = dayjs(dateString);
    return date.isValid() ? date : undefined;
  } catch (error) {
    console.warn('日期解析错误:', dateString, error);
    return undefined;
  }
};

// 辅助函数：安全地将dayjs对象转换为字符串
const safeDateToString = (date) => {
  if (!date) return '';
  if (dayjs.isDayjs(date) && date.isValid()) {
    return date.format('YYYY-MM-DD');
  }
  if (typeof date === 'string') {
    const parsedDate = dayjs(date);
    return parsedDate.isValid() ? parsedDate.format('YYYY-MM-DD') : '';
  }
  return '';
};

function Profile() {
  const { 
    userProfile, 
    updateUserProfile,  
    profileLoading,
    profileError 
  } = useUser();
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [form] = Form.useForm();

  // 初始化表单数据
  const [userData, setUserData] = useState({
    username: userProfile?.username || '',
    nickname: userProfile?.nickname || '',
    phone: userProfile?.telephone || '',
    gender: userProfile?.gender || '男',
    birthday: userProfile?.birthday || '',
    location: userProfile?.location || '',
    bio: userProfile?.bio || '',
    avatar: userProfile?.avatar || null,
    tags: userProfile?.tags ? (typeof userProfile.tags === 'string' ? userProfile.tags.split(',') : userProfile.tags) : [],
    
    // 隐私设置
    profileVisibility: userProfile?.profileVisibility || 'public',
    showOnlineStatus: true,
    allowDirectMessage: true,
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
    if (userProfile) {
      const updatedData = {
        ...userData,
        username: userProfile.username || userData.username,
        nickname: userProfile.nickname || userData.nickname,
        phone: userProfile.telephone || userData.phone, // 后端是telephone，前端是phone
        avatar: userProfile.avatar || userData.avatar,
        gender: userProfile.gender || userData.gender,
        birthday: userProfile.birthday || userData.birthday,
        location: userProfile.location || userData.location,
        bio: userProfile.bio || userData.bio,
        tags: userProfile.tags ? (typeof userProfile.tags === 'string' ? userProfile.tags.split(',') : userProfile.tags) : userData.tags,
        profileVisibility: userProfile.profileVisibility || userData.profileVisibility,
        showOnlineStatus: userData.showOnlineStatus,
        allowDirectMessage: userData.allowDirectMessage,
        showPhone: userData.showPhone,
      };
      setUserData(updatedData);
      
      // 为表单设置值，生日需要转换为dayjs对象
      const formData = {
        ...updatedData,
        birthday: safeStringToDate(updatedData.birthday)
      };
      form.setFieldsValue(formData);
    }
    
    // 组件挂载时的初始化（处理没有userProfile的情况）
    if (!userProfile && userData) {
      const safeInitialData = {
        ...userData,
        birthday: safeStringToDate(userData.birthday)
      };
      form.setFieldsValue(safeInitialData);
    }
  }, [userProfile, userData.birthday]); // 监听userProfile和生日数据变化

  const handleSave = async (values) => {
    setLoading(true);
    try {
      // 处理tags字段 - 如果是数组则转换为字符串
      const tagsString = Array.isArray(values.tags) ? values.tags.join(',') : values.tags;
      
      // 处理生日格式 - 将dayjs对象转换为YYYY-MM-DD字符串
      const birthdayString = safeDateToString(values.birthday);
      
      // 准备要发送到后端的数据
      const updateData = {
        username: values.username,
        nickname: values.nickname,
        avatar: userData.avatar, // 头像从当前状态获取
        gender: values.gender,
        birthday: birthdayString,
        location: values.location,
        telephone: values.phone,
        bio: values.bio,
        tags: tagsString,
        profileVisibility: values.profileVisibility,
      };
      
      console.log('准备发送到后端的数据:', updateData);
      
      // 使用UserContext的updateUserProfile方法
      const result = await updateUserProfile(updateData);
      
      if (result.success) {
        // 更新本地状态
        const updatedData = { ...userData, ...values };
        setUserData(updatedData);
        
        message.success('个人资料保存成功！');
        setEditing(false);
      } else {
        message.error(result.error || '保存失败，请重试');
      }
    } catch (error) {
      console.error('保存用户资料失败:', error);
      message.error('保存失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  /**
   * 处理头像上传
   * 
   * 整个上传流程：
   * 1. 用户选择图片文件
   * 2. 调用后端API上传到腾讯云COS
   * 3. 获得云存储的URL
   * 4. 更新本地状态和用户资料
   * 5. 显示上传结果给用户
   */
  const handleAvatarChange = async (info) => {
    const { file } = info;
    
    // 只在用户选择文件时处理（beforeUpload返回false，所以不会自动上传）
    if (file.status === undefined) {
      try {
        console.log('🖼️ 用户选择头像文件:', file.name);
        
        // 1. 显示上传中状态
        message.loading({ content: '正在上传头像...', key: 'avatar-upload' });
        
        // 2. 调用文件上传服务，上传到腾讯云COS
        const newAvatarUrl = await uploadUserAvatar(file);
        console.log('📸 新头像URL:', newAvatarUrl);
        
        // 3. 更新本地状态
        setUserData(prev => ({ ...prev, avatar: newAvatarUrl }));
        
        // 4. 更新后端用户资料（保存到数据库）
        const updateResult = await updateUserProfile({ avatar: newAvatarUrl });
        
        if (updateResult.success) {
          // 5. 显示成功消息
          message.success({ content: '头像上传成功！', key: 'avatar-upload', duration: 2 });
        } else {
          throw new Error(updateResult.error || '更新用户资料失败');
        }
        
      } catch (error) {
        console.error('❌ 头像上传失败:', error);
        
        // 显示错误消息
        message.error({ 
          content: `头像上传失败: ${error.message}`, 
          key: 'avatar-upload',
          duration: 3 
        });
      }
    }
  };

  const handlePrivacyChange = async (key, value) => {
    try {
      // 更新本地状态
      const updatedData = { ...userData, [key]: value };
      setUserData(updatedData);
      
      // 准备发送到后端的数据，只发送必要的字段
      const birthdayString = safeDateToString(updatedData.birthday || userProfile?.birthday);
      
      const updateData = {
        username: updatedData.username || userProfile?.username,
        nickname: updatedData.nickname || userProfile?.nickname,
        avatar: updatedData.avatar || userProfile?.avatar || "default_avatar.png",
        gender: updatedData.gender || userProfile?.gender,
        birthday: birthdayString,
        location: updatedData.location || userProfile?.location,
        telephone: updatedData.phone || userProfile?.telephone,
        bio: updatedData.bio || userProfile?.bio,
        tags: (() => {
          if (Array.isArray(updatedData.tags)) {
            return updatedData.tags.join(',');
          } else if (typeof updatedData.tags === 'string') {
            return updatedData.tags;
          } else {
            return '';
          }
        })(),
        profileVisibility: updatedData.profileVisibility,
      };
      
      console.log('发送到后端的数据:', updateData); // 添加调试日志
      console.log('profileVisibility 值:', updateData.profileVisibility);
      console.log('tags 类型:', typeof updateData.tags, '值:', updateData.tags);
      
      // 使用UserContext的updateUserProfile方法
      const result = await updateUserProfile(updateData);
      console.log('后端响应:', result);
      
      if (result.success) {
        message.success('隐私设置已更新并保存');
      } else {
        message.error(result.error || '保存失败，请重试');
        // 回滚本地状态
        setUserData(userData);
      }
    } catch (error) {
      console.error('保存隐私设置失败:', error);
      message.error('保存失败，请重试: ' + error.message);
      // 回滚本地状态
      setUserData(userData);
    }
  };

  const getUserAvatar = () => {
    if (userData.avatar) {
      return userData.avatar;
    }
    const username = userData.username || 'User';
    return username.charAt(0).toUpperCase();
  };

  // 显示加载状态
  if (profileLoading && !userProfile) {
    return (
      <div className="profile-page">
        <div className="profile-container" style={{ textAlign: 'center', padding: '50px' }}>
          <div>加载用户信息中...</div>
        </div>
      </div>
    );
  }

  // 显示错误状态
  if (profileError) {
    return (
      <div className="profile-page">
        <div className="profile-container" style={{ textAlign: 'center', padding: '50px' }}>
          <div>加载用户信息失败: {profileError}</div>
          <Button onClick={() => window.location.reload()}>重新加载</Button>
        </div>
      </div>
    );
  }

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
                {!editing && (
                  <Button 
                    type="primary" 
                    icon={<EditOutlined />}
                    onClick={() => setEditing(true)}
                  >
                    编辑资料
                  </Button>
                )}
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

                      <Form.Item 
                        label="手机号（不会公开显示）" 
                        name="phone"
                      >
                        <Input prefix={<MobileOutlined />} />
                      </Form.Item>

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
                        <DatePicker 
                          placeholder="选择生日"
                          style={{ width: '100%' }}
                          format="YYYY-MM-DD"
                        />
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
                          // 重置表单时需要处理生日格式
                          const formData = {
                            ...userData,
                            birthday: safeStringToDate(userData.birthday)
                          };
                          form.setFieldsValue(formData);
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
                    <Text>{userProfile?.registrationDate ? new Date(userProfile.registrationDate).toLocaleDateString() : '2025-07-01'}</Text>
                  </div>
                  <div className="info-item">
                    <Text type="secondary">用户ID</Text>
                    <Text>{userProfile?.id || userProfile?.virtualId || 'demo-user'}</Text>
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