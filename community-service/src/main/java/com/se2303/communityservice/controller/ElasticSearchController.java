package com.se2303.communityservice.controller;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.IElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class ElasticSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);

    @Autowired
    private IElasticSearchService elasticSearchService;

    /**
     * 智能搜索帖子接口
     *
     * @param keyword 关键词
     * @param tags 标签（用逗号分隔）
     * @param sortBy 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse> smartGetPosts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "sortBy", defaultValue = "created_at") String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            logger.info("智能搜索请求 - 关键词: {}, 标签: {}, 排序: {}, 页码: {}, 大小: {}", 
                       keyword, tags, sortBy, page, size);

            // 解析标签
            List<String> tagList = null;
            if (tags != null && !tags.trim().isEmpty()) {
                tagList = Arrays.asList(tags.split(","));
            }

            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size);

            // 执行搜索 (reviewStatus设为"APPROVED"，只搜索已审核的帖子)
            PageDTO<PostBriefInfo> result = elasticSearchService.smartSearch(keyword, tagList, "APPROVED", sortBy, pageable);

            logger.info("智能搜索完成，返回 {} 条结果", result.getContent().size());

            return ResponseEntity.ok(new ApiResponse(true, result, "搜索成功"));
        } catch (Exception e) {
            logger.error("智能搜索失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "搜索失败: " + e.getMessage()));
        }
    }

    /**
     * 同步数据接口（管理员使用）
     *
     * @return 同步结果
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse> syncData() {
        try {
            logger.info("开始同步数据到ElasticSearch");
            elasticSearchService.syncFromDatabase();
            logger.info("数据同步完成");
            return ResponseEntity.ok(new ApiResponse(true, "同步完成", "数据同步成功"));
        } catch (Exception e) {
            logger.error("数据同步失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "数据同步失败: " + e.getMessage()));
        }
    }

    /**
     * 强制重建索引接口（管理员使用）
     * 删除现有索引，重新创建并同步所有数据
     * 适用于索引结构变化或数据严重不一致的情况
     *
     * @return 重建结果
     */
    @PostMapping("/rebuild")
    public ResponseEntity<ApiResponse> forceRebuildIndex() {
        try {
            logger.info("开始强制重建ElasticSearch索引");
            elasticSearchService.forceRebuildIndex();
            logger.info("索引重建完成");
            return ResponseEntity.ok(new ApiResponse(true, "重建完成", "索引重建并数据同步成功"));
        } catch (Exception e) {
            logger.error("索引重建失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "索引重建失败: " + e.getMessage()));
        }
    }

    /**
     * 创建索引接口（管理员使用）
     *
     * @return 创建结果
     */
    @PostMapping("/index/create")
    public ResponseEntity<ApiResponse> createIndex() {
        try {
            logger.info("开始创建ElasticSearch索引");
            elasticSearchService.createIndex();
            logger.info("索引创建完成");
            return ResponseEntity.ok(new ApiResponse(true, "索引创建完成", "索引创建成功"));
        } catch (Exception e) {
            logger.error("索引创建失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "索引创建失败: " + e.getMessage()));
        }
    }

    /**
     * 删除索引接口（管理员使用）
     *
     * @return 删除结果
     */
    @DeleteMapping("/index/delete")
    public ResponseEntity<ApiResponse> deleteIndex() {
        try {
            logger.info("开始删除ElasticSearch索引");
            elasticSearchService.deleteIndex();
            logger.info("索引删除完成");
            return ResponseEntity.ok(new ApiResponse(true, "索引删除完成", "索引删除成功"));
        } catch (Exception e) {
            logger.error("索引删除失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "索引删除失败: " + e.getMessage()));
        }
    }

    /**
     * 检查索引状态接口
     *
     * @return 索引状态
     */
    @GetMapping("/index/status")
    public ResponseEntity<ApiResponse> checkIndexStatus() {
        try {
            boolean exists = elasticSearchService.indexExists();
            String status = exists ? "索引已存在" : "索引不存在";
            logger.info("索引状态检查: {}", status);
            return ResponseEntity.ok(new ApiResponse(true, status, "检查完成"));
        } catch (Exception e) {
            logger.error("索引状态检查失败", e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, "检查失败: " + e.getMessage()));
        }
    }
} 