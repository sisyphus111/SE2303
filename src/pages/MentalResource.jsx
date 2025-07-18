import React, { useState, useEffect } from "react";
import { Card, Typography, Space, Row, Col, Divider } from "antd";
import { HeartOutlined, BookOutlined, SoundOutlined, BulbOutlined } from "@ant-design/icons";
import MentalCarousel from "../components/MentalCarousel";
import ArticleList from "../components/ArticleList";
import MusicList from "../components/MusicList";
import { useNavigate } from "react-router-dom";

import "../css/MentalResource.css";

// API服务导入注释
import { getCarouselImages, getRecommendedArticles, getMusicList, recordMusicPlay } from '../service/mentalResourceService';

const { Title, Paragraph } = Typography;

const carouselImages = [
  "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80",
];

const articles = [
  {
    id: "1",
    title: "如何保持良好的心理健康",
    description: "分享一些日常保持心理健康的小技巧，包括规律作息、适度运动、保持社交联系等实用方法。",
    cover: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    link: "#",
  },
  {
    id: "2",
    title: "压力管理的有效方法",
    description: "学会管理生活和工作中的压力，通过时间管理、放松技巧和心态调整来应对挑战。",
    cover: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80",
    link: "#",
  },
];

const musics = [
  {
    title: "森林白噪音",
    artist: "Nature Sounds",
    cover: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=120&q=80",
    url: "example.invalid/birdsing.mp3", // 测试音频
  },
  {
    title: "舒缓钢琴",
    artist: "Piano Music",
    cover: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80",
    url: "example.invalid/piano.mp3", // 测试音频
  },
];

function MentalResource() {
  const navigate = useNavigate();
  const [carouselData, setCarouselData] = useState(carouselImages);
  const [articlesData, setArticlesData] = useState(articles);
  const [musicData, setMusicData] = useState(musics);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadResourceData();
  }, []);

  // 加载资源数据的函数
  const loadResourceData = async () => {
    try {
      setLoading(true);
      // 轮播图
      const carouselRes = await getCarouselImages();
      if (carouselRes && Array.isArray(carouselRes) && carouselRes.length > 0) {
        const formattedCarouselData = carouselRes.map(item => item.imageUrl);
        setCarouselData(formattedCarouselData);
      } else {
        setCarouselData(carouselImages);
      }
      // 推荐文章
      const articlesRes = await getRecommendedArticles({ page: 1, size: 10, category: 'mental-health' });
      setArticlesData(Array.isArray(articlesRes.list) ? articlesRes.list : (Array.isArray(articlesRes) ? articlesRes : []));
      // 音乐
      const musicRes = await getMusicList({ page: 1, size: 20 });
      setMusicData(Array.isArray(musicRes.list) ? musicRes.list : (Array.isArray(musicRes) ? musicRes : []));
    } catch (error) {
      console.error('加载资源数据失败:', error);
      setCarouselData(carouselImages);
      setArticlesData(articles);
      setMusicData(musics);
    } finally {
      setLoading(false);
    }
  };

  // 记录音乐播放的函数
  const handleMusicPlay = async (musicId, duration) => {
    // API调用注释：记录音乐播放
    // try {
    //   await recordMusicPlay(musicId, duration);
    //   console.log('音乐播放记录成功');
    // } catch (error) {
    //   console.error('记录音乐播放失败:', error);
    // }
  };

  const handleArticleClick = (id) => {
    navigate(`/article/${id}`);
  };

  if (loading) {
    return <div className="mental-resource-loading">
      <div className="loading-spinner"></div>
      <p>加载中...</p>
    </div>;
  }

  return (
    <div className="mental-resource-container">
      {/* 页面头部 */}
      <div className="mental-resource-header">
        <div className="mental-resource-header-content">
          <div className="mental-resource-title-section">
            <HeartOutlined className="mental-resource-main-icon" />
            <Title level={1} className="mental-resource-main-title">
              心理健康资源
            </Title>
            <Paragraph className="mental-resource-subtitle">
              发现有助于心理健康的文章、音乐和专业指导，让心灵得到滋养与成长
            </Paragraph>
          </div>
        </div>
      </div>

      {/* 主要内容区域 */}
      <div className="mental-resource-content">
        <Row gutter={[24, 24]}>
          <Col xs={24}>
            {/* 轮播图区域 */}
            <Card className="mental-resource-card carousel-card">
              <div className="mental-resource-card-header">
                <BulbOutlined className="section-icon" />
                <Title level={3} className="section-title">每日心理小贴士</Title>
              </div>
              <MentalCarousel images={carouselData} />
            </Card>
          </Col>
        </Row>

        <Row gutter={[24, 24]}>
          <Col xs={24} lg={12}>
            {/* 文章推荐区域 */}
            <Card className="mental-resource-card article-card">
              <div className="mental-resource-card-header">
                <BookOutlined className="section-icon" />
                <Title level={3} className="section-title">精选文章</Title>
                <Paragraph className="section-description">
                  专业心理健康知识，助你更好地了解自己
                </Paragraph>
              </div>
              <Divider className="section-divider" />
              <ArticleList articles={articlesData} onArticleClick={handleArticleClick}/>
            </Card>
          </Col>

          <Col xs={24} lg={12}>
            {/* 音乐推荐区域 */}
            <Card className="mental-resource-card music-card">
              <div className="mental-resource-card-header">
                <SoundOutlined className="section-icon" />
                <Title level={3} className="section-title">舒缓音乐</Title>
                <Paragraph className="section-description">
                  放松心情，让音乐治愈你的心灵
                </Paragraph>
              </div>
              <Divider className="section-divider" />
              <MusicList musics={musicData} onMusicPlay={handleMusicPlay} />
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );
}

export default MentalResource;