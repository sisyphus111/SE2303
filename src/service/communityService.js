// 社区相关API服务
import {get, post, put, del} from './common';

/**
 * 获取帖子列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.category - 分类（根据页面：情感、学习、生活、健康、成长）
 * @param {string} params.sortBy - 排序方式（最新、最多评论、最多浏览）
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise<Object>} 帖子列表
 */
export const getPostList = async (params = {}) => {
    // 1. 验证参数
    if (params.page < 1) {
        throw new Error('页码必须大于或等于1');
    }
    if (params.size < 1 || params.size > 100) {
        throw new Error('每页大小必须在1到100之间');
    }
    if (params.sortBy && !['latest', 'mostCommented', 'mostViewed'].includes(params.sortBy)) {
        throw new Error('排序方式必须是 latest、mostCommented 或 mostViewed');
    }
    // 2. 构建查询参数
    const queryParams = {
        page: params.page || 1,
        size: params.size || 5,
        sortBy: params.sortBy || 'latest',
        ...(params.category && {category: params.category}),
        ...(params.keyword && {keyword: params.keyword}),
    };
    const queryString = new URLSearchParams(queryParams).toString();
    try {
        const response = await get(`/posts/get_posts?${queryString}`);

        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('获取帖子请求失败:', error);
        return {
            success: false,
            error: error.message || '帖子获取失败'
        };
    }
};

/**
 * 创建帖子
 * @param {Object} postData - 帖子数据
 * @param {string} postData.title - 标题
 * @param {string} postData.content - 内容
 * @param {Array<string>} postData.tags - 标签数组
 * @returns {Promise<Object>} 创建的帖子
 */
export const createPost = async (postData) => {
    // 1. 验证必填字段
    if (!postData.title || postData.title.trim() === '') {
        throw new Error('帖子标题不能为空');
    }
    if (!postData.content || postData.content.trim() === '') {
        throw new Error('帖子内容不能为空');
    }
    if (postData.tags == null) {
        throw new Error('帖子标签不能为空');
    }

    const requestBody = {
        title: postData.title,
        content: postData.content,
        tags: postData.tags || []
    };

    try {
        // 4. 发送请求
        const response = await post('/posts/send', {requestBody});

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('创建帖子时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子获取失败'
        };
    }
};

/**
 * 获取帖子详情
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 帖子详情
 */
export const getPostDetail = async (postId) => {
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        // 4. 发送请求
        const response = await get(`/posts/get_post/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('获取帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子获取失败'
        };
    }
};

/**
 * 更新帖子
 * @param {string} postId - 帖子ID
 * @param {Object} postData - 更新的帖子数据
 * @param {string} postData.title - 标题
 * @param {string} postData.content - 内容
 * @param {Array<string>} postData.tags - 标签数组
 * @returns {Promise<Object>} 更新结果
 */
export const updatePost = async (postId, postData) => {
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    if (!postData.title || postData.title.trim() === '') {
        throw new Error('帖子标题不能为空');
    }
    if (!postData.content || postData.content.trim() === '') {
        throw new Error('帖子内容不能为空');
    }
    if (postData.tags == null) {
        throw new Error('帖子标签不能为空');
    }

    try {
        // 4. 发送请求
        const response = await put(`/posts/update/${postId}`, {postData});

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
};

/**
 * 删除帖子
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 删除结果
 */
export const deletePost = async (postId) => {
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        // 4. 发送请求
        const response = await put(`/posts/delete/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('删除帖子时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子删除失败'
        };
    }
};

/**
 * 点赞帖子
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 操作结果
 */
export const LikePost = async (postId) => {
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        // 4. 发送请求
        const response = await post(`/posts/like/${postId}`, {});

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('点赞帖子时发生错误:', error);
        return {
            success: false,
            error: error.message || '点赞失败'
        };
    }
};

/**
 * 取消点赞帖子
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 操作结果
 */
export const UnlikePost = async (postId) => {
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        // 4. 发送请求
        const response = await del(`/posts/unlike/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('取消点赞帖子时发生错误:', error);
        return {
            success: false,
            error: error.message || '取消点赞失败'
        };
    }
};

/**
 * 获取我的帖子
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 * @constructor
 */
export const MyPost = async () => {
    try {
        // 4. 发送请求
        const response = await get(`/posts/my_post`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('获取我的帖子时发生错误:', error);
        return {
            success: false,
            error: error.message || '获取我的帖子失败'
        };
    }
}

/**
 * 获取所有tag
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const getAllTags = async () => {

    try {
        // 4. 发送请求
        const response = await get(`/posts/get_tags`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('获取标签时发生错误:', error);
        return {
            success: false,
            error: error.message || '获取标签失败'
        };
    }
}

/**
 * 获取帖子评论
 * @param postId
 * @param page
 * @param size
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const getCommentOfPost = async (postId, page = 1, size = 5) => {
    try {
        // 4. 发送请求
        const response = await get(`/posts/get_comments/${postId}?page=${page}&size=${size}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('获取评论时发生错误:', error);
        return {
            success: false,
            error: error.message || '获取评论失败'
        };
    }
}
/**
 * 给对应帖子发评论
 * @param postId
 * @param commentData 评论
 * @param commentData.content 评论内容
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const sendCommentToPost = async (postId, commentData) => {
    try {
        // 4. 发送请求
        const response = await post(`/posts/add_comment/${postId}`, {commentData});

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}

/**
 * 收藏对应帖子
 * @param postId
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const collectPost = async (postId) => {
    try {
        // 4. 发送请求
        const response = await post(`/posts/collect/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}

/**
 * 取消收藏帖子
 * @param postId
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const delCollectedPost = async (postId) => {
    try {
        // 4. 发送请求
        const response = await del(`/posts/delete/collection/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}

/**
 * 审核通过对应帖子
 * @param postId
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const approvePost = async (postId) => {
    try {
        // 4. 发送请求
        const response = await put(`/posts/approve/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}

/**
 * 封禁对应帖子
 * @param postId
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const banPost = async (postId) => {
    try {
        // 4. 发送请求
        const response = await put(`/posts/ban/${postId}`);

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}

/**
 * 举报对应帖子
 * @param postId
 * @param reason 举报原因
 * @returns {Promise<{success: boolean, error: string}|{success: boolean, user, preferences: {}}>}
 */
export const reportPost = async (postId,reason) => {
    try {
        // 4. 发送请求
        const response = await post(`/posts/ban/${postId}`,{reason});

        // 5. 解析响应
        return {
            success: true,
            user: response.data, // 兼容不同的响应格式
            preferences: {}
        };
    } catch (error) {
        console.error('修改帖子详情时发生错误:', error);
        return {
            success: false,
            error: error.message || '帖子修改失败'
        };
    }
}




/**
 * 获取社区帖子数据（模拟数据，用于前端测试）
 * @returns {Array} 帖子列表
 */
export const getCommunityPosts = () => {
    return [
        {
            id: 1,
            title: '如何缓解工作压力？',
            content: '最近工作压力很大，想和大家分享一下我的减压方法，也希望能听到大家的建议...',
            author: '小明',
            authorAvatar: '👨',
            category: '健康',
            tags: ['健康'],
            likes: 24,
            comments: 8,
            views: 156,
            createdAt: '2024-01-15 14:30',
            isLiked: false
        },
        {
            id: 2,
            title: '分享一个让我心情变好的小习惯',
            content: '每天早上起床后，我会花5分钟时间做深呼吸和简单的伸展运动，这让我一整天都感觉很好...',
            author: '小红',
            authorAvatar: '👩',
            category: '情感',
            tags: ['情感', '生活'],
            likes: 42,
            comments: 15,
            views: 289,
            createdAt: '2024-01-14 09:15',
            isLiked: true
        },
        {
            id: 3,
            title: '关于焦虑症的一些思考',
            content: '想和大家讨论一下焦虑症这个话题，希望能帮助到有类似经历的朋友...',
            author: '小李',
            authorAvatar: '👨‍🦱',
            category: '健康',
            tags: ['健康', '情感'],
            likes: 18,
            comments: 12,
            views: 203,
            createdAt: '2024-01-13 16:45',
            isLiked: false
        },
        {
            id: 4,
            title: '推荐几本治愈系书籍',
            content: '最近读了几本很棒的心理学书籍，想推荐给大家，希望能帮助到需要的人...',
            author: '小王',
            authorAvatar: '👩‍🦰',
            category: '学习',
            tags: ['学习', '成长'],
            likes: 31,
            comments: 6,
            views: 178,
            createdAt: '2024-01-12 11:20',
            isLiked: false
        }
    ];
}; 