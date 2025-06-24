import React from 'react';
import { Link } from 'react-router-dom';
import '../css/HomePage.css';

const HomePage = () => {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  
    return (
      <div className="homepage-container">
        <h1 className="homepage-title">心理健康社区</h1>
        <p className="homepage-subtitle">
          欢迎来到我们的心理健康社区，这里有温暖的陪伴和支持
        </p>
        
        <div className="homepage-buttons">
          <Link 
            to="/ai-chat" 
            className="homepage-button ai-chat"
          >
            🤖 AI陪伴对话
          </Link>
          
          <Link 
            to="/community" 
            className="homepage-button community"
          >
            👥 社区交流
          </Link>
          
          {isLoggedIn ? (
            <>
              <Link 
                to="/profile" 
                className="homepage-button profile"
              >
                👤 个人主页
              </Link>
              
              <Link 
                to="/diary" 
                className="homepage-button diary"
              >
                📝 写日记
              </Link>
            </>
          ) : (
            <Link 
              to="/login" 
              className="homepage-button login"
            >
              🔐 登录
            </Link>
          )}
          
          <div className="homepage-button disabled">
            📚 心理资源 (即将推出)
          </div>
        </div>
        
        <div className="homepage-footer">
          <p className="homepage-footer-main">
            我们致力于为每个人提供温暖的心理健康支持
          </p>
          <p className="homepage-footer-sub">
            记住，寻求帮助是勇敢的表现 💙
          </p>
        </div>
      </div>
    );
  }

  export default HomePage;