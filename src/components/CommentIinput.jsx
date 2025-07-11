import React, {useMemo, useState} from 'react';
import { SendOutlined } from '@ant-design/icons';
import { message } from 'antd';
import { useUser } from '../lib/UserContext';
import ContentWarningModal from './ContentWarningModal';
import { ContentInspectionClient } from '../service/diaryService';
import '../css/CommentIinput.css';

const CommentInput = ({ onSubmit, postId }) => {
    const [comment, setComment] = useState('');
    const [showWarningModal, setShowWarningModal] = useState(false);
    const { userProfile, valid, identity } = useUser();
    const auth = useMemo(() => ({
        userId: userProfile?.userId,
        valid,
        identity,
    }), [userProfile, valid, identity]);

    const handleSubmit = async () => {
        if (!userProfile || !valid) {
            message.error('请先登录');
            return;
        }
        if (comment.trim()) {
            try {
                // 内容审查
                const contentInspector = new ContentInspectionClient();
                const contentToInspect = comment;
                
                const contentSafe = await contentInspector.validateContent(contentToInspect);
                
                // 添加调试信息
                console.log('评论内容审查结果:');
                console.log('内容安全:', contentSafe, '类型:', typeof contentSafe);
                console.log('内容内容:', contentToInspect);
                
                // 如果内容不安全，显示通知框并停止发送
                if (!contentSafe) {
                    // 创建内容审查警报
                    try {
                        await contentInspector.createContentAlert(userProfile.userId, contentToInspect, '评论');
                        console.log('🚨 已创建评论内容审查警报');
                    } catch (alertError) {
                        console.error('创建内容审查警报失败:', alertError);
                    }
                    
                    // 显示安慰用户的模态框
                    setShowWarningModal(true);
                    return; // 停止发送评论
                }

                // 内容安全，通过onSubmit回调发送评论
                onSubmit(comment);
                setComment('');
            } catch (error) {
                console.error('内容审查失败:', error);
                message.error('内容审查失败，请稍后重试');
            }
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
            handleSubmit();
        }
    };

    return (
        <div className="comment-input-container">
            <ContentWarningModal 
                isOpen={showWarningModal} 
                onClose={() => setShowWarningModal(false)} 
            />
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