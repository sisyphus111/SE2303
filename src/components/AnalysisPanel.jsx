import React from 'react';
import ReactMarkdown from 'react-markdown';
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
          <div className="markdown-content">
            <ReactMarkdown 
              components={{
                // 自定义markdown组件样式
                p: ({ children }) => <p className="analysis-paragraph">{children}</p>,
                h1: ({ children }) => <h1 className="analysis-heading">{children}</h1>,
                h2: ({ children }) => <h2 className="analysis-heading">{children}</h2>,
                h3: ({ children }) => <h3 className="analysis-heading">{children}</h3>,
                ul: ({ children }) => <ul className="analysis-list">{children}</ul>,
                ol: ({ children }) => <ol className="analysis-list">{children}</ol>,
                li: ({ children }) => <li className="analysis-list-item">{children}</li>,
                strong: ({ children }) => <strong className="analysis-emphasis">{children}</strong>,
                em: ({ children }) => <em className="analysis-emphasis">{children}</em>,
                blockquote: ({ children }) => <blockquote className="analysis-quote">{children}</blockquote>,
                code: ({ children, className }) => {
                  const isInline = !className;
                  return isInline ? (
                    <code className="analysis-inline-code">{children}</code>
                  ) : (
                    <pre className="analysis-code-block">
                      <code>{children}</code>
                    </pre>
                  );
                },
              }}
            >
              {analysisData}
            </ReactMarkdown>
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