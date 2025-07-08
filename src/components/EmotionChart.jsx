import React, { useRef, useEffect, useState } from 'react';
import '../css/EmotionChart.css';

const EmotionChart = ({ data = [] }) => {
  const containerRef = useRef(null);
  const [dimensions, setDimensions] = useState({ width: 300, height: 180 });

  useEffect(() => {
    const updateDimensions = () => {
      if (containerRef.current) {
        const { width, height } = containerRef.current.getBoundingClientRect();
        // 减去padding和边框，获取实际可用空间
        const actualWidth = width - 40; // 减去左右padding
        const actualHeight = height - 60; // 减去上下padding和标签空间
        setDimensions({ width: actualWidth, height: actualHeight });
      }
    };

    // 初始化尺寸
    updateDimensions();

    // 监听窗口大小变化
    window.addEventListener('resize', updateDimensions);
    
    // 使用ResizeObserver监听容器尺寸变化（更精确）
    const resizeObserver = new ResizeObserver(updateDimensions);
    if (containerRef.current) {
      resizeObserver.observe(containerRef.current);
    }

    return () => {
      window.removeEventListener('resize', updateDimensions);
      resizeObserver.disconnect();
    };
  }, []);

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
  
  // 使用实际容器尺寸作为viewBox尺寸，避免拉伸
  const chartWidth = dimensions.width;
  const chartHeight = dimensions.height;

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

  // 根据容器大小动态计算数据点大小
  const getPointRadius = () => {
    const baseRadius = 4;
    const scaleFactor = Math.min(chartWidth / 300, chartHeight / 180);
    return Math.max(3, baseRadius * scaleFactor);
  };

  // 根据容器大小动态计算线条粗细
  const getStrokeWidth = () => {
    const baseWidth = 2.5;
    const scaleFactor = Math.min(chartWidth / 300, chartHeight / 180);
    return Math.max(1.5, baseWidth * scaleFactor);
  };

  // 根据容器大小动态计算文字大小
  const getFontSize = () => {
    const baseFontSize = 12;
    const scaleFactor = Math.min(chartWidth / 300, chartHeight / 180);
    return Math.max(10, baseFontSize * scaleFactor);
  };

  // 动态计算网格密度
  const getGridSize = () => {
    const baseGridWidth = Math.max(20, chartWidth / 15);
    const baseGridHeight = Math.max(10, chartHeight / 18);
    return { width: baseGridWidth, height: baseGridHeight };
  };

  const gridSize = getGridSize();

  return (
    <div className="feature-item chart-container">
      <span>情绪趋势图</span>
      <div className="emotion-chart">
        <div className="line-chart-container" ref={containerRef}>
          <svg 
            className="line-chart" 
            viewBox={`0 0 ${chartWidth} ${chartHeight}`}
            preserveAspectRatio="xMidYMid meet"
            style={{ width: '100%', height: 'calc(100% - 40px)' }}
          >
            {/* 定义渐变和滤镜 */}
            <defs>
              {/* 动态网格图案 */}
              <pattern 
                id="grid" 
                width={gridSize.width} 
                height={gridSize.height} 
                patternUnits="userSpaceOnUse"
              >
                <path 
                  d={`M ${gridSize.width} 0 L 0 0 0 ${gridSize.height}`} 
                  fill="none" 
                  stroke="#f0f0f0" 
                  strokeWidth="0.5"
                />
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
                  strokeWidth="0.5"
                  strokeDasharray="3,6"
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
              strokeWidth={getStrokeWidth()}
              strokeLinecap="round"
              strokeLinejoin="round"
            />
            
            {/* 数据点 */}
            {data.map((item, index) => {
              const point = getPointCoordinates(item.value, index);
              const radius = getPointRadius();
              return (
                <g key={index} className="data-point-group">
                  {/* 主数据点 - 使用圆形，避免拉伸 */}
                  <circle
                    cx={point.x}
                    cy={point.y}
                    r={radius}
                    fill={getPointColor(item.value)}
                    stroke="#ffffff"
                    strokeWidth={Math.max(1, radius * 0.4)}
                    className="data-point"
                  />
                  
                  {/* 数值标签 */}
                  <text
                    x={point.x}
                    y={point.y - radius - 8}
                    textAnchor="middle"
                    className="point-value"
                    fill="#2c3e50"
                    fontSize={getFontSize()}
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
            {[5, 4, 3, 2, 1].map(value => (
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