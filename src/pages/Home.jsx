import React, { useState, useEffect } from 'react';
import NewsCarousel from '../components/NewsCarousel';
import CheckInCard from '../components/CheckInCard';
import DiaryCard from '../components/DiaryCard';
import EmergencyAlert from '../components/EmergencyAlert';
import EmotionChart from '../components/EmotionChart';
import AnalysisPanel from '../components/AnalysisPanel';
import { getArticles, getMoodData, getAnalysisData } from '../service/homeService';
import '../css/Home.css';

export const Home = () => {
  const [articles, setArticles] = useState([]);
  const [moodData, setMoodData] = useState([]);
  const [analysisData, setAnalysisData] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [articlesRes, moodRes, analysisRes] = await Promise.all([
        getArticles(),
        getMoodData(),
        getAnalysisData()
      ]);
      
      setArticles(articlesRes || []);
      setMoodData(moodRes || []);
      setAnalysisData(analysisRes || '');
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDataUpdate = () => {
    loadData(); // 当子组件需要刷新数据时调用
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="home-container">
      {/* 推送新闻轮播 */}
      <NewsCarousel articles={articles} />

      {/* 功能卡片区域 */}
      <div className="cards-section">
        <CheckInCard onCheckInSuccess={handleDataUpdate} />
        <DiaryCard />
      </div>

      {/* 功能列表区域 */}
      <div className="features-section">
        <EmergencyAlert />
        <EmotionChart data={moodData} />
        <AnalysisPanel analysisData={analysisData} />
      </div>
    </div>
  );
};