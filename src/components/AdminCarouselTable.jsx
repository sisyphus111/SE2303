import React from 'react';
import { Table, Button, Space, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';

const AdminCarouselTable = ({ carousels, loading, pagination, onEdit, onDelete, onPageChange }) => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '图片', dataIndex: 'imageUrl', key: 'imageUrl', width: 120, render: (url) => (<img src={url} alt="轮播图" style={{ width: 80, height: 40, objectFit: 'cover', borderRadius: 4 }} />) },
    { title: '标题', dataIndex: 'title', key: 'title', width: 180, ellipsis: true },
    { title: '描述', dataIndex: 'description', key: 'description', width: 220, ellipsis: true },
    { title: '分类', dataIndex: 'category', key: 'category', width: 100 },
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 80 },
    { title: '操作', key: 'action', width: 180, render: (_, record) => (
      <Space>
        <Button type="primary" size="small" icon={<EditOutlined />} onClick={() => onEdit(record)}>编辑</Button>
        <Popconfirm title="确定要删除这张轮播图吗？" onConfirm={() => onDelete(record.id)} okText="确定" cancelText="取消">
          <Button type="primary" danger size="small" icon={<DeleteOutlined />}>删除</Button>
        </Popconfirm>
      </Space>
    ) },
  ];
  return (
    <Table
      columns={columns}
      dataSource={carousels}
      rowKey="id"
      loading={loading}
      scroll={{ x: 'max-content' }}
      pagination={pagination}
      onChange={onPageChange}
    />
  );
};
export default AdminCarouselTable; 