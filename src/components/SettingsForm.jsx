import { Form, Input, Button, Upload, Avatar, Radio, Select } from 'antd';
import { UploadOutlined, UserOutlined } from '@ant-design/icons';
import { useState } from 'react';

const tagOptions = [
  { label: '积极', value: '积极' },
  { label: '乐观', value: '乐观' },
  { label: '耐心', value: '耐心' },
  { label: '细心', value: '细心' },
  { label: '善解人意', value: '善解人意' },
];

// 自定义一个用户对象
const user = {
  username: '张三',
  telephone: '10000000000',
  email: 'zhangsan@example.com',
  gender: '男',
  tags: ['积极', '乐观'],
  avatar: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=120&q=80', // 可以放头像URL
};

function SettingsForm() {
  const [form] = Form.useForm();
  const [avatarUrl, setAvatarUrl] = useState(user.avatar);
  // 假设虚拟ID由后端生成，这里先写死
  const virtualId = 'MH123456';

  // 头像上传处理
  const handleAvatarChange = info => {
    if (info.file.status === 'done' || info.file.status === 'uploading') {
      const reader = new FileReader();
      reader.onload = e => {
        setAvatarUrl(e.target.result);
        // 同步设置表单字段
        form.setFieldsValue({ avatar: e.target.result });
      };
      reader.readAsDataURL(info.file.originFileObj);
    }
  };

  const onFinish = values => {
    // 处理表单提交
    console.log('Received values:', values);
  };

return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      className="settings-form"
      initialValues={{
        ...user,
        gender: user.gender || '保密',
        tags: user.tags || [],
        avatar: user.avatar,
      }}
    >
      <Form.Item label="头像">
        <div className="settings-avatar-row">
          <Avatar
            size={64}
            src={avatarUrl}
            icon={<UserOutlined />}
            className="settings-avatar"
          />
          <Upload
            showUploadList={false}
            beforeUpload={() => false}
            onChange={handleAvatarChange}
            accept="image/*"
          >
            <Button icon={<UploadOutlined />}>上传头像</Button>
          </Upload>
        </div>
        <Form.Item name="avatar" noStyle>
          <Input type="hidden" />
        </Form.Item>
      </Form.Item>
      <Form.Item label="虚拟ID">
        <Input value={virtualId} disabled />
      </Form.Item>
      <Form.Item
        label="用户名"
        name="username"
        rules={[{ required: true, message: '请输入用户名' }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="联系电话"
        name="telephone"
        rules={[{ required: true, message: '请输入联系电话' }]}
      >
        <Input/>
      </Form.Item>
      <Form.Item
        label="邮箱"
        name="email"
        rules={[
          { required: true, message: '请输入邮箱' },
          { type: 'email', message: '邮箱格式不正确' }
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="性别"
        name="gender"
        rules={[{ required: true, message: '请选择性别' }]}
      >
        <Radio.Group>
          <Radio value="男">男</Radio>
          <Radio value="女">女</Radio>
          <Radio value="保密">保密</Radio>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        label="个人标签"
        name="tags"
      >
        <Select
          mode="multiple"
          allowClear
          placeholder="请选择个人标签"
          options={tagOptions}
        />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit" block size="large" className="settings-btn">
          保存设置
        </Button>
      </Form.Item>
    </Form>
  );
}

export default SettingsForm;