import React from 'react';
import { Modal } from 'antd';
import { HeartOutlined, SafetyOutlined, TeamOutlined } from '@ant-design/icons';
import './ContentWarningModal.css';

const ContentWarningModal = ({ isOpen, onClose }) => {
  return (
    <Modal
      title={
        <div className="modal-title">
          <SafetyOutlined className="title-icon" />
          <span>内容安全提醒</span>
        </div>
      }
      open={isOpen}
      onCancel={onClose}
      footer={null}
      width={500}
      centered
      className="content-warning-modal"
    >
      <div className="modal-content">
        <div className="warning-icon">
          <HeartOutlined />
        </div>
        
        <h3 className="warning-title">我们关心你的心理健康</h3>
        
        <div className="warning-message">
          <p>我们检测到你的内容可能包含一些需要关注的内容。</p>
          <p>请记住，你并不孤单，我们都在这里支持你。</p>
        </div>
        
        <div className="support-info">
          <div className="support-item">
            <TeamOutlined className="support-icon" />
            <div className="support-text">
              <strong>专业心理咨询师</strong>
              <span>将在24小时内联系你</span>
            </div>
          </div>
        </div>
        
        <div className="encouragement">
          <p>💝 每一个困难都是成长的机会</p>
          <p>🌟 你比想象中更坚强</p>
          <p>🤝 我们与你同在</p>
        </div>
        
        <div className="modal-actions">
          <button className="close-button" onClick={onClose}>
            我知道了
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default ContentWarningModal; 