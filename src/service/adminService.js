// 管理员服务 - 处理管理员相关的API调用
import { request } from './apiUtils';

/**
 * 获取管理员统计数据
 */
export const getAdminStats = async () => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', '/admin/stats');
    
    // 模拟数据
    return {
      success: true,
      data: {
        totalUsers: 1234,
        pendingReviews: 15,
        highRiskUsers: 3,
        todayActivities: 89
      }
    };
  } catch (error) {
    console.error('获取管理员统计数据失败:', error);
    throw error;
  }
};

/**
 * 获取用户列表
 */
export const getUsers = async (page = 1, pageSize = 10, filters = {}) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', `/admin/users?page=${page}&size=${pageSize}`, { params: filters });
    
    // 模拟数据
    const mockUsers = [
      {
        id: 1,
        username: '张三',
        email: 'zhangsan@example.com',
        status: 'active',
        riskLevel: 'low',
        lastLogin: '2024-01-15 14:30',
        joinDate: '2024-01-01'
      },
      {
        id: 2,
        username: '李四',
        email: 'lisi@example.com',
        status: 'disabled',
        riskLevel: 'medium',
        lastLogin: '2024-01-14 09:15',
        joinDate: '2023-12-20'
      },
      {
        id: 3,
        username: '王五',
        email: 'wangwu@example.com',
        status: 'active',
        riskLevel: 'high',
        lastLogin: '2024-01-15 16:45',
        joinDate: '2024-01-10'
      }
    ];
    
    return {
      success: true,
      data: {
        users: mockUsers,
        total: mockUsers.length,
        page,
        pageSize
      }
    };
  } catch (error) {
    console.error('获取用户列表失败:', error);
    throw error;
  }
};

/**
 * 更新用户状态
 */
export const updateUserStatus = async (userId, status) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('PUT', `/admin/users/user/status`, { status });
    
    // 模拟成功响应
    return {
      success: true,
      message: `用户状态已更新为${status === 'active' ? '正常' : '禁用'}`
    };
  } catch (error) {
    console.error('更新用户状态失败:', error);
    throw error;
  }
};

/**
 * 获取待审核内容
 */
export const getPendingContent = async () => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', '/admin/content/pending');
    
    // 模拟数据
    const mockPosts = [
      {
        id: 1,
        title: '关于压力的一些想法',
        author: '张三',
        content: '最近工作压力很大，想找个地方倾诉一下...',
        type: 'post',
        reportCount: 2,
        submitTime: '2024-01-15 14:30',
        category: '心情分享'
      },
      {
        id: 2,
        title: '如何应对焦虑情绪',
        author: '李四',
        content: '分享一些我应对焦虑的方法...',
        type: 'post',
        reportCount: 1,
        submitTime: '2024-01-15 10:15',
        category: '经验分享'
      }
    ];

    const mockComments = [
      {
        id: 101,
        content: '我也有同样的感受，我们可以互相支持',
        author: '王五',
        postTitle: '关于压力的一些想法',
        type: 'comment',
        reportCount: 1,
        submitTime: '2024-01-15 15:00'
      }
    ];
    
    return {
      success: true,
      data: {
        posts: mockPosts,
        comments: mockComments
      }
    };
  } catch (error) {
    console.error('获取待审核内容失败:', error);
    throw error;
  }
};

/**
 * 审核内容
 */
export const reviewContent = async (contentId, contentType, action) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('POST', `/admin/content/${contentType}/${contentId}/review`, { action });
    
    // 模拟成功响应
    return {
      success: true,
      message: `内容已${action === 'approve' ? '通过' : '拒绝'}`
    };
  } catch (error) {
    console.error('审核内容失败:', error);
    throw error;
  }
};

/**
 * 获取高危用户列表
 */
export const getHighRiskUsers = async () => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', '/admin/users/user');
    
    // 模拟数据
    const mockUsers = [
      {
        id: 1,
        username: '张三',
        email: 'zhangsan@example.com',
        riskLevel: 'high',
        riskScore: 85,
        lastActivity: '2024-01-15 14:30',
        riskFactors: ['情绪低落', '睡眠问题', '社交回避'],
        lastContact: '2024-01-10 09:00',
        contactStatus: 'pending',
        priority: 'urgent'
      },
      {
        id: 2,
        username: '李四',
        email: 'lisi@example.com',
        riskLevel: 'high',
        riskScore: 78,
        lastActivity: '2024-01-14 16:45',
        riskFactors: ['焦虑倾向', '压力过大'],
        lastContact: '2024-01-12 14:30',
        contactStatus: 'contacted',
        priority: 'high'
      },
      {
        id: 3,
        username: '王五',
        email: 'wangwu@example.com',
        riskLevel: 'medium',
        riskScore: 65,
        lastActivity: '2024-01-15 10:20',
        riskFactors: ['学业压力'],
        lastContact: null,
        contactStatus: 'pending',
        priority: 'medium'
      }
    ];
    
    return {
      success: true,
      data: mockUsers
    };
  } catch (error) {
    console.error('获取高危用户列表失败:', error);
    throw error;
  }
};

/**
 * 记录用户回访
 */
export const recordUserContact = async (userId, contactData) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('POST', `/admin/users/user/contact`, contactData);
    
    // 模拟成功响应
    return {
      success: true,
      message: '回访记录已保存'
    };
  } catch (error) {
    console.error('记录用户回访失败:', error);
    throw error;
  }
};

/**
 * 标记用户为已处理
 */
export const markUserAsResolved = async (userId) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('PUT', `/admin/users/user/resolve`);
    
    // 模拟成功响应
    return {
      success: true,
      message: '用户已标记为已处理'
    };
  } catch (error) {
    console.error('标记用户失败:', error);
    throw error;
  }
};

/**
 * 获取用户详情
 */
export const getUserDetail = async (userId) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', `/admin/users/user
    
    // 模拟数据
    return {
      success: true,
      data: {
        id: userId,
        username: '用户' + userId,
        email: `user${userId}@example.com`,
        profile: {
          nickname: '用户昵称',
          avatar: null,
          age: 22,
          gender: 'male'
        },
        status: 'active',
        riskLevel: 'low',
        riskScore: 45,
        joinDate: '2024-01-01',
        lastLogin: '2024-01-15 14:30',
        activityCount: 156,
        postCount: 23,
        commentCount: 89
      }
    };
  } catch (error) {
    console.error('获取用户详情失败:', error);
    throw error;
  }
};

/**
 * 获取举报列表
 */
export const getReports = async (filters = {}) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', '/admin/reports', { params: filters });
    
    // 模拟数据
    const mockReports = [
      {
        id: 1,
        type: 'inappropriate_content',
        reason: '不当内容',
        contentType: 'post',
        contentId: 101,
        reportedContent: '这是一条可能不当的帖子内容...',
        reportedUser: '张三',
        reporter: '李四',
        reportTime: '2024-01-15 14:30',
        status: 'pending',
        priority: 'medium',
        description: '用户发布了不当言论，可能影响社区氛围'
      },
      {
        id: 2,
        type: 'harassment',
        reason: '骚扰他人',
        contentType: 'comment',
        contentId: 201,
        reportedContent: '这是一条评论内容...',
        reportedUser: '王五',
        reporter: '赵六',
        reportTime: '2024-01-15 16:20',
        status: 'pending',
        priority: 'high',
        description: '该用户在评论中骚扰其他用户'
      },
      {
        id: 3,
        type: 'spam',
        reason: '垃圾信息',
        contentType: 'post',
        contentId: 102,
        reportedContent: '广告内容或垃圾信息...',
        reportedUser: '刘七',
        reporter: '陈八',
        reportTime: '2024-01-14 10:15',
        status: 'resolved',
        priority: 'low',
        description: '发布垃圾广告信息',
        resolution: '已删除相关内容并警告用户'
      }
    ];
    
    return {
      success: true,
      data: mockReports
    };
  } catch (error) {
    console.error('获取举报列表失败:', error);
    throw error;
  }
};

/**
 * 处理举报
 */
export const handleReport = async (reportId, action, resolution = '') => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('POST', `/admin/reports/${reportId}/handle`, { action, resolution });
    
    // 模拟成功响应
    return {
      success: true,
      message: `举报已${action === 'accept' ? '处理' : '驳回'}`
    };
  } catch (error) {
    console.error('处理举报失败:', error);
    throw error;
  }
};

/**
 * 获取举报详情
 */
export const getReportDetail = async (reportId) => {
  try {
    // TODO: 替换为真实的API调用
    // return await request('GET', `/admin/reports/${reportId}`);
    
    // 模拟数据
    return {
      success: true,
      data: {
        id: reportId,
        type: 'inappropriate_content',
        reason: '不当内容',
        contentType: 'post',
        contentId: 101,
        reportedContent: '这是一条详细的被举报内容...',
        reportedUser: '张三',
        reporter: '李四',
        reportTime: '2024-01-15 14:30',
        status: 'pending',
        priority: 'medium',
        description: '详细的举报描述...',
        evidence: ['截图1.jpg', '截图2.jpg'],
        history: [
          {
            action: 'created',
            time: '2024-01-15 14:30',
            operator: '系统',
            note: '举报创建'
          }
        ]
      }
    };
  } catch (error) {
    console.error('获取举报详情失败:', error);
    throw error;
  }
}; 