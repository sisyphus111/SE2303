import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Diary.css';

const Diary = () => {
  const [diaries, setDiaries] = useState([
    {
      id: 1,
      date: '2024-01-15',
      title: '美好的一天',
      content: '今天和朋友一起去了公园，天气很好，心情也很愉快。我们聊了很多有趣的话题，感觉时间过得很快。',
      mood: 85,
      tags: ['朋友', '户外', '快乐']
    },
    {
      id: 2,
      date: '2024-01-14',
      title: '工作压力',
      content: '今天工作比较忙，有一些项目 deadline 要赶，感觉压力有点大。但是我相信自己能够完成这些任务。',
      mood: 65,
      tags: ['工作', '压力', '挑战']
    }
  ]);
  
  const [currentDiary, setCurrentDiary] = useState({
    title: '',
    content: '',
    mood: 75,
    tags: []
  });
  
  const [isWriting, setIsWriting] = useState(false);
  const [selectedDiary, setSelectedDiary] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // 检查登录状态
    const loginStatus = localStorage.getItem('isLoggedIn');
    if (loginStatus === 'true') {
      setIsLoggedIn(true);
    } else {
      navigate('/login');
    }
  }, [navigate]);

  const handleSaveDiary = () => {
    if (!currentDiary.title.trim() || !currentDiary.content.trim()) {
      alert('请填写日记标题和内容');
      return;
    }

    const newDiary = {
      id: Date.now(),
      date: new Date().toISOString().split('T')[0],
      ...currentDiary
    };

    setDiaries(prev => [newDiary, ...prev]);
    setCurrentDiary({ title: '', content: '', mood: 75, tags: [] });
    setIsWriting(false);
  };

  const handleMoodChange = (value) => {
    setCurrentDiary(prev => ({ ...prev, mood: value }));
  };

  const handleTagAdd = (tag) => {
    if (tag.trim() && !currentDiary.tags.includes(tag.trim())) {
      setCurrentDiary(prev => ({
        ...prev,
        tags: [...prev.tags, tag.trim()]
      }));
    }
  };

  const handleTagRemove = (tagToRemove) => {
    setCurrentDiary(prev => ({
      ...prev,
      tags: prev.tags.filter(tag => tag !== tagToRemove)
    }));
  };

  const getMoodEmoji = (score) => {
    if (score >= 80) return '😊';
    if (score >= 60) return '🙂';
    if (score >= 40) return '😐';
    return '😔';
  };

  const getMoodText = (score) => {
    if (score >= 80) return '很好';
    if (score >= 60) return '不错';
    if (score >= 40) return '一般';
    return '低落';
  };

  if (!isLoggedIn) {
    return null;
  }

  return (
    <div className="diary-container">
      <div className="diary-header">
        <h1>我的日记</h1>
        <button 
          className="new-diary-btn"
          onClick={() => setIsWriting(true)}
        >
          ✏️ 写新日记
        </button>
      </div>

      {isWriting && (
        <div className="diary-editor">
          <div className="editor-header">
            <h2>写新日记</h2>
            <button 
              className="close-btn"
              onClick={() => setIsWriting(false)}
            >
              ✕
            </button>
          </div>

          <div className="editor-content">
            <div className="input-group">
              <label>标题</label>
              <input
                type="text"
                value={currentDiary.title}
                onChange={(e) => setCurrentDiary(prev => ({ ...prev, title: e.target.value }))}
                placeholder="给今天的日记起个标题..."
              />
            </div>

            <div className="input-group">
              <label>心情评分</label>
              <div className="mood-slider">
                <input
                  type="range"
                  min="0"
                  max="100"
                  value={currentDiary.mood}
                  onChange={(e) => handleMoodChange(parseInt(e.target.value))}
                  className="mood-range"
                />
                <div className="mood-display">
                  <span className="mood-emoji">{getMoodEmoji(currentDiary.mood)}</span>
                  <span className="mood-text">{getMoodText(currentDiary.mood)}</span>
                  <span className="mood-score">({currentDiary.mood}分)</span>
                </div>
              </div>
            </div>

            <div className="input-group">
              <label>内容</label>
              <textarea
                value={currentDiary.content}
                onChange={(e) => setCurrentDiary(prev => ({ ...prev, content: e.target.value }))}
                placeholder="记录今天的心情、想法和经历..."
                rows="8"
              />
            </div>

            <div className="input-group">
              <label>标签</label>
              <div className="tags-input">
                <input
                  type="text"
                  placeholder="添加标签（按回车确认）"
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleTagAdd(e.target.value);
                      e.target.value = '';
                    }
                  }}
                />
                <div className="tags-list">
                  {currentDiary.tags.map(tag => (
                    <span key={tag} className="tag">
                      {tag}
                      <button onClick={() => handleTagRemove(tag)}>✕</button>
                    </span>
                  ))}
                </div>
              </div>
            </div>

            <div className="editor-actions">
              <button 
                className="cancel-btn"
                onClick={() => setIsWriting(false)}
              >
                取消
              </button>
              <button 
                className="save-btn"
                onClick={handleSaveDiary}
              >
                保存日记
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="diary-list">
        <h2>历史日记</h2>
        {diaries.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📝</div>
            <p>还没有写过日记</p>
            <button 
              className="start-writing-btn"
              onClick={() => setIsWriting(true)}
            >
              开始写第一篇日记
            </button>
          </div>
        ) : (
          <div className="diaries-grid">
            {diaries.map(diary => (
              <div 
                key={diary.id} 
                className="diary-card"
                onClick={() => setSelectedDiary(diary)}
              >
                <div className="diary-card-header">
                  <h3>{diary.title}</h3>
                  <div className="diary-mood">
                    <span className="mood-emoji">{getMoodEmoji(diary.mood)}</span>
                    <span className="mood-score">{diary.mood}分</span>
                  </div>
                </div>
                <p className="diary-content">
                  {diary.content.length > 100 
                    ? diary.content.substring(0, 100) + '...' 
                    : diary.content
                  }
                </p>
                <div className="diary-footer">
                  <span className="diary-date">{diary.date}</span>
                  <div className="diary-tags">
                    {diary.tags.slice(0, 3).map(tag => (
                      <span key={tag} className="tag-small">{tag}</span>
                    ))}
                    {diary.tags.length > 3 && (
                      <span className="tag-more">+{diary.tags.length - 3}</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {selectedDiary && (
        <div className="diary-modal">
          <div className="modal-content">
            <div className="modal-header">
              <h2>{selectedDiary.title}</h2>
              <button 
                className="close-btn"
                onClick={() => setSelectedDiary(null)}
              >
                ✕
              </button>
            </div>
            <div className="modal-body">
              <div className="diary-meta">
                <span className="diary-date">{selectedDiary.date}</span>
                <div className="diary-mood-full">
                  <span className="mood-emoji">{getMoodEmoji(selectedDiary.mood)}</span>
                  <span className="mood-text">{getMoodText(selectedDiary.mood)}</span>
                  <span className="mood-score">({selectedDiary.mood}分)</span>
                </div>
              </div>
              <div className="diary-content-full">
                {selectedDiary.content}
              </div>
              {selectedDiary.tags.length > 0 && (
                <div className="diary-tags-full">
                  {selectedDiary.tags.map(tag => (
                    <span key={tag} className="tag">{tag}</span>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Diary; 