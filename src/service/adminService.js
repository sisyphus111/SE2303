// 管理员专用API服务

import { get, put } from './common';

// ============ 真实的用户管理API ============

/**
 * 获取所有用户列表 (真实API)
 */
export const getAllUsers = async () => {
    try {
        console.log('🚀 调用获取用户列表API...');
        return await get('/user/admin/users');
    } catch (error) {
        console.error('❌ 获取用户列表失败:', error);
        throw error;
    }
};

/**
 * 更新用户状态 (真实API)
 * @param {number} userId - 用户ID
 * @param {boolean} valid - 是否有效
 */
export const updateUserStatus = async (userId, valid) => {
    try {
        console.log(`🚀 调用更新用户状态API: userId=${userId}, valid=${valid}`);
        return await put(`/user/admin/users/user/status?valid=${valid}`);
    } catch (error) {
        console.error('❌ 更新用户状态失败:', error);
        throw error;
    }
};

// ============ 伪函数API (将来会调用其他微服务) ============

/**
 * 获取举报列表 (伪函数)
 */
export const getReportsList = async () => {
    try {
        console.log('🚀 [伪函数] 获取举报列表...');
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // 模拟数据
        const mockReports = [
            {
                id: 1,
                type: 'post',
                reason: '恶意攻击',
                description: '用户发布了包含人身攻击的内容',
                reporter: 'user123',
                reportedUser: 'user456',
                status: 'pending',
                createdAt: '2024-01-15T10:30:00Z'
            },
            {
                id: 2,
                type: 'comment',
                reason: '垃圾信息',
                description: '评论中包含大量垃圾广告链接',
                reporter: 'user789',
                reportedUser: 'user101',
                status: 'resolved',
                createdAt: '2024-01-14T16:45:00Z',
                handledAt: '2024-01-14T17:00:00Z'
            },
            {
                id: 3,
                type: 'post',
                reason: '不当内容',
                description: '发布了不适合社区氛围的内容',
                reporter: 'user202',
                reportedUser: 'user303',
                status: 'pending',
                createdAt: '2024-01-13T09:15:00Z'
            }
        ];
        
        console.log('✅ [伪函数] 举报列表获取成功');
        return {
            success: true,
            data: mockReports,
            message: '举报列表获取成功'
        };
    } catch (error) {
        console.error('❌ [伪函数] 获取举报列表失败:', error);
        return {
            success: false,
            message: '获取举报列表失败'
        };
    }
};

/**
 * 处理举报 (伪函数)
 * @param {number} reportId - 举报ID
 * @param {string} action - 处理动作 (resolved/rejected)
 * @param {string} reason - 处理原因
 */
export const handleReport = async (reportId, action, reason = '') => {
    try {
        console.log(`🚀 [伪函数] 处理举报: reportId=${reportId}, action=${action}, reason=${reason}`);
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 模拟成功响应
        console.log(`✅ [伪函数] 举报处理成功: ${action}`);
        return {
            success: true,
            data: { reportId, action, processedAt: new Date().toISOString() },
            message: `举报已${action === 'resolved' ? '通过' : '拒绝'}处理`
        };
    } catch (error) {
        console.error('❌ [伪函数] 处理举报失败:', error);
        return {
            success: false,
            message: '处理举报失败'
        };
    }
};

/**
 * 获取审核队列 (伪函数)
 */
export const getReviewQueue = async () => {
    try {
        console.log('🚀 [伪函数] 获取审核队列...');
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 600));
        
        // 模拟数据
        const mockContents = [
            {
                id: 1,
                type: 'post',
                title: '分享我的学习心得',
                content: '最近在学习心理学，想和大家分享一些心得体会...',
                author: 'student001',
                status: 'pending',
                tags: ['学习', '心理学'],
                createdAt: '2024-01-15T14:20:00Z'
            },
            {
                id: 2,
                type: 'comment',
                content: '非常感谢楼主的分享，很有帮助！',
                author: 'student002',
                status: 'approved',
                createdAt: '2024-01-15T15:30:00Z',
                reviewedAt: '2024-01-15T15:35:00Z'
            },
            {
                id: 3,
                type: 'post',
                title: '关于压力管理的讨论',
                content: '最近学业压力比较大，想和大家讨论一下如何有效管理压力...',
                author: 'student003',
                status: 'pending',
                tags: ['压力管理', '心理健康'],
                createdAt: '2024-01-15T11:45:00Z'
            }
        ];
        
        console.log('✅ [伪函数] 审核队列获取成功');
        return {
            success: true,
            data: mockContents,
            message: '审核队列获取成功'
        };
    } catch (error) {
        console.error('❌ [伪函数] 获取审核队列失败:', error);
        return {
            success: false,
            message: '获取审核队列失败'
        };
    }
};

/**
 * 审核内容 (伪函数)
 * @param {number} contentId - 内容ID
 * @param {string} action - 审核动作 (approved/rejected)
 * @param {string} reason - 审核原因
 */
export const reviewContent = async (contentId, action, reason = '') => {
    try {
        console.log(`🚀 [伪函数] 审核内容: contentId=${contentId}, action=${action}, reason=${reason}`);
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // 模拟成功响应
        console.log(`✅ [伪函数] 内容审核成功: ${action}`);
        return {
            success: true,
            data: { contentId, action, reviewedAt: new Date().toISOString() },
            message: `内容已${action === 'approved' ? '通过' : '拒绝'}审核`
        };
    } catch (error) {
        console.error('❌ [伪函数] 审核内容失败:', error);
        return {
            success: false,
            message: '审核内容失败'
        };
    }
};

/**
 * 获取危险用户列表 (伪函数)
 */
export const getCrisisUsers = async () => {
    try {
        console.log('🚀 [伪函数] 获取危险用户列表...');
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 900));
        
        // 模拟数据
        const mockCrisisUsers = [
            {
                id: 1,
                username: 'user_at_risk_001',
                nickname: '小明',
                riskLevel: 'high',
                riskScore: 85,
                riskFactors: ['抑郁倾向', '社交隔离', '学业压力'],
                status: 'pending',
                detectedAt: '2024-01-15T08:30:00Z',
                telephone: '138****5678',
                lastActivityAt: '2024-01-15T12:00:00Z'
            },
            {
                id: 2,
                username: 'user_at_risk_002',
                nickname: '小红',
                riskLevel: 'medium',
                riskScore: 65,
                riskFactors: ['焦虑症状', '睡眠问题'],
                status: 'contacted',
                detectedAt: '2024-01-14T16:20:00Z',
                telephone: '139****1234',
                lastActivityAt: '2024-01-15T10:30:00Z'
            },
            {
                id: 3,
                username: 'user_at_risk_003',
                nickname: '小李',
                riskLevel: 'low',
                riskScore: 45,
                riskFactors: ['轻微压力'],
                status: 'resolved',
                detectedAt: '2024-01-13T14:15:00Z',
                handledAt: '2024-01-14T09:00:00Z',
                lastActivityAt: '2024-01-15T14:45:00Z'
            }
        ];
        
        console.log('✅ [伪函数] 危险用户列表获取成功');
        return {
            success: true,
            data: mockCrisisUsers,
            message: '危险用户列表获取成功'
        };
    } catch (error) {
        console.error('❌ [伪函数] 获取危险用户列表失败:', error);
        return {
            success: false,
            message: '获取危险用户列表失败'
        };
    }
};

/**
 * 标记危险用户为已处理 (伪函数)
 * @param {number} userId - 用户ID
 * @param {string} action - 处理动作 (contacted/resolved)
 * @param {string} notes - 处理备注
 */
export const markCrisisHandled = async (userId, action, notes = '') => {
    try {
        console.log(`🚀 [伪函数] 标记危险用户处理: userId=${userId}, action=${action}, notes=${notes}`);
        
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 700));
        
        // 模拟成功响应
        console.log(`✅ [伪函数] 危险用户标记成功: ${action}`);
        return {
            success: true,
            data: { userId, action, handledAt: new Date().toISOString() },
            message: `用户已标记为${action === 'contacted' ? '已联系' : '已解决'}`
        };
    } catch (error) {
        console.error('❌ [伪函数] 标记危险用户失败:', error);
        return {
            success: false,
            message: '标记危险用户失败'
        };
    }
};

// ============ 工具函数 ============

/**
 * 检查是否为管理员身份
 */
export const checkAdminPermission = () => {
    // 这里可以添加管理员权限检查逻辑
    // 目前简单返回true，实际应该检查UserContext中的identity字段
    return true;
};

/**
 * 格式化API错误信息
 */
export const formatApiError = (error) => {
    if (error.response && error.response.data && error.response.data.message) {
        return error.response.data.message;
    }
    return error.message || '操作失败';
}; 