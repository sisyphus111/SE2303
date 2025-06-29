import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Typography, Button } from "antd";
import "../css/MentalResource.css"

// API服务导入注释
// import { getArticleDetail } from '../service/mentalResourceService';

const { Title, Paragraph } = Typography;

// 假设所有文章数据都在这里，实际项目可从接口获取
const articles = [
  {
    id: "1",
    title: "如何缓解焦虑情绪",
    description: "掌握科学方法，帮助你有效应对生活中的焦虑与压力。",
    content: "这是详细内容：焦虑是一种常见的情绪反应，可以通过呼吸训练、运动、心理疏导等方式缓解……",
    cover: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
  },
  {
    id: "2",
    title: "心理健康的五个小习惯",
    description: "每天坚持这些小习惯，让你的心理更健康。",
    content: "这是详细内容：1. 保持规律作息 2. 适度运动 3. 多与人交流 4. 学会放松 5. 保持积极心态……",
    cover: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80",
  },
];

function ArticleDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadArticleDetail();
  }, [id]);

  // 加载文章详情的函数
  const loadArticleDetail = async () => {
    try {
      setLoading(true);
      
      // API调用注释：获取文章详情
      // const response = await getArticleDetail(id);
      // setArticle(response.data);
      
      // 使用模拟数据作为备选
      const foundArticle = articles.find(a => a.id === id);
      setArticle(foundArticle);
      
    } catch (error) {
      console.error('获取文章详情失败:', error);
      // 使用模拟数据作为备选
      const foundArticle = articles.find(a => a.id === id);
      setArticle(foundArticle);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  if (!article) {
    return <div>文章不存在</div>;
  }

  return (
    <div className="mental-resource-bg">
      <Card className="mental-resource-card mental-resource-card-body">
        <Button onClick={() => navigate(-1)} className="mental-resource-detail-back-btn">返回</Button>
        <img src={article.cover} alt={article.title} className="mental-resource-detail-cover" />
        <Title level={2}>{article.title}</Title>
        <Paragraph type="secondary">{article.description}</Paragraph>
        <Paragraph>{article.content}</Paragraph>
      </Card>
    </div>
  );
}

export default ArticleDetail;