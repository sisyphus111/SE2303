import React, { useState } from 'react';
import { Input, Button, Space, Row, Col } from 'antd';
const { TextArea } = Input;

const CommentInput = ({ onSubmit }) => {
    const [comment, setComment] = useState('');

    const handleSubmit = () => {
        if (comment.trim()) {
            onSubmit(comment);
            setComment('');
        }
    };

    return (
        <Space direction="vertical" style={{ width: '100%', marginTop: '20px' }}>
            <TextArea
                placeholder="评论"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                onPressEnter={handleSubmit}
                style={{ width: '100%', marginBottom: '10px' }}
            />
            <Row justify="end">
                <Col><Button type="primary" onClick={handleSubmit} disabled={!comment.trim()}>发送</Button></Col>
            </Row>
        </Space>
    );
};

export default CommentInput;