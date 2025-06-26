import React, { useState } from 'react';
import { submitCheckIn } from '../service/homeService';
import TagSelector from './TagSelector';
import '../css/CheckInCard.css';

const CheckInCard = ({ onCheckInSuccess }) => {
  const [selectedRating, setSelectedRating] = useState(0);
  const [selectedTags, setSelectedTags] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 可选标签
  const availableTags = [
    '开心', '焦虑', '压力', '放松', '疲惫', 
    '兴奋', '平静', '担心', '满足', '困惑'
  ];

  const handleRatingClick = (rating) => {
    setSelectedRating(rating);
  };

  const handleTagToggle = (tag) => {
    setSelectedTags(prev => 
      prev.includes(tag) 
        ? prev.filter(t => t !== tag)
        : [...prev, tag]
    );
  };

  const handleSubmit = async () => {
    if (selectedRating === 0) {
      alert('请选择评分！');
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      await submitCheckIn({
        rating: selectedRating,
        tags: selectedTags,
        timestamp: new Date().toISOString()
      });
      
      alert('打卡成功！');
      setSelectedRating(0);
      setSelectedTags([]);
      
      // 通知父组件更新数据
      if (onCheckInSuccess) {
        onCheckInSuccess();
      }
    } catch (error) {
      console.error('打卡失败:', error);
      alert('打卡失败，请重试');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="card checkin-card">
      <div className="card-content">
        <h2>打卡 (评分+标签)</h2>
        
        {/* 星星评分 */}
        <div className="rating-section">
          <p>今日心情评分：</p>
          <div className="stars">
            {[1, 2, 3, 4, 5].map(star => (
              <span
                key={star}
                className={`star ${selectedRating >= star ? 'selected' : ''}`}
                onClick={() => handleRatingClick(star)}
              >
                ★
              </span>
            ))}
          </div>
        </div>

        {/* 标签选择 */}
        <div className="tags-section">
          <TagSelector
            tags={availableTags}
            selectedTags={selectedTags}
            onTagToggle={handleTagToggle}
            title="选择标签："
            variant="mood"
            maxSelection={5}
          />
        </div>

        <button 
          className={`checkin-btn ${isSubmitting ? 'submitting' : ''}`}
          onClick={handleSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? '提交中...' : '提交打卡'}
        </button>
      </div>
    </div>
  );
};

export default CheckInCard;