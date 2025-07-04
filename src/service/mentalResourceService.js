// 心理健康资源相关API服务
import { request } from './apiUtils';
import {get} from "./common";

/**
 * 获取轮播图数据
 * @param {string} category - 轮播图分类（可选，如 'resource', 'banner' 等）
 * @returns {Promise<Array>} 轮播图列表
 */
export const getCarouselImages = async (category = null) => {
  try {
    let url = '/notification/carousel/all';
    
    // 如果指定了分类，使用带分类参数的端点
    if (category) {
      url = `/notification/carousel/list?category=${encodeURIComponent(category)}`;
    }
    
    const response = await get(url);
    return response;
  } catch (error) {
    console.error('获取轮播图数据失败:', error);
    // 不抛出错误，让调用方处理
    return null;
  }
};

// 默认推荐文章数据
export const defaultArticles = [
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

/**
 * 获取推荐文章列表，优先用后端，失败时用默认
 * @param {Object} params - 查询参数
 * @returns {Promise<Array>} 文章列表
 */
export const getRecommendedArticles = async (params = { page: 1, size: 10, category: 'mental-health' }) => {
  try {
    let url = '/notification/articles/recommended';
    // 如果有参数，拼接到url
    const queryString = new URLSearchParams(params).toString();
    if (queryString) {
      url += `?${queryString}`;
    }
    const response = await get(url);
    // response 应该是数组
    if (Array.isArray(response) && response.length > 0) {
      return response;
    }
    return defaultArticles;
  } catch (error) {
    // 失败时兜底
    return defaultArticles;
  }
};

/**
 * 获取文章详情
 * @param {string} articleId - 文章ID
 * @returns {Promise<Object>} 文章详情
 */
export const getArticleDetail = async (articleId) => {
  return request(`/resources/articles/${articleId}`);
};

// 默认音乐数据
export const defaultMusics = [
  {
    title: "森林白噪音",
    artist: "Nature Sounds",
    cover: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=120&q=80",
    url: "example.invalid/birdsing.mp3",
  },
  {
    title: "舒缓钢琴",
    artist: "Piano Music",
    cover: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80",
    url: "example.invalid/piano.mp3",
  },
];

/**
 * 获取音乐列表，优先用后端，失败时用默认
 * @param {Object} params - 查询参数
 * @returns {Promise<Array>} 音乐列表
 */
export const getMusicList = async (params = { page: 1, size: 20 }) => {
  try {
    let url = '/notification/music/list';
    const queryString = new URLSearchParams(params).toString();
    if (queryString) {
      url += `?${queryString}`;
    }
    const response = await get(url);
    if (Array.isArray(response) && response.length > 0) {
      return response;
    }
    return defaultMusics;
  } catch (error) {
    return defaultMusics;
  }
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