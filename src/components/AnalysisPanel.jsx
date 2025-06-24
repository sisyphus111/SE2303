import React from 'react';
import '../css/AnalysisPanel.css';

const AnalysisPanel = ({ analysisData }) => {
  return (
    <div className="feature-item analysis">
      <div className="analysis-header">
        <span>智能分析</span>
      </div>
      
      <div className="analysis-content">
        {analysisData || '正在分析您的情绪数据...'}
      </div>
    </div>
  );
};

export default AnalysisPanel;