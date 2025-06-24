import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/DiaryCard.css';

const DiaryCard = () => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/diary');
  };

  return (
    <div className="card diary-card" onClick={handleClick}>
      <div className="card-content">
        <h2>文字日记</h2>
        <p>记录你的心情和想法</p>
        <div className="diary-icon">📝</div>
        <div className="diary-arrow">→</div>
      </div>
    </div>
  );
};

export default DiaryCard;
