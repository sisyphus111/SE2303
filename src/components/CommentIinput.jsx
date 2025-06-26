import React, { useState } from 'react';
import { Input, Button, Space, Row, Col } from 'antd';
import '../css/CommentIinput.css';
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
        <div className="comment-input-area">
            <Space direction="vertical" style={{ width: '100%' }}>
                <div className="comment-input-textarea">
                    <TextArea
                        placeholder="写下你的评论..."
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        onPressEnter={handleSubmit}
                        style={{ width: '100%', marginBottom: '10px', border: 'none', boxShadow: 'none' }}
                        autoSize={{ minRows: 3, maxRows: 6 }}
                    />
                </div>
                <Row justify="end">
                    <Col>
                        <Button className="comment-input-btn" onClick={handleSubmit} disabled={!comment.trim()}>
                            发送
                        </Button>
                    </Col>
                </Row>
            </Space>
        </div>
    );
};

export default CommentInput;