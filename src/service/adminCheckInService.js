import { get, del } from './common';

// 根据后端MoodLabel分类获取心情标签显示文本
export const getMoodLabelDisplay = (labels) => {
    if (!labels || labels.length === 0) {
        return ''; // 没有心情时保持空白
    }
    
    // 根据MoodLabel.java的分类进行显示
    const positiveLabels = ['开心', '兴奋', '满足', '宁静', '感恩', '自信', '充满活力', '乐观'];
    const neutralLabels = ['平静', '冷静', '专注', '放松'];
    const negativeLabels = ['悲伤', '愤怒', '焦虑', '压力', '疲惫', '孤独', '沮丧', '担心', '失望', '烦躁', '困惑'];
    
    // 按分类组织标签
    const positive = labels.filter(label => positiveLabels.includes(label));
    const neutral = labels.filter(label => neutralLabels.includes(label));
    const negative = labels.filter(label => negativeLabels.includes(label));
    
    // 构建显示文本
    const parts = [];
    if (positive.length > 0) {
        parts.push(`积极: ${positive.join(', ')}`);
    }
    if (neutral.length > 0) {
        parts.push(`中性: ${neutral.join(', ')}`);
    }
    if (negative.length > 0) {
        parts.push(`消极: ${negative.join(', ')}`);
    }
    
    return parts.join(' | ');
};

// 获取心情分类统计
export const getMoodCategoryStats = (labels) => {
    if (!labels || labels.length === 0) {
        return { positive: 0, neutral: 0, negative: 0 };
    }
    
    const positiveLabels = ['开心', '兴奋', '满足', '宁静', '感恩', '自信', '充满活力', '乐观'];
    const neutralLabels = ['平静', '冷静', '专注', '放松'];
    const negativeLabels = ['悲伤', '愤怒', '焦虑', '压力', '疲惫', '孤独', '沮丧', '担心', '失望', '烦躁', '困惑'];
    
    const positive = labels.filter(label => positiveLabels.includes(label)).length;
    const neutral = labels.filter(label => neutralLabels.includes(label)).length;
    const negative = labels.filter(label => negativeLabels.includes(label)).length;
    
    return { positive, neutral, negative };
};

// 获取所有打卡数据
export const fetchAllCheckins = async (params = {}) => {
    try {
        const response = await get('/mood/query');
        if (response && response.moods) {
            return response.moods;
        } else {
            console.warn('API返回数据格式异常:', response);
            return [];
        }
    } catch (error) {
        console.error('获取打卡数据失败:', error);
        throw error;
    }
};

// 获取心情标签列表（按分类）
export const fetchMoodLabels = async () => {
    try {
        const response = await get('/mood/labels');
        if (response && response.positive && response.neutral && response.negative) {
            // 返回分类的标签
            return {
                positive: response.positive || [],
                neutral: response.neutral || [],
                negative: response.negative || []
            };
        } else {
            console.warn('API返回心情标签格式异常:', response);
            return { positive: [], neutral: [], negative: [] };
        }
    } catch (error) {
        console.error('获取心情标签失败:', error);
        throw error;
    }
};

// 获取所有心情标签（合并）
export const fetchAllMoodLabels = async () => {
    try {
        const response = await get('/mood/labels');
        if (response && response.positive && response.neutral && response.negative) {
            // 合并所有标签
            const allLabels = [
                ...response.positive,
                ...response.neutral,
                ...response.negative
            ];
            return allLabels;
        } else {
            console.warn('API返回心情标签格式异常:', response);
            return [];
        }
    } catch (error) {
        console.error('获取心情标签失败:', error);
        throw error;
    }
};

// 获取心情分布统计
export const fetchMoodStats = async () => {
    try {
        const response = await get('/mood/query');
        if (response && response.data) {
            const checkins = response.data;
            
            // 计算统计数据
            const stats = {
                total: checkins.length,
                today: checkins.filter(c => {
                    const today = new Date().toDateString();
                    const checkinDate = new Date(c.date).toDateString();
                    return today === checkinDate;
                }).length,
                averageMood: checkins.length > 0 ? 
                    (checkins.reduce((sum, c) => sum + (c.score || 0), 0) / checkins.length).toFixed(1) : 0
            };

            // 根据后端MoodLabel分类计算心情分布
            const moodDistribution = {
                // 积极情绪
                positive: {
                    happy: checkins.filter(c => c.labels && c.labels.includes('开心')).length,
                    excited: checkins.filter(c => c.labels && c.labels.includes('兴奋')).length,
                    content: checkins.filter(c => c.labels && c.labels.includes('满足')).length,
                    peaceful: checkins.filter(c => c.labels && c.labels.includes('宁静')).length,
                    grateful: checkins.filter(c => c.labels && c.labels.includes('感恩')).length,
                    confident: checkins.filter(c => c.labels && c.labels.includes('自信')).length,
                    energized: checkins.filter(c => c.labels && c.labels.includes('充满活力')).length,
                    optimistic: checkins.filter(c => c.labels && c.labels.includes('乐观')).length
                },
                // 中性情绪
                neutral: {
                    neutral: checkins.filter(c => c.labels && c.labels.includes('平静')).length,
                    calm: checkins.filter(c => c.labels && c.labels.includes('冷静')).length,
                    focused: checkins.filter(c => c.labels && c.labels.includes('专注')).length,
                    relaxed: checkins.filter(c => c.labels && c.labels.includes('放松')).length
                },
                // 消极情绪
                negative: {
                    sad: checkins.filter(c => c.labels && c.labels.includes('悲伤')).length,
                    angry: checkins.filter(c => c.labels && c.labels.includes('愤怒')).length,
                    anxious: checkins.filter(c => c.labels && c.labels.includes('焦虑')).length,
                    stressed: checkins.filter(c => c.labels && c.labels.includes('压力')).length,
                    tired: checkins.filter(c => c.labels && c.labels.includes('疲惫')).length,
                    lonely: checkins.filter(c => c.labels && c.labels.includes('孤独')).length,
                    frustrated: checkins.filter(c => c.labels && c.labels.includes('沮丧')).length,
                    worried: checkins.filter(c => c.labels && c.labels.includes('担心')).length,
                    disappointed: checkins.filter(c => c.labels && c.labels.includes('失望')).length,
                    irritated: checkins.filter(c => c.labels && c.labels.includes('烦躁')).length,
                    confused: checkins.filter(c => c.labels && c.labels.includes('困惑')).length
                }
            };

            return { stats, moodDistribution, checkins };
        }
        return { stats: {}, moodDistribution: {}, checkins: [] };
    } catch (error) {
        console.error('获取心情统计失败:', error);
        throw error;
    }
}; 

// 删除打卡
export const deleteCheckin = async (checkinId) => {
    try {
        return await del(`/mood/${checkinId}`);
    } catch (error) {
        console.error('删除打卡失败:', error);
        throw error;
    }
};