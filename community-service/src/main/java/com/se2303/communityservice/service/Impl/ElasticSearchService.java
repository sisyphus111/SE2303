package com.se2303.communityservice.service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.PostDocument;
import com.se2303.communityservice.repository.PostElasticSearchRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.LikeRepository;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.service.IElasticSearchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Collections;
import java.util.stream.StreamSupport;

@Service
public class ElasticSearchService implements IElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
    private static final String INDEX_NAME = "posts";

    @Autowired
    private PostElasticSearchRepository postElasticSearchRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public PageDTO<PostBriefInfo> smartSearch(String keyword, List<String> tags, String reviewStatus, String sortBy, Pageable pageable) {
        logger.info("🔍 通用智能搜索 - 关键词: {}, 标签: {}, 审核状态: {}, 排序: {}, 分页: {}", keyword, tags, reviewStatus, sortBy, pageable);

        try {
            // 构建排序条件
            Sort sort = buildSort(sortBy);
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

            // 设置默认审核状态
            final String finalReviewStatus = StringUtils.hasText(reviewStatus) ? reviewStatus : "APPROVED";
            
            // 处理标签参数（支持字符串转换为列表）
            final List<String> finalTags = (tags != null && !tags.isEmpty()) ? tags : null;
            
            // 检查关键词是否有效
            final boolean hasKeyword = StringUtils.hasText(keyword);
            final boolean hasTags = finalTags != null && !finalTags.isEmpty();

            Page<PostDocument> searchResult;

            // 根据不同的搜索条件调用合适的repository方法
            if (hasKeyword && hasTags) {
                // 关键词 + 标签 + 审核状态
                searchResult = postElasticSearchRepository.searchByKeywordAndTagsAndReviewStatus(keyword, finalTags, finalReviewStatus, sortedPageable);
            } else if (hasKeyword) {
                // 仅关键词 + 审核状态
                searchResult = postElasticSearchRepository.searchByKeywordAndReviewStatus(keyword, finalReviewStatus, sortedPageable);
            } else if (hasTags) {
                // 仅标签筛选，需要手动过滤审核状态
                searchResult = postElasticSearchRepository.findByTagsIn(finalTags, sortedPageable);
            } else {
                // 无搜索条件，按审核状态返回
                searchResult = postElasticSearchRepository.findByReviewStatus(finalReviewStatus, sortedPageable);
            }

            // 转换为PostBriefInfo
            List<PostBriefInfo> postBriefInfos = searchResult.getContent()
                    .stream()
                    // 对于没有审核状态过滤的查询，手动过滤
                    .filter(doc -> finalReviewStatus.equals(doc.getReviewStatus()) || !hasTags || hasKeyword)
                    .map(this::convertToPostBriefInfo)
                    .collect(Collectors.toList());

            logger.info("✅ 搜索完成，共找到 {} 条结果", postBriefInfos.size());

            // 计算分页信息
            long totalElements = hasKeyword ? searchResult.getTotalElements() : postBriefInfos.size();
            int totalPages = hasKeyword ? searchResult.getTotalPages() : (int) Math.ceil((double) totalElements / pageable.getPageSize());

            return new PageDTO<>(
                    postBriefInfos,
                    searchResult.getNumber(),
                    searchResult.getSize(),
                    totalElements,
                    totalPages
            );

        } catch (Exception e) {
            logger.error("❌ 智能搜索失败", e);
            throw new RuntimeException("搜索失败: " + e.getMessage(), e);
        }
    }



    @Override
    public void indexPost(Post post) {
        try {
            PostDocument postDocument = PostDocument.fromPost(post);
            postElasticSearchRepository.save(postDocument);
            logger.info("帖子索引成功: {}", post.getId());
        } catch (Exception e) {
            logger.error("帖子索引失败: {}", post.getId(), e);
            throw new RuntimeException("帖子索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void indexPosts(List<Post> posts) {
        try {
            List<PostDocument> postDocuments = posts.stream()
                    .map(PostDocument::fromPost)
                    .collect(Collectors.toList());
            postElasticSearchRepository.saveAll(postDocuments);
            logger.info("批量索引成功，共 {} 条帖子", posts.size());
        } catch (Exception e) {
            logger.error("批量索引失败", e);
            throw new RuntimeException("批量索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePost(String postId) {
        try {
            postElasticSearchRepository.deleteById(postId);
            logger.info("删除帖子索引成功: {}", postId);
        } catch (Exception e) {
            logger.error("删除帖子索引失败: {}", postId, e);
            throw new RuntimeException("删除帖子索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePost(Post post) {
        try {
            PostDocument postDocument = PostDocument.fromPost(post);
            postElasticSearchRepository.save(postDocument);
            logger.info("更新帖子索引成功: {}", post.getId());
        } catch (Exception e) {
            logger.error("更新帖子索引失败: {}", post.getId(), e);
            throw new RuntimeException("更新帖子索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public PostDocument findById(String id) {
        Optional<PostDocument> document = postElasticSearchRepository.findById(id);
        return document.orElse(null);
    }

    @Override
    public boolean indexExists() {
        try {
            ExistsRequest request = ExistsRequest.of(e -> e.index(INDEX_NAME));
            return elasticsearchClient.indices().exists(request).value();
        } catch (Exception e) {
            logger.error("检查索引是否存在时发生错误", e);
            return false;
        }
    }

    @Override
    public void createIndex() {
        try {
            if (!indexExists()) {
                CreateIndexRequest request = CreateIndexRequest.of(c -> c
                        .index(INDEX_NAME)
                        .settings(s -> s
                                .numberOfShards("1")
                                .numberOfReplicas("1")
                        )
                        .mappings(m -> m
                                .properties("id", p -> p.keyword(k -> k))
                                .properties("title", p -> p.text(t -> t
                                        .analyzer("standard")
                                        .searchAnalyzer("standard")
                                ))
                                .properties("content", p -> p.text(t -> t
                                        .analyzer("standard")
                                        .searchAnalyzer("standard")
                                ))
                                .properties("userId", p -> p.keyword(k -> k))
                                .properties("tags", p -> p.keyword(k -> k))
                                .properties("createdAt", p -> p.date(d -> d
                                        .format("yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis")
                                ))
                                .properties("reviewStatus", p -> p.keyword(k -> k))
                        )
                );

                elasticsearchClient.indices().create(request);
                logger.info("创建索引成功: {}", INDEX_NAME);
            } else {
                logger.info("索引已存在: {}", INDEX_NAME);
            }
        } catch (Exception e) {
            logger.error("创建索引失败", e);
            throw new RuntimeException("创建索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteIndex() {
        try {
            if (indexExists()) {
                DeleteIndexRequest request = DeleteIndexRequest.of(d -> d.index(INDEX_NAME));
                elasticsearchClient.indices().delete(request);
                logger.info("删除索引成功: {}", INDEX_NAME);
            } else {
                logger.info("索引不存在: {}", INDEX_NAME);
            }
        } catch (Exception e) {
            logger.error("删除索引失败", e);
            throw new RuntimeException("删除索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void syncFromDatabase() {
        try {
            logger.info("🔄 开始全量同步数据库数据到ElasticSearch...");
            
            // 1. 获取数据库中所有已审核通过的帖子
            List<Post> approvedPosts = postRepository.findAllByReviewStatus("APPROVED");
            logger.info("📊 数据库中共有 {} 条已审核通过的帖子", approvedPosts.size());
            
            // 2. 获取ElasticSearch中现有的所有文档
            List<PostDocument> existingDocs = getAllPostDocuments();
            logger.info("📊 ElasticSearch中现有 {} 条文档", existingDocs.size());
            
            // 3. 构建数据库中APPROVED帖子的ID集合（转换为String）
            Set<String> approvedPostIds = approvedPosts.stream()
                    .map(post -> post.getId().toString())
                    .collect(Collectors.toSet());
            
            // 4. 构建ElasticSearch中现有文档的ID集合
            Set<String> existingDocIds = existingDocs.stream()
                    .map(PostDocument::getId)
                    .collect(Collectors.toSet());
            
            // 5. 找出需要删除的文档（存在于ES但不在数据库APPROVED列表中）
            Set<String> toDeleteIds = existingDocIds.stream()
                    .filter(id -> !approvedPostIds.contains(id))
                    .collect(Collectors.toSet());
            
            // 6. 删除不一致的文档
            if (!toDeleteIds.isEmpty()) {
                logger.info("🗑️ 发现 {} 条需要删除的过期文档，开始清理...", toDeleteIds.size());
                for (String docId : toDeleteIds) {
                    try {
                        postElasticSearchRepository.deleteById(docId);
                        logger.debug("✅ 已删除过期文档: {}", docId);
                    } catch (Exception e) {
                        logger.warn("⚠️ 删除文档失败 {}: {}", docId, e.getMessage());
                    }
                }
                logger.info("✅ 过期文档清理完成");
            } else {
                logger.info("✅ 无需清理过期文档");
            }
            
            // 7. 添加或更新所有APPROVED帖子到ElasticSearch
            if (!approvedPosts.isEmpty()) {
                logger.info("📝 开始更新 {} 条帖子到ElasticSearch...", approvedPosts.size());
                
                // 分批处理，避免一次性处理过多数据
                int batchSize = 100;
                for (int i = 0; i < approvedPosts.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, approvedPosts.size());
                    List<Post> batch = approvedPosts.subList(i, endIndex);
                    
                    try {
                        indexPosts(batch);
                        logger.debug("✅ 批次 {}/{} 处理完成", (i / batchSize) + 1, 
                                   (approvedPosts.size() + batchSize - 1) / batchSize);
                    } catch (Exception e) {
                        logger.error("❌ 批次处理失败 {}-{}: {}", i, endIndex - 1, e.getMessage());
                        // 继续处理下一批，不中断整个同步过程
                    }
                }
                logger.info("✅ 帖子数据更新完成");
            } else {
                logger.info("✅ 数据库中无APPROVED帖子需要同步");
            }
            
            // 8. 同步完成统计
            logger.info("🎉 数据同步完成！");
            logger.info("📊 同步统计:");
            logger.info("   - 数据库APPROVED帖子: {} 条", approvedPosts.size());
            logger.info("   - 清理过期文档: {} 条", toDeleteIds.size());
            logger.info("   - 同步到ES的帖子: {} 条", approvedPosts.size());
            
        } catch (Exception e) {
            logger.error("❌ 数据同步失败", e);
            throw new RuntimeException("数据同步失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取ElasticSearch中的所有PostDocument
     * @return 所有文档列表
     */
    private List<PostDocument> getAllPostDocuments() {
        try {
            // 使用Spring Data ElasticSearch的findAll方法获取所有文档
            Iterable<PostDocument> documents = postElasticSearchRepository.findAll();
            return StreamSupport.stream(documents.spliterator(), false)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("⚠️ 获取ElasticSearch中现有文档失败，将跳过清理步骤: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void forceRebuildIndex() {
        try {
            logger.info("🔨 开始强制重建索引...");
            
            // 1. 删除现有索引
            if (indexExists()) {
                logger.info("🗑️ 删除现有索引...");
                deleteIndex();
                logger.info("✅ 现有索引已删除");
            }
            
            // 2. 等待一段时间确保索引完全删除
            Thread.sleep(1000);
            
            // 3. 重新创建索引
            logger.info("📝 重新创建索引...");
            createIndex();
            logger.info("✅ 索引创建完成");
            
            // 4. 等待索引就绪
            Thread.sleep(2000);
            
            // 5. 同步所有数据
            logger.info("🔄 开始同步所有数据...");
            syncFromDatabase();
            
            logger.info("🎉 强制重建索引完成！");
            
        } catch (Exception e) {
            logger.error("❌ 强制重建索引失败", e);
            throw new RuntimeException("强制重建索引失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void syncPostStatus(Post post) {
        try {
            if (post == null) {
                logger.warn("⚠️ 帖子为空，跳过同步");
                return;
            }
            
            String postId = post.getId().toString();
            logger.debug("🔄 同步帖子状态: ID={}, Status={}", postId, post.getReviewStatus());
            
            // 如果帖子状态是APPROVED，添加或更新到ES
            if ("APPROVED".equals(post.getReviewStatus())) {
                indexPost(post);
                logger.debug("✅ 帖子已添加/更新到ElasticSearch: {}", postId);
            } else {
                // 如果状态不是APPROVED，从ES中删除（如果存在）
                try {
                    PostDocument existingDoc = findById(postId);
                    if (existingDoc != null) {
                        deletePost(postId);
                        logger.debug("✅ 帖子已从ElasticSearch中删除: {}", postId);
                    } else {
                        logger.debug("📋 帖子在ElasticSearch中不存在，无需删除: {}", postId);
                    }
                } catch (Exception e) {
                    logger.warn("⚠️ 检查/删除帖子时出错 {}: {}", postId, e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ 同步帖子状态失败: {}", post != null ? post.getId() : "unknown", e);
            // 不抛出异常，避免影响业务流程
        }
    }

    @Override
    public void syncPostsStatus(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            logger.debug("📋 无帖子需要同步状态");
            return;
        }
        
        try {
            logger.info("🔄 开始批量同步 {} 条帖子状态...", posts.size());
            
            // 分离APPROVED和非APPROVED的帖子
            List<Post> approvedPosts = posts.stream()
                    .filter(post -> "APPROVED".equals(post.getReviewStatus()))
                    .collect(Collectors.toList());
            
            List<Post> nonApprovedPosts = posts.stream()
                    .filter(post -> !"APPROVED".equals(post.getReviewStatus()))
                    .collect(Collectors.toList());
            
            // 批量索引APPROVED帖子
            if (!approvedPosts.isEmpty()) {
                logger.debug("📝 批量更新 {} 条APPROVED帖子到ElasticSearch", approvedPosts.size());
                indexPosts(approvedPosts);
            }
            
            // 删除非APPROVED帖子
            if (!nonApprovedPosts.isEmpty()) {
                logger.debug("🗑️ 删除 {} 条非APPROVED帖子从ElasticSearch", nonApprovedPosts.size());
                for (Post post : nonApprovedPosts) {
                    try {
                        String postId = post.getId().toString();
                        PostDocument existingDoc = findById(postId);
                        if (existingDoc != null) {
                            deletePost(postId);
                        }
                    } catch (Exception e) {
                        logger.warn("⚠️ 删除帖子失败 {}: {}", post.getId(), e.getMessage());
                    }
                }
            }
            
            logger.info("✅ 批量状态同步完成 - 更新: {}, 删除: {}", 
                       approvedPosts.size(), nonApprovedPosts.size());
            
        } catch (Exception e) {
            logger.error("❌ 批量同步帖子状态失败", e);
            // 不抛出异常，避免影响业务流程
        }
    }

    /**
     * 构建排序条件
     */
    private Sort buildSort(String sortBy) {
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy.toLowerCase()) {
                case "created_at":
                case "time":
                    return Sort.by(Sort.Direction.DESC, "createdAt");
                case "title":
                    return Sort.by(Sort.Direction.ASC, "title");
                default:
                    return Sort.by(Sort.Direction.DESC, "createdAt");
            }
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    /**
     * 将PostDocument转换为PostBriefInfo
     */
    private PostBriefInfo convertToPostBriefInfo(PostDocument document) {
        PostBriefInfo postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(Long.valueOf(document.getId()));
        postBriefInfo.setTitle(document.getTitle());
        postBriefInfo.setContent(document.getContent());
        postBriefInfo.setUserId(document.getUserId());
        postBriefInfo.setTags(document.getTags());
        postBriefInfo.setCreatedAt(document.getCreatedAt());
        postBriefInfo.setReviewStatus(document.getReviewStatus());
        
        // 获取点赞数和评论数
        Long postId = Long.valueOf(document.getId());
        Long likeCount = likeRepository.countByPostId(postId);
        Long commentCount = commentRepository.countByPostId(postId);
        
        postBriefInfo.setLikeCount(likeCount != null ? likeCount : 0L);
        postBriefInfo.setCommentCount(commentCount != null ? commentCount : 0L);
        
        return postBriefInfo;
    }
} 