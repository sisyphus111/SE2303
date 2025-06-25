// API服务统一导出文件

// 认证相关
export * from './authService';

// 用户相关
export * from './userService';

// AI聊天相关
export * from './aiChatService';

// 日记相关
export * from './diaryService';

// 社区相关
export * from './communityService';

// 评论相关
export * from './commentService';

// 首页相关
export * from './homeService';

// 心理健康资源相关
export * from './mentalResourceService';

// 文章相关
export * from './articleService';

// 文件上传相关
export * from './uploadService';

// 工具函数
export * from './apiUtils';

// 默认导出所有服务
import * as authService from './authService';
import * as userService from './userService';
import * as aiChatService from './aiChatService';
import * as diaryService from './diaryService';
import * as communityService from './communityService';
import * as commentService from './commentService';
import * as homeService from './homeService';
import * as mentalResourceService from './mentalResourceService';
import * as articleService from './articleService';
import * as uploadService from './uploadService';
import * as apiUtils from './apiUtils';

export default {
  auth: authService,
  user: userService,
  aiChat: aiChatService,
  diary: diaryService,
  community: communityService,
  comment: commentService,
  home: homeService,
  mentalResource: mentalResourceService,
  article: articleService,
  upload: uploadService,
  utils: apiUtils,
}; 