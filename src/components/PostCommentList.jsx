import React from 'react';
import { List, Space } from 'antd';
import PostComment from './PostComment';

const PostCommentList = ({ comments }) => {
    return (
        <Space direction="vertical" style={{ width: '100%', marginTop: '20px' }}>
            <List
                itemLayout="horizontal"
                dataSource={comments}
                renderItem={(comment, index) => (
                    <List.Item>
                        <PostComment key={index} user={comment.user} content={comment.content} />
                    </List.Item>
                )}
            />
        </Space>
    );
};

export default PostCommentList;