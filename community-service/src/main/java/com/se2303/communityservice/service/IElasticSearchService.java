package com.se2303.communityservice.service;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.PostDocument;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IElasticSearchService {
    
    /**
     * 通用智能搜索帖子 - 唯一的搜索接口
     * 支持关键词搜索、标签筛选、审核状态过滤、排序等功能
     * 所有参数都是可选的，传null表示不使用该筛选条件
     * 
     * @param keyword 关键词（可选，支持语义搜索、同义词匹配）
     * @param tags 标签列表（可选，传null或空列表表示不按标签筛选）
     * @param reviewStatus 审核状态（可选，传null表示不筛选状态）
     * @param sortBy 排序字段（可选，传null使用默认排序）
     * @param pageable 分页参数
     * @return 搜索结果
     */
    PageDTO<PostBriefInfo> smartSearch(String keyword, List<String> tags, String reviewStatus, String sortBy, Pageable pageable);
    
    /**
     * 索引单个帖子
     * @param post 帖子实体
     */
    void indexPost(Post post);
    
    /**
     * 批量索引帖子
     * @param posts 帖子列表
     */
    void indexPosts(List<Post> posts);
    
    /**
     * 删除索引中的帖子
     * @param postId 帖子ID
     */
    void deletePost(String postId);
    
    /**
     * 更新帖子索引
     * @param post 帖子实体
     */
    void updatePost(Post post);
    
    /**
     * 根据ID获取帖子文档
     * @param id 帖子ID
     * @return 帖子文档
     */
    PostDocument findById(String id);
    
    /**
     * 检查索引是否存在
     * @return 是否存在
     */
    boolean indexExists();
    
    /**
     * 创建索引
     */
    void createIndex();
    
    /**
     * 删除索引
     */
    void deleteIndex();
    
    /**
     * 同步数据库数据到ElasticSearch
     */
    void syncFromDatabase();
    
    /**
     * 强制重建索引并同步所有数据
     * 这个方法会删除现有索引，重新创建，然后同步所有数据
     * 适用于索引结构发生变化或数据严重不一致的情况
     */
    void forceRebuildIndex();
    
    /**
     * 同步单个帖子的状态变化
     * 根据帖子的当前状态决定是添加、更新还是删除ES中的文档
     * @param post 帖子实体
     */
    void syncPostStatus(Post post);
    
    /**
     * 批量同步帖子状态变化
     * @param posts 帖子列表
     */
    void syncPostsStatus(List<Post> posts);
} 