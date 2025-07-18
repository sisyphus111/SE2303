package com.se2303.communityservice.config;

import com.se2303.communityservice.service.IElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch初始化器
 * 在服务启动时自动创建索引并同步数据
 */
@Component
public class ElasticsearchInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchInitializer.class);

    @Autowired
    private IElasticSearchService elasticSearchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("🚀 开始初始化Elasticsearch...");
        
        try {
            // 1. 检查索引是否存在
            boolean indexExists = elasticSearchService.indexExists();
            
            if (!indexExists) {
                logger.info("📝 索引不存在，正在创建索引...");
                elasticSearchService.createIndex();
                logger.info("✅ 索引创建成功");
            } else {
                logger.info("📋 索引已存在，跳过创建步骤");
            }
            
            // 2. 同步数据库数据到Elasticsearch
            logger.info("🔄 开始同步数据库数据到Elasticsearch...");
            elasticSearchService.syncFromDatabase();
            logger.info("✅ 数据同步完成");
            
            logger.info("🎉 Elasticsearch初始化完成！");
            
        } catch (Exception e) {
            logger.error("❌ Elasticsearch初始化失败: {}", e.getMessage(), e);
            // 不抛出异常，避免阻止服务启动
            logger.warn("⚠️ Elasticsearch初始化失败，但服务将继续启动。请检查Elasticsearch连接配置。");
        }
    }
} 