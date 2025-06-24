import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './NewPost.css';

const NewPost = () => {
  const [postData, setPostData] = useState({
    title: '',
    content: '',
    category: '心情分享',
    tags: []
  });
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentTag, setCurrentTag] = useState('');
  const navigate = useNavigate();

  const categories = [
    '心情分享',
    '压力管理', 
    '心理健康',
    '资源推荐',
    '互助交流'
  ];

  const suggestedTags = [
    '焦虑', '抑郁', '压力', '快乐', '悲伤', '愤怒', '恐惧',
    '工作', '学习', '家庭', '朋友', '爱情', '健康', '运动',
    '冥想', '阅读', '音乐', '旅行', '美食', '宠物', '植物',
    '自我提升', '心理治疗', '心理咨询', '书籍推荐', '经验分享'
  ];

  useEffect(() => {
    // 检查登录状态
    const loginStatus = localStorage.getItem('isLoggedIn');
    if (loginStatus === 'true') {
      setIsLoggedIn(true);
    } else {
      navigate('/login');
    }
  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPostData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleTagAdd = (tag) => {
    const trimmedTag = tag.trim();
    if (trimmedTag && !postData.tags.includes(trimmedTag) && postData.tags.length < 5) {
      setPostData(prev => ({
        ...prev,
        tags: [...prev.tags, trimmedTag]
      }));
      setCurrentTag('');
    }
  };

  const handleTagRemove = (tagToRemove) => {
    setPostData(prev => ({
      ...prev,
      tags: prev.tags.filter(tag => tag !== tagToRemove)
    }));
  };

  const handleTagInputKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleTagAdd(currentTag);
    }
  };

  const handleSuggestedTagClick = (tag) => {
    if (!postData.tags.includes(tag) && postData.tags.length < 5) {
      setPostData(prev => ({
        ...prev,
        tags: [...prev.tags, tag]
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!postData.title.trim()) {
      alert('请输入帖子标题');
      return;
    }
    
    if (!postData.content.trim()) {
      alert('请输入帖子内容');
      return;
    }

    setIsSubmitting(true);

    try {
      // 模拟提交
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // 这里可以替换为真实的API调用
      const newPost = {
        id: Date.now(),
        ...postData,
        author: localStorage.getItem('username') || '用户',
        authorAvatar: '👤',
        likes: 0,
        comments: 0,
        views: 0,
        createdAt: new Date().toLocaleString(),
        isLiked: false
      };

      // 模拟保存到本地存储
      const existingPosts = JSON.parse(localStorage.getItem('posts') || '[]');
      existingPosts.unshift(newPost);
      localStorage.setItem('posts', JSON.stringify(existingPosts));

      alert('帖子发布成功！');
      navigate('/community');
    } catch (error) {
      alert('发布失败，请重试');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    if (postData.title.trim() || postData.content.trim()) {
      if (window.confirm('确定要取消发布吗？未保存的内容将丢失。')) {
        navigate('/community');
      }
    } else {
      navigate('/community');
    }
  };

  if (!isLoggedIn) {
    return null;
  }

  return (
    <div className="new-post-container">
      <div className="new-post-header">
        <button 
          className="back-btn"
          onClick={handleCancel}
        >
          ← 返回社区
        </button>
        <h1>发布新帖子</h1>
      </div>

      <form onSubmit={handleSubmit} className="new-post-form">
        <div className="form-section">
          <label htmlFor="title">标题 *</label>
          <input
            type="text"
            id="title"
            name="title"
            value={postData.title}
            onChange={handleInputChange}
            placeholder="给你的帖子起一个吸引人的标题..."
            maxLength="100"
            required
          />
          <span className="char-count">{postData.title.length}/100</span>
        </div>

        <div className="form-section">
          <label htmlFor="category">分类</label>
          <select
            id="category"
            name="category"
            value={postData.category}
            onChange={handleInputChange}
          >
            {categories.map(category => (
              <option key={category} value={category}>
                {category}
              </option>
            ))}
          </select>
        </div>

        <div className="form-section">
          <label>标签 (最多5个)</label>
          <div className="tags-input-container">
            <input
              type="text"
              value={currentTag}
              onChange={(e) => setCurrentTag(e.target.value)}
              onKeyPress={handleTagInputKeyPress}
              placeholder="输入标签后按回车添加..."
              maxLength="20"
            />
            <button
              type="button"
              className="add-tag-btn"
              onClick={() => handleTagAdd(currentTag)}
              disabled={!currentTag.trim() || postData.tags.length >= 5}
            >
              添加
            </button>
          </div>
          
          <div className="tags-list">
            {postData.tags.map(tag => (
              <span key={tag} className="tag">
                {tag}
                <button
                  type="button"
                  onClick={() => handleTagRemove(tag)}
                  className="remove-tag-btn"
                >
                  ✕
                </button>
              </span>
            ))}
          </div>

          <div className="suggested-tags">
            <p>推荐标签：</p>
            <div className="suggested-tags-list">
              {suggestedTags.map(tag => (
                <button
                  key={tag}
                  type="button"
                  className={`suggested-tag ${postData.tags.includes(tag) ? 'selected' : ''}`}
                  onClick={() => handleSuggestedTagClick(tag)}
                  disabled={postData.tags.includes(tag) || postData.tags.length >= 5}
                >
                  {tag}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="form-section">
          <label htmlFor="content">内容 *</label>
          <textarea
            id="content"
            name="content"
            value={postData.content}
            onChange={handleInputChange}
            placeholder="分享你的想法、经历或问题...&#10;&#10;提示：&#10;• 详细描述你的情况&#10;• 分享你的感受和想法&#10;• 寻求建议或支持&#10;• 保持友善和尊重"
            rows="12"
            required
          />
          <div className="content-tips">
            <p>💡 写作提示：</p>
            <ul>
              <li>详细描述你的情况，这样别人能更好地理解你</li>
              <li>分享你的真实感受，不要害怕表达脆弱</li>
              <li>如果你在寻求建议，请说明你希望得到什么样的帮助</li>
              <li>保持友善和尊重的态度，营造温暖的社区氛围</li>
            </ul>
          </div>
        </div>

        <div className="form-actions">
          <button
            type="button"
            className="cancel-btn"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            取消
          </button>
          <button
            type="submit"
            className="submit-btn"
            disabled={isSubmitting || !postData.title.trim() || !postData.content.trim()}
          >
            {isSubmitting ? (
              <div className="loading-spinner">
                <span></span>
                <span></span>
                <span></span>
              </div>
            ) : (
              '发布帖子'
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default NewPost; 