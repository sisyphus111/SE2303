// 社区相关API服务
import { request } from './apiUtils';

/**
 * 获取帖子列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.category - 分类（根据页面：心情分享、压力管理、心理健康、资源推荐、互助交流）
 * @param {Array<string>} params.tags - 标签数组
 * @param {string} params.sortBy - 排序方式（最新、最热、最多评论、最多浏览）
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise<Object>} 帖子列表
 */
export const getPostList = async (params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return request(`/posts?${queryString}`);
};

/**
 * 创建帖子
 * @param {Object} postData - 帖子数据
 * @param {string} postData.id - 帖子ID
 * @param {string} postData.title - 标题
 * @param {string} postData.content - 内容
 * @param {string} postData.author - 作者
 * @param {string} postData.authorAvatar - 作者头像
 * @param {string} postData.category - 分类
 * @param {Array<string>} postData.tags - 标签数组
 * @param {number} postData.likes - 点赞数
 * @param {number} postData.comments - 评论数
 * @param {number} postData.views - 浏览数
 * @param {string} postData.createdAt - 创建时间
 * @param {boolean} postData.isLiked - 是否已点赞
 * @returns {Promise<Object>} 创建的帖子
 */
export const createPost = async (postData) => {
  return request('/posts', {
    method: 'POST',
    body: JSON.stringify(postData),
  });
};

/**
 * 获取帖子详情
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 帖子详情
 */
export const getPostDetail = async (postId) => {
  return request(`/posts/${postId}`);
};

/**
 * 更新帖子
 * @param {string} postId - 帖子ID
 * @param {Object} postData - 更新的帖子数据
 * @returns {Promise<Object>} 更新结果
 */
export const updatePost = async (postId, postData) => {
  return request(`/posts/${postId}`, {
    method: 'PUT',
    body: JSON.stringify(postData),
  });
};

/**
 * 删除帖子
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 删除结果
 */
export const deletePost = async (postId) => {
  return request(`/posts/${postId}`, {
    method: 'DELETE',
  });
};

/**
 * 点赞/取消点赞帖子
 * @param {string} postId - 帖子ID
 * @returns {Promise<Object>} 操作结果
 */
export const togglePostLike = async (postId) => {
  return request(`/posts/${postId}/like`, {
    method: 'POST',
  });
};

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
      category: '压力管理',
      tags: ['工作压力', '减压方法', '心理健康'],
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
      category: '心情分享',
      tags: ['好习惯', '晨间routine', '积极心态'],
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
      category: '心理健康',
      tags: ['焦虑症', '心理治疗', '互助'],
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
      category: '资源推荐',
      tags: ['书籍推荐', '心理学', '治愈系'],
      likes: 31,
      comments: 6,
      views: 178,
      createdAt: '2024-01-12 11:20',
      isLiked: false
    }
  ];
}; 