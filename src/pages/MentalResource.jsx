import React from "react";
import { Card, Typography } from "antd";
import MentalCarousel from "../components/MentalCarousel";
import ArticleList from "../components/ArticleList";
import MusicList from "../components/MusicList";
import "../css/MentalResource.css";

const { Title } = Typography;

const carouselImages = [
  "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80",
];

const articles = [
  {
    title: "如何缓解焦虑情绪",
    description: "掌握科学方法，帮助你有效应对生活中的焦虑与压力。",
    cover: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    link: "#",
  },
  {
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
    url: "#",
  },
  {
    title: "舒缓钢琴",
    artist: "Peaceful Piano",
    cover: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80",    
    url: "#",
  },
];

function MentalResource() {
  return (
    <div className="mental-resource-bg">
      <Card className="mental-resource-card mental-resource-carousel-card">
        <MentalCarousel images={carouselImages} />
      </Card>

      <Card className="mental-resource-card mental-resource-card-body">
        <Title level={3} className="mental-resource-title">
          文章推荐
        </Title>
        <ArticleList articles={articles} />
      </Card>

      <Card className="mental-resource-card mental-resource-card-body">
        <Title level={3} className="mental-resource-title">
          音乐推荐
        </Title>
        <MusicList musics={musics} />
      </Card>
    </div>
  );
}

export default MentalResource;