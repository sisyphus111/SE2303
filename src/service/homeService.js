import { get, post, PREFIX } from './common';

// 示例数据
const mockArticles = [
  {
    id: 1,
    title: "如何保持良好的心理健康",
    summary: "分享一些日常保持心理健康的小技巧，包括规律作息、适度运动、保持社交联系等实用方法。",
    thumbnail: "https://via.placeholder.com/800x400/4CAF50/white?text=心理健康"
  },
  {
    id: 2,
    title: "压力管理的有效方法",
    summary: "学会管理生活和工作中的压力，通过时间管理、放松技巧和心态调整来应对挑战。",
    thumbnail: "https://via.placeholder.com/800x400/2196F3/white?text=压力管理"
  },
  {
    id: 3,
    title: "冥想与放松技巧",
    summary: "通过冥想和放松练习改善心情，每天10分钟的正念练习就能带来显著的心理健康改善。",
    thumbnail: "https://via.placeholder.com/800x400/FF9800/white?text=冥想放松"
  },
  {
    id: 4,
    title: "建立健康的睡眠习惯",
    summary: "良好的睡眠对心理健康至关重要，了解如何改善睡眠质量和建立规律的睡眠模式。",
    thumbnail: "https://via.placeholder.com/800x400/9C27B0/white?text=健康睡眠"
  },
  {
    id: 5,
    title: "情绪调节与自我关怀",
    summary: "学习识别和管理自己的情绪，培养自我关怀的习惯，建立更健康的心理状态。",
    thumbnail: "https://via.placeholder.com/800x400/E91E63/white?text=情绪调节"
  },
  {
    id: 6,
    title: "社交焦虑的克服方法",
    summary: "如何逐步克服社交焦虑，建立自信，改善人际关系，享受更丰富的社交生活。",
    thumbnail: "https://via.placeholder.com/800x400/00BCD4/white?text=社交焦虑"
  }
];

const mockMoodData = [
  { date: '周一', value: 3 },
  { date: '周二', value: 4 },
  { date: '周三', value: 2 },
  { date: '周四', value: 4 },
  { date: '周五', value: 5 },
  { date: '周六', value: 4 },
  { date: '周日', value: 3 }
];

const mockAnalysisData = "根据您最近一周的情绪数据分析，您的整体心情状态良好，平均评分为3.6分。但在周三出现了情绪低谷，可能与工作压力有关。建议您在工作压力较大时，适当进行放松活动，如散步、听音乐或与朋友交流。您的情绪模式显示周末心情较好，建议保持现有的休息习惯。";

const mockEmergencyStatus = {
  status: 'normal',
  message: ''
};

// API调用函数
export const getArticles = async () => {
  try {
    // 实际API调用
    // const response = await get(`${PREFIX}/articles`);
    // return response.data;
    
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    return mockArticles;
  } catch (error) {
    console.error('获取文章失败:', error);
    return mockArticles;
  }
};

export const getMoodData = async () => {
  try {
    // 实际API调用
    // const response = await get(`${PREFIX}/mood-data`);
    // return response.data;
    
    await new Promise(resolve => setTimeout(resolve, 300));
    return mockMoodData;
  } catch (error) {
    console.error('获取心情数据失败:', error);
    return mockMoodData;
  }
};

export const getAnalysisData = async () => {
  try {
    // 实际API调用
    // const response = await get(`${PREFIX}/analysis`);
    // return response.data;
    
    await new Promise(resolve => setTimeout(resolve, 800));
    return mockAnalysisData;
  } catch (error) {
    console.error('获取分析数据失败:', error);
    return mockAnalysisData;
  }
};

export const getEmergencyStatus = async () => {
  try {
    // 实际API调用
    // const response = await get(`${PREFIX}/emergency-status`);
    // return response.data;
    
    await new Promise(resolve => setTimeout(resolve, 200));
    return mockEmergencyStatus;
  } catch (error) {
    console.error('获取预警状态失败:', error);
    return mockEmergencyStatus;
  }
};

export const submitCheckIn = async (checkInData) => {
  try {
    // 实际API调用
    const response = await post(`${PREFIX}/checkin`, checkInData);
    return response;
  } catch (error) {
    console.error('提交打卡失败:', error);
    throw error;
  }
};