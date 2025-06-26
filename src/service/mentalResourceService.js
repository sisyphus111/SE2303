// 心理健康资源相关API服务
import { request } from './apiUtils';

/**
 * 获取轮播图数据
 * @param {string} type - 轮播图类型（banner, resource, article等）
 * @returns {Promise<Array>} 轮播图列表
 */
export const getCarouselImages = async (type = 'resource') => {
  return request(`/resources/carousel?type=${type}`);
};

/**
 * 获取推荐文章列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.category - 文章分类
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise<Object>} 文章列表
 */
export const getRecommendedArticles = async (params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return request(`/resources/articles?${queryString}`);
};

/**
 * 获取文章详情
 * @param {string} articleId - 文章ID
 * @returns {Promise<Object>} 文章详情
 */
export const getArticleDetail = async (articleId) => {
  return request(`/resources/articles/${articleId}`);
};

/**
 * 获取音乐列表
 * @param {Object} params - 查询参数
 * @param {string} params.id - 音乐ID
 * @param {string} params.title - 音乐标题
 * @param {string} params.cover - 封面图片
 * @param {string} params.description - 音乐描述
 * @param {string} params.url - 音乐URL
 * @returns {Promise<Object>} 音乐列表
 */
export const getMusicList = async (params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return request(`/resources/music?${queryString}`);
};

/**
 * 获取音乐详情
 * @param {string} musicId - 音乐ID
 * @returns {Promise<Object>} 音乐详情
 */
export const getMusicDetail = async (musicId) => {
  return request(`/resources/music/${musicId}`);
};

/**
 * 记录音乐播放
 * @param {string} musicId - 音乐ID
 * @param {number} duration - 播放时长（秒）
 * @returns {Promise<Object>} 记录结果
 */
export const recordMusicPlay = async (musicId, duration) => {
  return request('/resources/music/play', {
    method: 'POST',
    body: JSON.stringify({ musicId, duration }),
  });
};

/**
 * 获取资源分类
 * @returns {Promise<Array>} 分类列表
 */
export const getResourceCategories = async () => {
  return request('/resources/categories');
};

/**
 * 获取心理健康资源数据（模拟函数，用于前端测试）
 * @returns {Promise<Object>} 资源数据
 */
export const getMentalResourceData = async () => {
  // 模拟API调用延迟
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return {
    carouselImages: [
      "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80",
    ],
    articles: [
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
      {
        id: "3",
        title: "冥想入门指南",
        description: "从零开始学习冥想，改善心理健康状态。",
        cover: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=400&q=80",
        link: "#",
      }
    ],
    musics: [
      {
        id: "1",
        title: "轻松冥想",
        description: "适合冥想和放松的轻音乐",
        cover: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=120&q=80",
        url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
      },
      {
        id: "2",
        title: "舒缓钢琴", 
        description: "轻柔的钢琴曲，帮助缓解压力",
        cover: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80",    
        url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
      },
      {
        id: "3",
        title: "自然白噪音",
        description: "雨声、风声等自然声音，帮助睡眠",
        cover: "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=120&q=80",
        url: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
      }
    ]
  };
};

/**
 * 获取文章详情数据（模拟函数）
 * @param {string} articleId - 文章ID
 * @returns {Promise<Object>} 文章详情
 */
export const getArticleDetailData = async (articleId) => {
  // 模拟API调用延迟
  await new Promise(resolve => setTimeout(resolve, 300));
  
  const articles =  [
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
  
  return articles[articleId] || null;
}; 