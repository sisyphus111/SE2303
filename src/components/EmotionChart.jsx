import React from 'react';
import '../css/EmotionChart.css';

const EmotionChart = ({ data = [] }) => {
  if (data.length === 0) {
    return (
      <div className="feature-item chart-container">
        <span>情绪趋势图</span>
        <div className="chart-placeholder">暂无数据</div>
      </div>
    );
  }

  const maxValue = 5; // 情绪评分最大值
  const minValue = 1; // 情绪评分最小值
  const chartWidth = 300; // 增加图表宽度，让它更宽
  const chartHeight = 180; // 相对较小的高度，形成宽扁的比例

  // 计算折线图的点坐标
  const getPointCoordinates = (value, index) => {
    const x = (index / (data.length - 1)) * chartWidth;
    const y = chartHeight - ((value - minValue) / (maxValue - minValue)) * chartHeight;
    return { x, y };
  };

  // 生成SVG路径
  const generatePath = () => {
    if (data.length === 0) return '';
    
    let path = '';
    data.forEach((item, index) => {
      const point = getPointCoordinates(item.value, index);
      if (index === 0) {
        path += `M ${point.x} ${point.y}`;
      } else {
        path += ` L ${point.x} ${point.y}`;
      }
    });
    return path;
  };

  // 生成渐变填充路径
  const generateAreaPath = () => {
    if (data.length === 0) return '';
    
    const linePath = generatePath();
    const firstPoint = getPointCoordinates(data[0].value, 0);
    const lastPoint = getPointCoordinates(data[data.length - 1].value, data.length - 1);
    
    return `${linePath} L ${lastPoint.x} ${chartHeight} L ${firstPoint.x} ${chartHeight} Z`;
  };

  // 根据情绪值获取颜色
  const getPointColor = (value) => {
    if (value >= 4) return '#4CAF50';
    if (value >= 3) return '#FFC107';
    return '#F44336';
  };

  return (
    <div className="feature-item chart-container">
      <span>情绪趋势图</span>
      <div className="emotion-chart">
        <div className="line-chart-container">
          <svg 
            className="line-chart" 
            viewBox={`0 0 ${chartWidth} ${chartHeight}`}
            preserveAspectRatio="xMidYMid meet"
          >
            {/* 定义渐变和滤镜 */}
            <defs>
              {/* 网格图案 */}
              <pattern id="grid" width="20" height="8" patternUnits="userSpaceOnUse">
                <path d="M 20 0 L 0 0 0 8" fill="none" stroke="#f0f0f0" strokeWidth="0.5"/>
              </pattern>
              
              {/* 区域填充渐变 */}
              <linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stopColor="#667eea" stopOpacity="0.4"/>
                <stop offset="50%" stopColor="#667eea" stopOpacity="0.2"/>
                <stop offset="100%" stopColor="#667eea" stopOpacity="0.05"/>
              </linearGradient>
            </defs>
            
            {/* 背景网格 */}
            <rect width="100%" height="100%" fill="url(#grid)" opacity="0.6"/>
            
            {/* Y轴参考线 */}
            {[1, 2, 3, 4, 5].map(value => {
              const y = chartHeight - ((value - minValue) / (maxValue - minValue)) * chartHeight;
              return (
                <line
                  key={value}
                  x1="0"
                  y1={y}
                  x2={chartWidth}
                  y2={y}
                  stroke="#e0e0e0"
                  strokeWidth="0.3"
                  strokeDasharray="2,4"
                  opacity="0.7"
                />
              );
            })}
            
            {/* 填充区域 */}
            <path
              d={generateAreaPath()}
              fill="url(#areaGradient)"
            />
            
            {/* 折线 */}
            <path
              d={generatePath()}
              fill="none"
              stroke="#667eea"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
            
            {/* 数据点 */}
            {data.map((item, index) => {
              const point = getPointCoordinates(item.value, index);
              return (
                <g key={index} className="data-point-group">
                  {/* 主数据点 - 使用椭圆形来在viewBox中保持视觉上的圆形 */}
                  <ellipse
                    cx={point.x}
                    cy={point.y}
                    rx="3"
                    ry="7.5"
                    fill={getPointColor(item.value)}
                    stroke="#ffffff"
                    strokeWidth="1.5"
                    className="data-point"
                  />
                  
                  {/* 数值标签 */}
                  <text
                    x={point.x}
                    y={point.y - 15}
                    textAnchor="middle"
                    className="point-value"
                    fill="#2c3e50"
                    fontSize="8"
                  >
                    {item.value}
                  </text>
                </g>
              );
            })}
          </svg>
          
          {/* X轴标签 */}
          <div className="x-axis-labels">
            {data.map((item, index) => (
              <span key={index} className="x-label">
                {item.date}
              </span>
            ))}
          </div>
          
          {/* Y轴标签 */}
          <div className="y-axis-labels">
            {[1, 2, 3, 4, 5].map(value => (
              <span key={value} className="y-label" style={{
                bottom: `${((value - minValue) / (maxValue - minValue)) * 100}%`
              }}>
                {value}
              </span>
            ))}
          </div>
        </div>
        
        {/* 图例 */}
        <div className="chart-legend">
          <div className="legend-item">
            <div className="legend-color good"></div>
            <span>心情好 (4-5分)</span>
          </div>
          <div className="legend-item">
            <div className="legend-color normal"></div>
            <span>一般 (3分)</span>
          </div>
          <div className="legend-item">
            <div className="legend-color bad"></div>
            <span>心情不好 (1-2分)</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EmotionChart; 