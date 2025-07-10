import React from 'react';
import { Table, Button, Space, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, PlayCircleOutlined, PauseCircleOutlined } from '@ant-design/icons';

const AdminMusicTable = ({ musics, loading, pagination, playingMusicId, onPlay, onEdit, onDelete, onPageChange, showCategoryColumn }) => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '标题', dataIndex: 'title', key: 'title', width: 220, ellipsis: true },
    showCategoryColumn !== false && { title: '种类', dataIndex: 'category', key: 'category' },
    { title: '封面', dataIndex: 'cover', key: 'cover', width: 100, render: (cover) => (<img src={cover} alt="封面" style={{ width: 60, height: 40, objectFit: 'cover', borderRadius: 4 }} />) },
    { title: '操作', key: 'action', width: 200, render: (_, record) => (
      <Space>
        <Button type="default" size="small" icon={playingMusicId === record.id ? <PauseCircleOutlined /> : <PlayCircleOutlined />} onClick={() => onPlay(record)}>
          {playingMusicId === record.id ? '暂停' : '播放'}
        </Button>
        <Button type="primary" size="small" icon={<EditOutlined />} onClick={() => onEdit(record)}>编辑</Button>
        <Popconfirm title="确定要删除这首音乐吗？" onConfirm={() => onDelete(record.id)} okText="确定" cancelText="取消">
          <Button type="primary" danger size="small" icon={<DeleteOutlined />}>删除</Button>
        </Popconfirm>
      </Space>
    ) },
  ].filter(Boolean);
  return (
    <Table
      columns={columns}
      dataSource={musics}
      rowKey="id"
      loading={loading}
      scroll={{ x: 'max-content' }}
      pagination={pagination}
      onChange={onPageChange}
    />
  );
};
export default AdminMusicTable; 