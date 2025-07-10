import React from 'react';
import { Modal, Form, Input, Button, Space, InputNumber } from 'antd';
const { TextArea } = Input;

const AdminMusicModal = ({ visible, editingMusic, form, onSubmit, onCancel }) => (
  <Modal
    title={editingMusic ? '编辑音乐' : '添加音乐'}
    open={visible}
    onCancel={onCancel}
    footer={null}
    width={600}
  >
    <Form
      form={form}
      layout="vertical"
      onFinish={onSubmit}
    >
      <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入音乐标题' }]}>
        <Input placeholder="请输入音乐标题" />
        </Form.Item>
      <Form.Item name="category" label="种类">
        <Input placeholder="请输入音乐种类" />
        </Form.Item>
      <Form.Item name="cover" label="封面图片URL">
        <Input placeholder="请输入封面图片URL" />
        </Form.Item>
      <Form.Item name="url" label="音频文件URL" rules={[{ required: true, message: '请输入音频文件URL' }]}>
        <Input placeholder="请输入音频文件URL" />
        </Form.Item>
      <Form.Item name="duration" label="时长（秒）" rules={[{ type: 'number', min: 1, message: '请输入正整数' }]}>
        <InputNumber min={1} style={{ width: '100%' }} placeholder="请输入音乐时长（秒）" />
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit">{editingMusic ? '更新' : '添加'}</Button>
          <Button onClick={onCancel}>取消</Button>
        </Space>
      </Form.Item>
    </Form>
  </Modal>
);
export default AdminMusicModal; 