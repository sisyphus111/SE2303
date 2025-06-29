import React, { useState } from 'react';
import { submitCheckIn } from '../service/homeService';
import TagSelector from './TagSelector';
import '../css/CheckInCard.css';

const CheckInCard = ({ onCheckInSuccess }) => {
  const [selectedRating, setSelectedRating] = useState(0);
  const [selectedTags, setSelectedTags] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  // 打卡成功状态
  const [isCheckedIn, setIsCheckedIn] = useState(false);
  const [checkedInData, setCheckedInData] = useState(null);

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
    
    const checkInData = {
      rating: selectedRating,
      tags: selectedTags,
      timestamp: new Date().toISOString(),
      date: new Date().toLocaleDateString('zh-CN')
    };
    
    try {
      // API调用注释：提交打卡数据到后端
      // await submitCheckIn(checkInData);
      
      // 模拟API调用延迟
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 保存打卡成功状态和数据（仅在前端，重启后清空）
      setCheckedInData(checkInData);
      setIsCheckedIn(true);
      
      // 清空表单
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

  // 重新打卡功能
  const handleRestart = () => {
    setIsCheckedIn(false);
    setCheckedInData(null);
    setSelectedRating(0);
    setSelectedTags([]);
  };

  return (
    <div className={`card checkin-card ${isCheckedIn ? 'checked-in' : ''}`}>
      <div className="card-content">
        {isCheckedIn ? (
          // 打卡成功状态
          <div className="checkin-success">
            <div className="success-icon">✓</div>
            <h2>打卡成功！</h2>
            <div className="success-message">
              <p>今日打卡已完成</p>
              <p className="check-date">{checkedInData?.date}</p>
            </div>
            
            <div className="checkin-result">
              <div className="result-rating">
                <p>心情评分：</p>
                <div className="stars display-only">
                  {[1, 2, 3, 4, 5].map(star => (
                    <span
                      key={star}
                      className={`star ${checkedInData?.rating >= star ? 'selected' : ''}`}
                    >
                      ★
                    </span>
                  ))}
                </div>
              </div>
              
              {checkedInData?.tags && checkedInData.tags.length > 0 && (
                <div className="result-tags">
                  <p>选择的标签：</p>
                  <div className="tags-display">
                    {checkedInData.tags.map((tag, index) => (
                      <span key={index} className="tag-item selected">
                        {tag}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
            
            <button 
              className="restart-btn"
              onClick={handleRestart}
            >
              重新打卡
            </button>
          </div>
        ) : (
          // 正常打卡状态
          <>
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
          </>
        )}
      </div>
    </div>
  );
};

export default CheckInCard;