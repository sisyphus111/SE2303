import React from 'react';
import { Modal, Form, Input, Button, Space, InputNumber } from 'antd';
const { TextArea } = Input;

const AdminCarouselModal = ({ visible, editingCarousel, form, onSubmit, onCancel }) => (
  <Modal
    title={editingCarousel ? '编辑轮播图' : '添加轮播图'}
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
        <Form.Item name="imageUrl" label="图片URL" rules={[{ required: true, message: '请输入图片URL' }]}>
            <Input placeholder="请输入图片URL" />
        </Form.Item>
        <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="请输入标题" />
        </Form.Item>
        <Form.Item name="description" label="描述">
            <TextArea rows={2} placeholder="请输入描述" />
        </Form.Item>
        <Form.Item name="category" label="分类">
            <Input placeholder="请输入分类" />
        </Form.Item>
        <Form.Item name="sortOrder" label="排序" rules={[{ type: 'number', min: 0, message: '请输入非负整数' }]}>
            <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入排序值（数字越小越靠前）" />
        </Form.Item>
        <Form.Item>
            <Space>
                <Button type="primary" htmlType="submit">{editingCarousel ? '更新' : '添加'}</Button>
                <Button onClick={onCancel}>取消</Button>
            </Space>
        </Form.Item>
    </Form>
  </Modal>
);
export default AdminCarouselModal; 