import {get, post, put, del} from './community_common';
import {message} from 'antd';

// 获取帖子列表

export const getPostList = async (params = {}, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }

    if (params.page < 1) {
        throw new Error('页码必须大于或等于1');
    }
    if (params.size < 1 || params.size > 100) {
        throw new Error('每页大小必须在1到100之间');
    }
    if (params.sortBy && !['latest', 'mostCommented', 'mostViewed'].includes(params.sortBy)) {
        throw new Error('排序方式必须是 latest、mostCommented 或 mostViewed');
    }

    const queryParams = {
        page: params.page || 1,
        size: params.size || 5,
        sortBy: params.sortBy || 'latest',
        ...(params.category && {category: params.category}),
        ...(params.keyword && {keyword: params.keyword}),
    };
    const queryString = new URLSearchParams(queryParams).toString();

    console.log(queryString);
    try {
        const response = await get(`/community/posts/get_posts`, {queryParams});
        return {
            success: true,
            data: response,
            message: "帖子获取成功",
        };

    } catch (error) {
        message.error('获取帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '获取帖子失败',
        };
    }
};

// 创建帖子
export const createPost = async (postData, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }

    if (!postData.title || postData.title.trim() === '') {
        throw new Error('帖子标题不能为空');
    }
    if (!postData.content || postData.content.trim() === '') {
        throw new Error('帖子内容不能为空');
    }
    if (!postData.tags || !Array.isArray(postData.tags)) {
        throw new Error('帖子标签必须为数组');
    }

    try {
        const response = await post('/community/posts/send', postData);

        message.success('帖子创建成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('创建帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '创建帖子失败',
        };
    }
};

// 获取帖子详情
export const getPostDetail = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        const response = await get(`/community/posts/get_post/${postId}`);

        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('获取帖子详情失败：' + error.message);
        return {
            success: false,
            error: error.message || '获取帖子详情失败',
        };
    }
};

// 更新帖子
export const updatePost = async (postId, postData, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    if (!postData.title || postData.title.trim() === '') {
        throw new Error('帖子标题不能为空');
    }
    if (!postData.content || postData.content.trim() === '') {
        throw new Error('帖子内容不能为空');
    }
    if (!postData.tags || !Array.isArray(postData.tags)) {
        throw new Error('帖子标签必须为数组');
    }

    try {
        const response = await put(`/community/posts/update/${postId}`, {postData});

        message.success('帖子更新成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('更新帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '更新帖子失败',
        };
    }
};

// 删除帖子
export const deletePost = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (!postId || postId.trim() === '') {
        throw new Error('帖子ID不能为空');
    }
    try {
        const response = await put(`/community/posts/delete/${postId}`, {});

        message.success('帖子删除成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('删除帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '删除帖子失败',
        };
    }
};

// 点赞帖子
export const LikePost = async (postId) => {

    // if (!postId || postId.trim() === '') {
    //     throw new Error('帖子ID不能为空');
    // }
    try {
        console.log("开始点赞操作111");
        const response = await post(`/community/posts/like/${postId}`, {});
        message.success('点赞成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('点赞失败：' + error.message);
        return {
            success: false,
            error: error.message || '点赞失败',
        };
    }
};

// 取消点赞帖子
export const UnlikePost = async (postId) => {
    // if (!auth.userId || auth.valid === false) {
    //     message.error('请先登录');
    //     throw new Error('用户未登录');
    // }
    // if (!postId || postId.trim() === '') {
    //     throw new Error('帖子ID不能为空');
    // }
    try {
        const response = await del(`/community/posts/unlike/${postId}`, {});
        message.success('取消点赞成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('取消点赞失败：' + error.message);
        return {
            success: false,
            error: error.message || '取消点赞失败',
        };
    }
};

// 获取我的帖子
export const MyPost = async (auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await get(`/community/posts/my_post`, {});

        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('获取我的帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '获取我的帖子失败',
        };
    }
};

// 获取所有标签
export const getAllTags = async (auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await get(`/community/posts/get_tags`, {});
        return {
            success: true,
            data: response,
            message: "标签获取成功",
        };

    } catch (error) {
        message.error('获取标签失败：' + error.message);
        return {
            success: false,
            error: error.message || '获取标签失败',
        };
    }
};

// 获取帖子评论
export const getCommentOfPost = async (postId, page = 1, size = 5, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await get(`/community/posts/get_comments/${postId}?page=${page}&size=${size}`);

        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('获取评论失败：' + error.message);
        return {
            success: false,
            error: error.message || '获取评论失败',
        };
    }
};

// 给对应帖子发评论
export const sendCommentToPost = async (postId, content, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await post(`/community/posts/add_comment/${postId}`, {content});

        message.success('评论发表成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('评论发表失败：' + error.message);
        return {
            success: false,
            error: error.message || '评论发表失败',
        };
    }
};

// 收藏帖子
export const collectPost = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await post(`/community/posts/collect/${postId}`, {});
        message.success('收藏成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('收藏失败：' + error.message);
        return {
            success: false,
            error: error.message || '收藏失败',
        };
    }
};

// 取消收藏帖子
export const delCollectedPost = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    try {
        const response = await del(`/community/posts/delete/collection/${postId}`, {});
        message.success('取消收藏成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('取消收藏失败：' + error.message);
        return {
            success: false,
            error: error.message || '取消收藏失败',
        };
    }
};

// 审核通过帖子
export const approvePost = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (auth.identity !== false) {
        message.error('仅管理员可审核帖子');
        throw new Error('权限不足');
    }
    try {
        const response = await put(`/community/posts/approve/${postId}`, {});

        message.success('帖子审核通过');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('审核帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '审核帖子失败',
        };
    }
};

// 封禁帖子
export const banPost = async (postId, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (auth.identity !== false) {
        message.error('仅管理员可封禁帖子');
        throw new Error('权限不足');
    }
    try {
        const response = await put(`/community/posts/ban/${postId}`, {});

        message.success('帖子封禁成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('封禁帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '封禁帖子失败',
        };
    }
};

// 举报帖子
export const reportPost = async (postId, reason, auth = {}) => {
    if (!auth.userId || auth.valid === false) {
        message.error('请先登录');
        throw new Error('用户未登录');
    }
    if (!reason || reason.trim() === '') {
        throw new Error('举报原因不能为空');
    }
    try {
        const response = await post(`/community/posts/report/${postId}`, {reason});

        message.success('举报成功');
        return {
            success: true,
            data: response,
            message: response.message,
        };

    } catch (error) {
        message.error('举报帖子失败：' + error.message);
        return {
            success: false,
            error: error.message || '举报帖子失败',
        };
    }
};