import { get, post, PREFIX } from './common';

// 示例数据 - 心理健康主题配风景封面
const mockArticles = [
  {
    id: 1,
    title: "如何保持良好的心理健康",
    summary: "分享一些日常保持心理健康的小技巧，包括规律作息、适度运动、保持社交联系等实用方法。",
    thumbnail: "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 2,
    title: "压力管理的有效方法",
    summary: "学会管理生活和工作中的压力，通过时间管理、放松技巧和心态调整来应对挑战。",
    thumbnail: "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 3,
    title: "冥想与放松技巧",
    summary: "通过冥想和放松练习改善心情，每天10分钟的正念练习就能带来显著的心理健康改善。",
    thumbnail: "https://images.unsplash.com/photo-1464207687429-7505649dae38?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 4,
    title: "建立健康的睡眠习惯",
    summary: "良好的睡眠对心理健康至关重要，了解如何改善睡眠质量和建立规律的睡眠模式。",
    thumbnail: "https://images.unsplash.com/photo-1418065460487-3baa5e04c47b?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 5,
    title: "情绪调节与自我关怀",
    summary: "学习识别和管理自己的情绪，培养自我关怀的习惯，建立更健康的心理状态。",
    thumbnail: "https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 6,
    title: "社交焦虑的克服方法",
    summary: "如何逐步克服社交焦虑，建立自信，改善人际关系，享受更丰富的社交生活。",
    thumbnail: "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 7,
    title: "正念练习与情绪平衡",
    summary: "学习正念技巧帮助平衡情绪，减少焦虑和抑郁情绪，提升整体心理健康水平。",
    thumbnail: "https://images.unsplash.com/photo-1522383225653-ed111181a951?auto=format&fit=crop&w=1200&q=85"
  },
  {
    id: 8,
    title: "心理韧性的培养与提升",
    summary: "增强心理韧性，学会在面对困难和挫折时保持积极心态，建立更强的抗压能力。",
    thumbnail: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&q=85"
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
    
    return mockArticles;
  } catch (error) {
    console.error('获取文章失败:', error);
    return mockArticles;
  }
};

export const getMoodData = async (userId) => {
  try {
    if (!userId) {
      console.warn('未提供用户ID，返回模拟数据');
      return mockMoodData;
    }

    // 计算最近7天的日期范围
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - 6); // 获取最近7天

    // 格式化日期为 YYYY-MM-DD
    const formatDate = (date) => {
      return date.toISOString().split('T')[0];
    };

    const startDateStr = formatDate(startDate);
    const endDateStr = formatDate(endDate);

    // 调用后端API获取情绪数据
    const response = await get(`/mood/user/${userId}/range?startDate=${startDateStr}&endDate=${endDateStr}`);

    // 检查响应格式 - 可能是直接返回数组，也可能是包装在object中
    let moodRecords;
    if (Array.isArray(response)) {
      // 直接返回数组的情况
      moodRecords = response;
    } else if (response && response.object && Array.isArray(response.object)) {
      // 包装在object中的情况
      moodRecords = response.object;
    } else {
      console.warn('⚠️ 未知的响应格式:', response);
      return mockMoodData;
    }

    // 先按日期升序排列（最早的在前）
    const sortedRecords = [...moodRecords].sort((a, b) => new Date(a.date) - new Date(b.date));
    
    // 将后端数据转换为前端需要的格式
    const moodData = sortedRecords.map(record => {
      
      // 确保日期格式正确
      let date;
      if (typeof record.date === 'string') {
        date = new Date(record.date);
      } else if (record.date) {
        date = new Date(record.date);
      } else {
        console.warn('⚠️ 记录中没有日期:', record);
        return null;
      }
      
      const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const dayName = dayNames[date.getDay()];
      
      // 确保评分值正确
      let score = record.score;
      if (score === null || score === undefined) {
        score = 1; // 默认值
      }
      
      const dataPoint = {
        date: dayName,
        value: score
      };

      return dataPoint;
    }).filter(item => item !== null); // 过滤掉无效的记录

    // 如果数据不足7天，用默认值填充
    while (moodData.length < 7) {
      const missingDayIndex = moodData.length;
      const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      moodData.push({
        date: dayNames[missingDayIndex],
        value: 1 // 默认值
      });
    }

    console.log('✅ 最终情绪数据:', moodData);
    return moodData;
    
  } catch (error) {
    console.error('❌ 获取心情数据失败:', error);
    return mockMoodData;
  }
};

export const getAnalysisData = async () => {
  try {
    // 实际API调用
    // const response = await get(`${PREFIX}/analysis`);
    // return response.data;
    
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