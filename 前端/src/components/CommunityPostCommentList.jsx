import React from 'react';
import { List, Space } from 'antd';
import PostComment from './CommunityPostComment';

const PostCommentList = ({ comments }) => {
    return (
        <Space direction="vertical" style={{ width: '100%', marginTop: '20px' }}>
            <List
                itemLayout="horizontal"
                dataSource={comments}
                renderItem={(comment, index) => (
                    <List.Item>
                        <PostComment key={index} comment={comment} />
                    </List.Item>
                )}
            />
        </Space>
    );
};

export default PostCommentList;