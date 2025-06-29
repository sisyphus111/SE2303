import React, { useState, useEffect, useRef } from 'react';
import '../css/NewsCarousel.css';

const NewsCarousel = ({ articles = [] }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(true);
  const intervalRef = useRef(null);

  const handleArticleClick = (article) => {
    console.log('点击文章:', article.title);
    // 可以跳转到文章详情页面
  };

  // 自动轮播功能
  useEffect(() => {
    if (articles.length <= 1) return;

    if (isAutoPlaying) {
      intervalRef.current = setInterval(() => {
        setCurrentIndex(prev => (prev + 1) % articles.length);
      }, 4000); // 每4秒切换一次
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [articles.length, isAutoPlaying]);

  // 手动切换到指定索引
  const goToSlide = (index) => {
    setCurrentIndex(index);
  };

  // 鼠标悬停暂停自动播放
  const handleMouseEnter = () => {
    setIsAutoPlaying(false);
  };

  // 鼠标离开恢复自动播放
  const handleMouseLeave = () => {
    setIsAutoPlaying(true);
  };

  if (articles.length === 0) {
    return (
      <div className="news-section">
        <div className="news-empty">暂无推送内容</div>
      </div>
    );
  }

  return (
    <div className="news-section">
      <div 
        className="carousel-container"
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        {/* 轮播内容 */}
        <div className="carousel-wrapper">
          <div 
            className="carousel-track"
            style={{
              transform: `translateX(-${currentIndex * 100}%)`,
              width: `${articles.length * 100}%`
            }}
          >
            {articles.map((article, index) => (
              <div 
                key={article.id || index} 
                className={`carousel-slide ${index === currentIndex ? 'active' : ''}`}
                onClick={() => handleArticleClick(article)}
              >
                <div className="article-cover">
                  <img 
                    src={article.thumbnail} 
                    alt={article.title} 
                    className="cover-image"
                    onError={(e) => {
                      e.target.src = "https://via.placeholder.com/600x300/667eea/white?text=Article+Cover";
                    }}
                  />
                  <div className="article-title-overlay">
                    <h3>{article.title}</h3>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* 指示器 */}
        {articles.length > 1 && (
          <div className="carousel-indicators">
            {articles.map((_, index) => (
              <button
                key={index}
                className={`indicator ${index === currentIndex ? 'active' : ''}`}
                onClick={() => goToSlide(index)}
                aria-label={`第${index + 1}张`}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default NewsCarousel;
