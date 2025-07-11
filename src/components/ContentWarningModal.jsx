import React from 'react';
import '../css/ContentWarningModal.css';

const ContentWarningModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="content-warning-overlay">
      <div className="content-warning-modal">
        <div className="warning-header">
          <div className="warning-icon">💚</div>
          <h2>我们关心你的感受</h2>
        </div>
        
        <div className="warning-content">
          <p className="warning-message">
            亲爱的朋友，我们注意到你的日记内容可能需要一些关注。
          </p>
          
          <div className="support-message">
            <h3>请记住：</h3>
            <ul>
              <li>你并不孤单，我们都在这里支持你</li>
              <li>专业的心理咨询师即将与你联系</li>
              <li>你的感受是重要的，值得被倾听和理解</li>
            </ul>
          </div>
          
          <div className="contact-info">
            <h3>立即获得帮助：</h3>
            <div className="phone-number">
              <span className="phone-icon">📞</span>
              <span className="phone-text">心理咨询热线：400-123-4567</span>
            </div>
            <p className="contact-note">
              我们会尽快安排专业的心理咨询师与你联系，为你提供支持和帮助。
            </p>
          </div>
        </div>
        
        <div className="warning-actions">
          <button className="close-btn" onClick={onClose}>
            我知道了
          </button>
        </div>
      </div>
    </div>
  );
};

export default ContentWarningModal; 