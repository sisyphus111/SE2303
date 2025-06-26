import React, { useState, useEffect } from "react";
import { Card, Typography } from "antd";
import MentalCarousel from "../components/MentalCarousel";
import ArticleList from "../components/ArticleList";
import MusicList from "../components/MusicList";
import { useNavigate } from "react-router-dom";

import "../css/MentalResource.css";

// API服务导入注释
// import { getCarouselImages, getRecommendedArticles, getMusicList, recordMusicPlay } from '../service/mentalResourceService';

const { Title } = Typography;

const carouselImages = [
  "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80",
];

const articles = [
  {
    id: "1",
    title: "如何缓解焦虑情绪",
    description: "掌握科学方法，帮助你有效应对生活中的焦虑与压力。",
    cover: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    link: "#",
  },
  {
    id: "2",
    title: "心理健康的五个小习惯",
    description: "每天坚持这些小习惯，让你的心理更健康。",
    cover: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80",
    link: "#",
  },
];

const musics = [
  {
    title: "轻松冥想",
    artist: "Calm Artist",
    cover: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=120&q=80",
    url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", 
  },
  {
    title: "舒缓钢琴",
    artist: "Peaceful Piano",
    cover: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80",    
    url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
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
      
      // API调用注释：获取轮播图数据
      // const carouselRes = await getCarouselImages('resource');
      // setCarouselData(carouselRes.data);
      
      // API调用注释：获取推荐文章列表
      // const articlesRes = await getRecommendedArticles({
      //   page: 1,
      //   size: 10,
      //   category: 'mental-health'
      // });
      // setArticlesData(articlesRes.data);
      
      // API调用注释：获取音乐列表
      // const musicRes = await getMusicList({
      //   page: 1,
      //   size: 20
      // });
      // setMusicData(musicRes.data);
      
    } catch (error) {
      console.error('加载资源数据失败:', error);
      // 使用默认数据作为备选
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
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="mental-resource-bg">
      <Card className="mental-resource-card mental-resource-carousel-card">
        <MentalCarousel images={carouselData} />
      </Card>

      <Card className="mental-resource-card mental-resource-card-body">
        <Title level={3} className="mental-resource-title">
          文章推荐
        </Title>
        <ArticleList articles={articlesData} onArticleClick={handleArticleClick}/>
      </Card>

      <Card className="mental-resource-card mental-resource-card-body">
        <Title level={3} className="mental-resource-title">
          音乐推荐
        </Title>
        <MusicList musics={musicData} onMusicPlay={handleMusicPlay} />
      </Card>
    </div>
  );
}

export default MentalResource;