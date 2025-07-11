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

const mockAnalysisData = `# 情绪分析报告

## 整体状态评估
根据您最近一周的情绪数据分析，您的**整体心情状态良好**，平均评分为 **3.6分**。

## 关键发现
- 在周三出现了情绪低谷，可能与工作压力有关
- 周末心情较好，显示您有良好的休息习惯
- 情绪波动在正常范围内

## 建议措施
1. **工作压力管理**：在工作压力较大时，适当进行放松活动
2. **日常放松**：如散步、听音乐或与朋友交流
3. **保持习惯**：建议保持现有的休息习惯

## 情绪趋势
您的情绪模式显示：
- 工作日：平均 3.2分
- 周末：平均 4.1分
- 整体趋势：稳定向好

> 💡 **小贴士**：保持规律的作息和适度的运动有助于情绪稳定。`;

const mockEmergencyStatus = {
  status: 'normal',
  message: ''
};

// 生成动态模拟数据的辅助函数
const generateDynamicMockData = () => {
  const today = new Date();
  const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  const dynamicMockData = [];
  
  // 生成最近7天的数据，从7天前开始到今天
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(today.getDate() - i);
    const dayName = dayNames[date.getDay()];
    
    // 生成随机的情绪评分（1-5分）
    const randomValue = Math.floor(Math.random() * 5) + 1;
    
    dynamicMockData.push({
      date: dayName,
      value: randomValue
    });
  }
  
  return dynamicMockData;
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
      console.warn('未提供用户ID，返回动态生成的模拟数据');
      return generateDynamicMockData();
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
      return generateDynamicMockData();
    }

    // 先按日期升序排列（最早的在前）
    const sortedRecords = [...moodRecords].sort((a, b) => new Date(a.date) - new Date(b.date));
    
    // 将后端数据转换为前端需要的格式，并记录原始日期信息
    const moodDataWithDates = sortedRecords.map(record => {
      
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
        value: score,
        originalDate: date // 保留原始日期用于排序
      };

      return dataPoint;
    }).filter(item => item !== null); // 过滤掉无效的记录

    // 获取今天和最近7天的日期信息
    const today = new Date();
    const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    
    // 生成最近7天的完整日期数组
    const last7Days = [];
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      last7Days.push({
        date: date,
        dayName: dayNames[date.getDay()],
        isToday: i === 0 // 最后一天是今天
      });
    }

    // 创建完整的数据数组，确保今天的数据在最后位置
    const completeMoodData = [];
    
    for (let i = 0; i < 7; i++) {
      const targetDate = last7Days[i];
      
      // 查找是否有对应日期的数据
      const existingData = moodDataWithDates.find(item => {
        return item.originalDate.toDateString() === targetDate.date.toDateString();
      });
      
      if (existingData) {
        // 使用现有数据
        completeMoodData.push({
          date: existingData.date,
          value: existingData.value
        });
      } else {
        // 填充默认数据
        completeMoodData.push({
          date: targetDate.dayName,
          value: 1 // 默认值
        });
      }
    }

    console.log('✅ 最终情绪数据:', completeMoodData);
    return completeMoodData;
    
  } catch (error) {
    console.error('❌ 获取心情数据失败:', error);
    return generateDynamicMockData();
  }
};

export const getAnalysisData = async (userId) => {
  try {
    if (!userId) {
      console.warn('未提供用户ID，返回模拟数据');
      // 添加模拟延迟以测试加载状态
      await new Promise(resolve => setTimeout(resolve, 1500));
      return mockAnalysisData;
    }

    // 调用后端API获取分析数据
    const response = await get(`/mood/analysis/${userId}`);
    
    // 检查响应格式 - 支持多种可能的响应结构
    let analysisData;
    if (typeof response === 'string') {
      // 直接返回字符串的情况
      analysisData = response;
    } else if (response && response.analysis) {
      // 包装在analysis字段中的情况
      analysisData = response.analysis;
    } else if (response && response.data) {
      // 包装在data字段中的情况
      analysisData = response.data;
    } else if (response && typeof response === 'object') {
      // 其他对象格式，尝试转换为字符串
      analysisData = JSON.stringify(response, null, 2);
    } else {
      console.warn('⚠️ 未知的分析数据响应格式:', response);
      return mockAnalysisData;
    }
    
    console.log('✅ 获取到分析数据:', analysisData);
    return analysisData;
    
  } catch (error) {
    console.error('❌ 获取分析数据失败:', error);
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