import React from 'react';
import { Table, Button, Space, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';

const AdminArticleTable = ({ articles, loading, pagination, onEdit, onDelete, onPageChange }) => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '标题', dataIndex: 'title', key: 'title', width: 220, ellipsis: true },
    { title: '简介', dataIndex: 'introduction', key: 'introduction', width: 260, ellipsis: true, render: (text) => (<div style={{maxWidth: 240, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}>{text || ''}</div>) },
    { title: '描述', dataIndex: 'description', key: 'description', width: 320, ellipsis: true, render: (text) => (<div style={{maxWidth: 300, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}>{text || ''}</div>) },
    { title: '封面', dataIndex: 'cover', key: 'cover', width: 100, render: (cover) => (<img src={cover} alt="封面" style={{ width: 60, height: 40, objectFit: 'cover', borderRadius: 4 }} />) },
    { title: '操作', key: 'action', width: 180, render: (_, record) => (<Space><Button type="primary" size="small" icon={<EditOutlined />} onClick={() => onEdit(record)}>编辑</Button><Popconfirm title="确定要删除这篇文章吗？" onConfirm={() => onDelete(record.id)} okText="确定" cancelText="取消"><Button type="primary" danger size="small" icon={<DeleteOutlined />}>删除</Button></Popconfirm></Space>) },
  ];
  return (
    <Table
      columns={columns}
      dataSource={articles}
      rowKey="id"
      loading={loading}
      scroll={{ x: 'max-content' }}
      pagination={pagination}
      onChange={onPageChange}
    />
  );
};
export default AdminArticleTable; 