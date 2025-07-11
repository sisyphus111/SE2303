import { handleApiResponse } from '../lib/ErrorHandler';


// const addAuthHeaders = (options = {}, auth = {}) => {
//     const headers = {
//         ...(options.headers || {}),
//         'Content-Type': 'application/json',
//     };
//     if (auth.userId) {
//         headers['X-User-Id'] = auth.userId;
//         headers['X-Valid'] = auth.valid?.toString() || 'false';
//         headers['X-Identity'] = auth.identity?.toString() || 'true';
//     }
//     return { ...options, headers };
// };

export async function get(url, options = {}) {
    let fullUrl = `${PREFIX}${url}`;
    console.log("get请求：",options);
    if (options.queryParams) {
        const searchParams = new URLSearchParams();
        Object.entries(options.queryParams).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
                searchParams.append(key, value);
            }
        });
        const queryString = searchParams.toString();
        console.log("get请求：",queryString);
        if (queryString) {
            fullUrl += `?${queryString}`;
        }
    }

    console.log(`🌐 GET请求: ${fullUrl}`);

    let res = await fetch(fullUrl, {
        method: "GET",
        credentials: "include" // 包含cookie进行认证
    });
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function put(url, data) {
    let opts = {
        method: "PUT",
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: "include" // 包含cookie进行认证
    };
    let res = await fetch(`${PREFIX}${url}`, opts);
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function del(url, data) {
    let opts = {
        method: "DELETE",
        credentials: "include" // 包含cookie进行认证
    };

    if (data) {
        opts.body = JSON.stringify(data);
        opts.headers = {
            'Content-Type': 'application/json'
        };
    }

    let res = await fetch(`${PREFIX}${url}`, opts);
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function post(url, data, options = {}) {
    let fullUrl = `${PREFIX}${url}`;

    // 如果有查询参数，添加到URL中
    if (options.params) {
        const searchParams = new URLSearchParams();
        Object.entries(options.params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
                searchParams.append(key, value);
            }
        });
        const queryString = searchParams.toString();
        if (queryString) {
            fullUrl += `?${queryString}`;
        }
    }

    console.log(`🌐 POST请求: ${fullUrl}`);
    console.log('📤 请求数据:', data);
    console.log('📤 请求数据（JSON 序列化后）:', JSON.stringify(data));

    let opts = {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json',
            ...options.headers // 合并额外的请求头
        },
        credentials: "include" // 包含cookie进行认证
    };

    let res = await fetch(fullUrl, opts);
    console.log(`📥 HTTP响应状态: ${res.status} ${res.statusText}`);

    const response = await res.json();
    console.log('📥 响应数据:', response);

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export const BASEURL = 'https://7936e369.r10.vip.cpolar.cn';
export const PREFIX = `${BASEURL}/api`;

// ============ 管理员专用API方法 ============

/**
 * 获取所有帖子（管理员版本，包括所有状态的帖子）
 * @param {Object} options - 查询选项
 * @param {string} options.keyword - 搜索关键词
 * @param {string} options.status - 帖子状态筛选
 * @param {string} options.category - 帖子分类
 * @param {string} options.sortBy - 排序方式
 * @param {number} options.page - 页码
 * @param {number} options.size - 每页数量
 */
export async function getAdminPosts(options = {}) {
    try {
        console.log('🚀 [管理员] 获取帖子列表:', options);
        
        const queryParams = {};
        if (options.keyword) queryParams.keyword = options.keyword;
        if (options.category) queryParams.category = options.category;
        if (options.sortBy) queryParams.sortBy = options.sortBy;
        if (options.page) queryParams.page = options.page;
        if (options.size) queryParams.size = options.size;
        if (options.status) queryParams.status = options.status;
        
        return await get('/community/posts/get_posts', { queryParams });
    } catch (error) {
        console.error('❌ [管理员] 获取帖子列表失败:', error);
        throw error;
    }
}

/**
 * 审核通过帖子
 * @param {number} postId - 帖子ID
 */
export async function approvePost(postId) {
    try {
        console.log('🚀 [管理员] 审核通过帖子:', postId);
        return await put(`/community/posts/approve/${postId}`);
    } catch (error) {
        console.error('❌ [管理员] 审核通过帖子失败:', error);
        throw error;
    }
}

/**
 * 封禁帖子
 * @param {number} postId - 帖子ID
 */
export async function banPost(postId) {
    try {
        console.log('🚀 [管理员] 封禁帖子:', postId);
        return await put(`/community/posts/ban/${postId}`);
    } catch (error) {
        console.error('❌ [管理员] 封禁帖子失败:', error);
        throw error;
    }
}

/**
 * 删除帖子（软删除）
 * @param {number} postId - 帖子ID
 */
export async function deletePost(postId) {
    try {
        console.log('🚀 [管理员] 删除帖子:', postId);
        return await put(`/community/posts/delete/${postId}`);
    } catch (error) {
        console.error('❌ [管理员] 删除帖子失败:', error);
        throw error;
    }
}

/**
 * 审核通过评论
 * @param {number} commentId - 评论ID
 */
export async function approveComment(commentId) {
    try {
        console.log('🚀 [管理员] 审核通过评论:', commentId);
        return await put(`/community/comments/approve/${commentId}`);
    } catch (error) {
        console.error('❌ [管理员] 审核通过评论失败:', error);
        throw error;
    }
}

/**
 * 封禁评论
 * @param {number} commentId - 评论ID
 */
export async function banComment(commentId) {
    try {
        console.log('🚀 [管理员] 封禁评论:', commentId);
        return await put(`/community/comments/ban/${commentId}`);
    } catch (error) {
        console.error('❌ [管理员] 封禁评论失败:', error);
        throw error;
    }
}

/**
 * 获取所有评论（管理员版本，包括所有状态的评论）
 * @param {Object} options - 查询选项
 * @param {string} options.keyword - 搜索关键词
 * @param {string} options.status - 评论状态筛选
 * @param {string} options.sortBy - 排序方式
 * @param {number} options.page - 页码
 * @param {number} options.size - 每页数量
 */
export async function getAdminComments(options = {}) {
    try {
        console.log('🚀 [管理员] 获取评论列表:', options);
        
        const queryParams = {};
        if (options.keyword) queryParams.keyword = options.keyword;
        if (options.sortBy) queryParams.sortBy = options.sortBy;
        if (options.page) queryParams.page = options.page;
        if (options.size) queryParams.size = options.size;
        if (options.status) queryParams.status = options.status;
        
        return await get('/community/comments/admin', { queryParams });
    } catch (error) {
        console.error('❌ [管理员] 获取评论列表失败:', error);
        throw error;
    }
}

/**
 * 删除评论（软删除）
 * @param {number} commentId - 评论ID
 */
export async function deleteComment(commentId) {
    try {
        console.log('🚀 [管理员] 删除评论:', commentId);
        return await put(`/community/comments/delete/${commentId}`);
    } catch (error) {
        console.error('❌ [管理员] 删除评论失败:', error);
        throw error;
    }
}

/**
 * 举报帖子
 * @param {number} postId - 帖子ID
 * @param {string} reason - 举报原因
 */
export async function reportPost(postId, reason) {
    try {
        console.log('🚀 举报帖子:', postId, reason);
        return await post(`/community/posts/report/${postId}`, reason);
    } catch (error) {
        console.error('❌ 举报帖子失败:', error);
        throw error;
    }
}

/**
 * 举报评论
 * @param {number} commentId - 评论ID
 * @param {string} reason - 举报原因
 */
export async function reportComment(commentId, reason) {
    try {
        console.log('🚀 举报评论:', commentId, reason);
        // 注意：后端可能还没有实现评论举报API，这里先预留接口
        return await post(`/community/comments/report/${commentId}`, reason);
    } catch (error) {
        console.error('❌ 举报评论失败:', error);
        throw error;
    }
}

// 注意：以下API可能需要后端进一步实现具体的举报和审核管理接口
// 目前使用模拟实现，后续可以替换为真实API

/**
 * 获取举报列表
 * @param {Object} options - 查询选项
 */
export async function getReportsList(options = {}) {
    try {
        console.log('🚀 [管理员] 获取举报列表:', options);
        
        const queryParams = {};
        if (options.status) queryParams.status = options.status;
        if (options.type) queryParams.type = options.type;
        if (options.page) queryParams.page = options.page;
        if (options.size) queryParams.size = options.size;
        
        return await get('/community/reports', { queryParams });
    } catch (error) {
        console.error('❌ [管理员] 获取举报列表失败:', error);
        throw error;
    }
}

/**
 * 获取举报详情
 * @param {number} reportId - 举报ID
 */
export async function getReportDetail(reportId) {
    try {
        console.log('🚀 [管理员] 获取举报详情:', reportId);
        return await get(`/community/reports/${reportId}`);
    } catch (error) {
        console.error('❌ [管理员] 获取举报详情失败:', error);
        throw error;
    }
}

/**
 * 获取举报统计信息
 */
export async function getReportStats() {
    try {
        console.log('🚀 [管理员] 获取举报统计信息');
        return await get('/community/reports/stats');
    } catch (error) {
        console.error('❌ [管理员] 获取举报统计信息失败:', error);
        throw error;
    }
}

/**
 * 处理举报
 * @param {number} reportId - 举报ID
 * @param {string} action - 处理动作 (resolved/rejected)
 * @param {string} reason - 处理原因
 */
export async function handleReport(reportId, action, reason = '') {
    try {
        console.log('🚀 [管理员] 处理举报:', reportId, action, reason);
        
        const data = { action, reason };
        return await put(`/community/reports/${reportId}/handle`, data);
    } catch (error) {
        console.error('❌ [管理员] 处理举报失败:', error);
        throw error;
    }
}

/**
 * 获取审核队列
 * @param {Object} options - 查询选项
 */
export async function getReviewQueue(options = {}) {
    try {
        console.log('🚀 [管理员] 获取审核队列:', options);
        
        const queryParams = {};
        if (options.status) queryParams.status = options.status;
        if (options.type) queryParams.type = options.type;
        if (options.page) queryParams.page = options.page;
        if (options.size) queryParams.size = options.size;
        
        return await get('/community/review/queue', { queryParams });
    } catch (error) {
        console.error('❌ [管理员] 获取审核队列失败:', error);
        throw error;
    }
}

/**
 * 获取内容详情
 * @param {number} contentId - 内容ID
 * @param {string} type - 内容类型 (post/comment)
 */
export async function getContentDetail(contentId, type = null) {
    try {
        console.log('🚀 [管理员] 获取内容详情:', contentId, type);
        
        const queryParams = {};
        if (type) queryParams.type = type;
        
        return await get(`/community/review/${contentId}`, { queryParams });
    } catch (error) {
        console.error('❌ [管理员] 获取内容详情失败:', error);
        throw error;
    }
}

/**
 * 审核内容
 * @param {number} contentId - 内容ID
 * @param {string} action - 审核动作 (APPROVED/BANNED)
 * @param {string} type - 内容类型 (post/comment)
 * @param {string} reason - 审核原因
 */
export async function reviewContent(contentId, action, type, reason = '') {
    try {
        console.log('🚀 [管理员] 审核内容:', contentId, action, type, reason);
        
        const data = { action, type, reason };
        return await put(`/community/review/${contentId}`, data);
    } catch (error) {
        console.error('❌ [管理员] 审核内容失败:', error);
        throw error;
    }
}
