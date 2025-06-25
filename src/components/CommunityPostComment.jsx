import React from 'react';
import { Avatar, Typography, Space } from 'antd';
const { Text } = Typography;

const PostComment = ({ user, content }) => {
    return (
        <Space direction="vertical" style={{ width: '100%', marginBottom: '20px' }}>
            <Space>
                <Avatar src={user.avatar} />
                <Text style={{ color: 'grey', fontSize: '16px' }}>{user.name}</Text>
            </Space>
            <Text style={{ fontSize: '16px', color: 'black' }}>{content}</Text>
        </Space>
    );
};

export default PostComment;