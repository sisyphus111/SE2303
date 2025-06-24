import React, { useState, useEffect } from 'react';
import { getEmergencyStatus } from '../service/homeService';
import '../css/EmergencyAlert.css';

const EmergencyAlert = () => {
  const [status, setStatus] = useState('normal');
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadEmergencyStatus();
  }, []);

  const loadEmergencyStatus = async () => {
    try {
      const data = await getEmergencyStatus();
      setStatus(data.status);
      setMessage(data.message);
    } catch (error) {
      console.error('获取预警状态失败:', error);
    }
  };

  const getStatusText = () => {
    switch (status) {
      case 'warning':
        return '需要关注';
      case 'danger':
        return '紧急预警';
      default:
        return '正常';
    }
  };

  return (
    <div className="feature-item emergency">
      <span>紧急预警</span>
      <div className="emergency-status">
        <span className={`status-indicator ${status}`}>
          {getStatusText()}
        </span>
        {message && (
          <div className="emergency-message">
            {message}
          </div>
        )}
      </div>
    </div>
  );
};

export default EmergencyAlert;