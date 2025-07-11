import React, { useState } from 'react';
import { ExclamationCircleOutlined, CloseOutlined } from '@ant-design/icons';
import { message } from 'antd';
import '../css/ReportModal.css';

const ReportModal = ({ 
    visible, 
    onClose, 
    onReport, 
    contentType = 'post', // 'post' 或 'comment'
    contentId,
    contentTitle = ''
}) => {
    const [reason, setReason] = useState('');
    const [detail, setDetail] = useState('');
    const [additionalInfo, setAdditionalInfo] = useState('');
    const [loading, setLoading] = useState(false);

    // 举报原因选项
    const reportReasons = [
        { value: 'spam', label: '垃圾信息/广告' },
        { value: 'inappropriate', label: '不当内容' },
        { value: 'harassment', label: '骚扰/攻击' },
        { value: 'copyright', label: '版权侵犯' },
        { value: 'fake', label: '虚假信息' },
        { value: 'other', label: '其他原因' }
    ];

    const handleSubmit = async () => {
        if (!reason) {
            message.error('请选择举报原因');
            return;
        }

        if (reason === 'other' && !detail.trim()) {
            message.error('请填写详细说明');
            return;
        }

        try {
            setLoading(true);
            const reportData = {
                reason: reason ,
                detail: detail,
            };
            
            await onReport(contentId, reportData);
            message.success('举报提交成功，我们会尽快处理');
            handleClose();
        } catch (error) {
            message.error('举报提交失败：' + error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setReason('');
        setDetail('');
        setLoading(false);
        onClose();
    };

    if (!visible) return null;

    return (
        <div className="report-modal-overlay" onClick={handleClose}>
            <div className="report-modal-content" onClick={e => e.stopPropagation()}>
                <div className="report-modal-header">
                    <div className="report-modal-title">
                        <ExclamationCircleOutlined className="report-icon" />
                        <span>举报{contentType === 'post' ? '帖子' : '评论'}</span>
                    </div>
                    <button className="report-modal-close" onClick={handleClose}>
                        <CloseOutlined />
                    </button>
                </div>

                <div className="report-modal-body">
                    {contentTitle && (
                        <div className="report-content-info">
                            <strong>被举报内容：</strong>
                            <div className="report-content-title">{contentTitle}</div>
                        </div>
                    )}

                    <div className="report-form">
                        <div className="report-form-group">
                            <label className="report-form-label">
                                <span className="required">*</span>举报原因
                            </label>
                            <div className="report-reasons-grid">
                                {reportReasons.map(reasonOption => (
                                    <label key={reasonOption.value} className="report-reason-item">
                                        <input
                                            type="radio"
                                            name="reason"
                                            value={reasonOption.value}
                                            checked={reason === reasonOption.value}
                                            onChange={(e) => setReason(e.target.value)}
                                        />
                                        <span className="report-reason-label">{reasonOption.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="report-form-group">
                            <label className="report-form-label">
                                <span className="required">*</span>详细说明
                            </label>
                            <textarea
                                className="report-detail-input"
                                placeholder="请详细描述举报原因..."
                                value={detail}
                                onChange={(e) => setDetail(e.target.value)}
                                rows={4}
                                maxLength={500}
                            />
                            <div className="report-char-count">
                                {detail.length}/500
                            </div>
                        </div>

                    </div>

                    <div className="report-notice">
                        <ExclamationCircleOutlined className="notice-icon" />
                        <span>我们会对举报内容进行审核，请确保举报信息真实有效。</span>
                    </div>
                </div>

                <div className="report-modal-footer">
                    <button 
                        className="report-btn report-btn-cancel" 
                        onClick={handleClose}
                        disabled={loading}
                    >
                        取消
                    </button>
                    <button 
                        className="report-btn report-btn-submit" 
                        onClick={handleSubmit}
                        disabled={loading}
                    >
                        {loading ? '提交中...' : '提交举报'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ReportModal; 