import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  ArrowLeftOutlined, 
  TagOutlined, 
  EditOutlined,
  SendOutlined,
  BookOutlined,
  HeartOutlined,
  UserOutlined,
  SmileOutlined,
  ThunderboltOutlined
} from '@ant-design/icons';
import { tagOptions } from '../data/post_data';
import '../css/SendCommunityPost.css';

const SendCommunityPostPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        tags: []
    });
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    const tagIcons = {
        '情感': <HeartOutlined />,
        '学习': <BookOutlined />,
        '生活': <UserOutlined />,
        '健康': <SmileOutlined />,
        '成长': <ThunderboltOutlined />
    };

    const validateForm = () => {
        const newErrors = {};
        
        if (!formData.title.trim()) {
            newErrors.title = '请输入帖子标题';
        } else if (formData.title.length > 100) {
            newErrors.title = '标题不能超过100个字符';
        }

        if (!formData.content.trim()) {
            newErrors.content = '请输入帖子内容';
        } else if (formData.content.length > 2000) {
            newErrors.content = '内容不能超过2000个字符';
        }

        if (formData.tags.length === 0) {
            newErrors.tags = '请至少选择一个标签';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleInputChange = (field, value) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
        
        // 清除对应字段的错误
        if (errors[field]) {
            setErrors(prev => ({
                ...prev,
                [field]: ''
            }));
        }
    };

    const handleTagToggle = (tagValue) => {
        setFormData(prev => ({
            ...prev,
            tags: prev.tags.includes(tagValue)
                ? prev.tags.filter(tag => tag !== tagValue)
                : [...prev.tags, tagValue]
        }));

        if (errors.tags) {
            setErrors(prev => ({
                ...prev,
                tags: ''
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setLoading(true);
        
        try {
            // TODO: 调用API提交帖子
            console.log('Submitted post:', formData);
            
            // 模拟API调用
            await new Promise(resolve => setTimeout(resolve, 1500));
            
            // 成功后导航回社区页面
            navigate('/community');
        } catch (error) {
            console.error('Failed to submit post:', error);
            // TODO: 显示错误提示
        } finally {
        setLoading(false);
        }
    };

    return (
        <div className="send-post-page">
            {/* 头部导航 */}
            <div className="send-post-header">
                <button 
                    className="back-button"
                    onClick={() => navigate('/community')}
                >
                    <ArrowLeftOutlined />
                    <span>返回社区</span>
                </button>
                <h1>发表新帖子</h1>
                <div className="header-decoration">
                    <EditOutlined />
                </div>
            </div>

            <div className="send-post-layout">
                {/* 主表单区域 */}
                <div className="post-form-section">
                    <form onSubmit={handleSubmit} className="post-form">
                        {/* 标题输入 */}
                        <div className="form-group">
                            <label className="form-label">
                                <EditOutlined />
                                <span>帖子标题</span>
                                <span className="required">*</span>
                            </label>
                            <input
                                type="text"
                                className={`form-input ${errors.title ? 'error' : ''}`}
                                placeholder="分享你想要讨论的主题..."
                                value={formData.title}
                                onChange={(e) => handleInputChange('title', e.target.value)}
                                maxLength={100}
                            />
                            {errors.title && <span className="error-message">{errors.title}</span>}
                            <div className="char-counter">
                                {formData.title.length}/100
                            </div>
                        </div>

                        {/* 标签选择 */}
                        <div className="form-group">
                            <label className="form-label">
                                <TagOutlined />
                                <span>选择标签</span>
                                <span className="required">*</span>
                            </label>
                            <div className="tags-selection">
                                {tagOptions.map(tag => (
                                    <button
                                        key={tag.value}
                                        type="button"
                                        className={`tag-option ${formData.tags.includes(tag.value) ? 'selected' : ''}`}
                                        onClick={() => handleTagToggle(tag.value)}
                                    >
                                        {tagIcons[tag.value]}
                                        <span>{tag.label}</span>
                                    </button>
                                ))}
                            </div>
                            {errors.tags && <span className="error-message">{errors.tags}</span>}
                            <div className="selected-tags">
                                {formData.tags.map(tag => (
                                    <span key={tag} className="selected-tag">
                                        {tagIcons[tag]}
                                        {tag}
                                    </span>
                                ))}
                            </div>
                        </div>

                        {/* 内容输入 */}
                        <div className="form-group">
                            <label className="form-label">
                                <BookOutlined />
                                <span>帖子内容</span>
                                <span className="required">*</span>
                            </label>
                            <textarea
                                className={`form-textarea ${errors.content ? 'error' : ''}`}
                                placeholder="分享你的想法、经历或感受...&#10;&#10;在这里你可以：&#10;• 分享学习心得和方法&#10;• 讨论生活中的趣事和困扰&#10;• 交流情感体验和成长感悟&#10;• 分享健康生活的小贴士&#10;&#10;让我们一起创建一个温暖、积极的交流空间！"
                                value={formData.content}
                                onChange={(e) => handleInputChange('content', e.target.value)}
                                rows={12}
                                maxLength={2000}
                            />
                            {errors.content && <span className="error-message">{errors.content}</span>}
                            <div className="char-counter">
                                {formData.content.length}/2000
                            </div>
                        </div>

                        {/* 提交按钮 */}
                        <div className="form-actions">
                            <button
                                type="button"
                                className="cancel-button"
                                onClick={() => navigate('/community')}
                                disabled={loading}
                            >
                                取消
                            </button>
                            <button
                                type="submit"
                                className="submit-button"
                                disabled={loading || !formData.title.trim() || !formData.content.trim() || formData.tags.length === 0}
                            >
                                {loading ? (
                                    <>
                                        <div className="loading-spinner"></div>
                                        <span>发布中...</span>
                                    </>
                                ) : (
                                    <>
                                        <SendOutlined />
                                        <span>发布帖子</span>
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>

                {/* 右侧提示区域 */}
                <div className="post-tips-section">
                    <div className="tips-card">
                        <div className="tips-header">
                            <HeartOutlined />
                            <h3>发帖小贴士</h3>
                        </div>
                        <div className="tips-content">
                            <div className="tip-item">
                                <div className="tip-icon">
                                    <EditOutlined />
                                </div>
                                <div className="tip-text">
                                    <strong>写一个吸引人的标题</strong>
                                    <p>简洁明了地表达你的主题</p>
                                </div>
                            </div>
                            <div className="tip-item">
                                <div className="tip-icon">
                                    <TagOutlined />
                                </div>
                                <div className="tip-text">
                                    <strong>选择合适的标签</strong>
                                    <p>帮助其他用户找到你的帖子</p>
                                </div>
                            </div>
                            <div className="tip-item">
                                <div className="tip-icon">
                                    <BookOutlined />
                                </div>
                                <div className="tip-text">
                                    <strong>详细描述你的想法</strong>
                                    <p>分享更多细节会获得更好的回应</p>
                                </div>
                            </div>
                            <div className="tip-item">
                                <div className="tip-icon">
                                    <HeartOutlined />
                                </div>
                                <div className="tip-text">
                                    <strong>保持友善和尊重</strong>
                                    <p>营造积极的社区氛围</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="community-stats">
                        <div className="stats-header">
                            <UserOutlined />
                            <h3>社区动态</h3>
                        </div>
                        <div className="stats-content">
                            <div className="stat-item">
                                <span className="stat-number">1,234</span>
                                <span className="stat-label">活跃用户</span>
                            </div>
                            <div className="stat-item">
                                <span className="stat-number">5,678</span>
                                <span className="stat-label">总帖子数</span>
                            </div>
                            <div className="stat-item">
                                <span className="stat-number">12,345</span>
                                <span className="stat-label">评论互动</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SendCommunityPostPage;