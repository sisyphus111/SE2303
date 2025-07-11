import React from 'react';
import '../css/AnalysisPanel.css';

const AnalysisPanel = ({ analysisData, analysisLoading = false }) => {
  return (
    <div className="feature-item analysis">
      <div className="analysis-header">
        <span>智能分析</span>
      </div>
      
      <div className="analysis-content">
        {analysisLoading ? (
          <div className="loading-placeholder">
            <div className="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <p>正在分析您的情绪数据...</p>
          </div>
        ) : analysisData ? (
          <div className="analysis-text">
            {analysisData}
          </div>
        ) : (
          <div className="no-data">
            <p>暂无分析数据</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AnalysisPanel;