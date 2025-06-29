import React, { useState } from 'react';
import { SendOutlined } from '@ant-design/icons';
import '../css/CommentIinput.css';

const CommentInput = ({ onSubmit }) => {
    const [comment, setComment] = useState('');

    const handleSubmit = () => {
        if (comment.trim()) {
            onSubmit(comment);
            setComment('');
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
            handleSubmit();
        }
    };

    return (
        <div className="comment-input-container">
            <div className="input-header">
                <h4>发表评论</h4>
                <span className="input-tip">Ctrl + Enter 快速发送</span>
            </div>
            <div className="input-wrapper">
                <textarea
                    className="comment-textarea"
                    placeholder="分享你的想法..."
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                    onKeyDown={handleKeyPress}
                    rows={4}
                />
                <div className="input-footer">
                    <div className="char-count">
                        {comment.length}/500
                    </div>
                    <button 
                        className="submit-btn"
                        onClick={handleSubmit} 
                        disabled={!comment.trim() || comment.length > 500}
                    >
                        <SendOutlined />
                        <span>发布评论</span>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CommentInput;