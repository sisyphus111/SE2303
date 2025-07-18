import React from 'react';
import { Modal, Form, Input, Button, Space } from 'antd';
const { TextArea } = Input;

const AdminArticleModal = ({ visible, editingArticle, form, onSubmit, onCancel }) => (
  <Modal
    title={editingArticle ? '编辑文章' : '添加文章'}
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
      <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入文章标题' }]}> 
        <Input placeholder="请输入文章标题" />
      </Form.Item>
      <Form.Item name="introduction" label="简介" rules={[{ required: true, message: '请输入文章简介' }]}> 
        <TextArea rows={2} placeholder="请输入文章简介" />
      </Form.Item>
      <Form.Item name="description" label="描述" rules={[{ required: true, message: '请输入文章描述' }]}> 
        <TextArea rows={3} placeholder="请输入文章描述" />
      </Form.Item>
      <Form.Item name="cover" label="封面图片URL"> 
        <Input placeholder="请输入封面图片URL" />
      </Form.Item>
      <Form.Item name="link" label="文章链接"> 
        <Input placeholder="请输入文章链接" />
      </Form.Item>
      <Form.Item name="category" label="分类"> 
        <Input placeholder="请输入文章分类" />
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit">{editingArticle ? '更新' : '添加'}</Button>
          <Button onClick={onCancel}>取消</Button>
        </Space>
      </Form.Item>
    </Form>
  </Modal>
);
export default AdminArticleModal; 