import React, { useState, useEffect } from 'react';
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
import { message } from 'antd';
import { createPost } from '../service/communityService'; // Import createPost from communityService
import { useUser } from '../lib/UserContext'; // Import useUser hook from UserContext
import TagSelector from '../components/TagSelector';
import ContentWarningModal from '../components/ContentWarningModal';
import { ContentInspectionClient } from '../service/diaryService';
import '../css/SendCommunityPost.css';

const SendCommunityPostPage = () => {
    const navigate = useNavigate();
    const { userProfile, valid, identity, loading: contextLoading } = useUser(); // Get auth data from UserContext
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        tags: []
    });
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});
    const [showWarningModal, setShowWarningModal] = useState(false);

    const tagIcons = {
        '情感': <HeartOutlined />,
        '学习': <BookOutlined />,
        '生活': <UserOutlined />,
        '健康': <SmileOutlined />,
        '成长': <ThunderboltOutlined />
    };

    // Redirect to login if not authenticated
    useEffect(() => {
        if (!contextLoading && (!userProfile || !valid)) {
            console.log('❌ 用户未认证，重定向到登录页面');
            message.error('请先登录');
            navigate('/login');
        }
    }, [userProfile, valid, contextLoading, navigate]);

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

        // Clear error for the field
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
            // 内容审查
            const contentInspector = new ContentInspectionClient();
            const titleToInspect = formData.title;
            const contentToInspect = formData.content;
            
            const [titleSafe, contentSafe] = await Promise.all([
                contentInspector.validateContent(titleToInspect),
                contentInspector.validateContent(contentToInspect)
            ]);
            
            // 添加调试信息
            console.log('内容审查结果:');
            console.log('标题安全:', titleSafe, '类型:', typeof titleSafe);
            console.log('内容安全:', contentSafe, '类型:', typeof contentSafe);
            console.log('标题内容:', titleToInspect);
            console.log('内容内容:', contentToInspect);
            
            // 如果内容不安全，显示通知框并停止发送
            if (!titleSafe || !contentSafe) {
                // 创建内容审查警报
                try {
                    if (!titleSafe) {
                        await contentInspector.createContentAlert(userProfile.userId, titleToInspect, '标题');
                        console.log('🚨 已创建标题内容审查警报');
                    }
                    if (!contentSafe) {
                        await contentInspector.createContentAlert(userProfile.userId, contentToInspect, '内容');
                        console.log('🚨 已创建内容审查警报');
                    }
                } catch (alertError) {
                    console.error('创建内容审查警报失败:', alertError);
                }
                
                // 显示安慰用户的模态框
                setShowWarningModal(true);
                setLoading(false);
                return; // 停止发送帖子
            }

            // Prepare post data
            const postData = {
                title: formData.title,
                content: formData.content,
                tags: formData.tags
            };

            console.log('postData: ', postData);
            // Call createPost from communityService with auth data
            const auth = {
                userId: userProfile?.userId,
                valid,
                identity
            };
            console.log('auth: ', auth);
            const response = await createPost(postData, auth);

            console.log('response: ', response);
            if (response.success) {
                message.success(response.message || '帖子发布成功');
                navigate('/community');
            } else {
                message.error(response.error || '发布帖子失败');
                setErrors({ submit: response.error || '发布帖子失败' });
            }
        } catch (error) {
            console.error('Failed to submit post:', error);
            message.error('发布帖子失败，请稍后重试');
            setErrors({ submit: '发布帖子失败，请稍后重试' });
        } finally {
            setLoading(false);
        }
    };

    // Show loading state while UserContext is initializing
    if (contextLoading) {
        return (
            <div className="loading" style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                fontSize: '16px',
                color: '#666'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ marginBottom: '16px', fontSize: '24px' }}>⏳</div>
                    正在初始化用户信息...
                </div>
            </div>
        );
    }

    return (
        <div className="send-post-page">
            <ContentWarningModal 
                isOpen={showWarningModal} 
                onClose={() => setShowWarningModal(false)} 
            />
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
                            <TagSelector
                                tags={[
                                    { value: '情感', label: '情感' },
                                    { value: '学习', label: '学习' },
                                    { value: '生活', label: '生活' },
                                    { value: '健康', label: '健康' },
                                    { value: '成长', label: '成长' },
                                ]}
                                selectedTags={formData.tags}
                                onTagToggle={handleTagToggle}
                                variant="post"
                                maxSelection={3}
                                tagIcons={tagIcons}
                                title=""
                            />
                            {errors.tags && <span className="error-message">{errors.tags}</span>}
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
                                placeholder="分享你的想法、经历或感受...

在这里你可以：
• 分享学习心得和方法
• 讨论生活中的趣事和困扰
• 交流情感体验和成长感悟
• 分享健康生活的小贴士

让我们一起创建一个温暖、积极的交流空间！"
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
                        {errors.submit && <span className="error-message">{errors.submit}</span>}
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
                </div>
            </div>
        </div>
    );
};

export default SendCommunityPostPage;