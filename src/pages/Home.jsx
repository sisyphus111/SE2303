import React, { useState, useEffect } from 'react';
import NewsCarousel from '../components/NewsCarousel';
import CheckInCard from '../components/CheckInCard';
import DiaryCard from '../components/DiaryCard';
import EmergencyAlert from '../components/EmergencyAlert';
import EmotionChart from '../components/EmotionChart';
import AnalysisPanel from '../components/AnalysisPanel';
import { getArticles, getMoodData, getAnalysisData } from '../service/homeService';
import { useUser } from '../lib/UserContext';
import '../css/Home.css';

// API服务导入注释（已导入homeService）
// import { getEmergencyStatus, submitCheckIn } from '../service/homeService';

export const Home = () => {
  const { userProfile } = useUser();
  const [articles, setArticles] = useState([]);
  const [moodData, setMoodData] = useState([]);
  const [analysisData, setAnalysisData] = useState('');
  const [loading, setLoading] = useState(true);
  const [emergencyStatus, setEmergencyStatus] = useState({ status: 'normal', message: '' });

  useEffect(() => {
    if (userProfile) {
      loadData();
    }
  }, [userProfile]);

  const loadData = async () => {
    try {
      setLoading(true);
      
      // API调用注释：获取文章列表
      // const articlesRes = await getArticles();
      
      // API调用注释：获取心情数据
      // const moodRes = await getMoodData();
      
      // API调用注释：获取分析数据
      // const analysisRes = await getAnalysisData();
      
      // API调用注释：获取预警状态
      // const emergencyRes = await getEmergencyStatus();

      const [articlesRes, moodRes, analysisRes] = await Promise.all([
        getArticles(),
        getMoodData(userProfile?.userId || 1), // 使用真实用户ID，如果没有则使用1作为后备
        getAnalysisData()
      ]);
      
      setArticles(articlesRes || []);
      setMoodData(moodRes || []);
      setAnalysisData(analysisRes || '');
      // setEmergencyStatus(emergencyRes || { status: 'normal', message: '' });
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 提交打卡的函数
  const handleCheckIn = async (checkInData) => {
    // API调用注释：提交打卡
    // try {
    //   const response = await submitCheckIn(checkInData);
    //   console.log('打卡成功:', response);
    //   // 重新加载数据
    //   loadData();
    // } catch (error) {
    //   console.error('打卡失败:', error);
    //   throw error;
    // }
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
        <EmergencyAlert status={emergencyStatus} />
        <EmotionChart data={moodData} />
        <AnalysisPanel analysisData={analysisData} />
      </div>
    </div>
  );
};