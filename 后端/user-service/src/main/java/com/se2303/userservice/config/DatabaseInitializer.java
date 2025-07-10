package com.se2303.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 数据库初始化类
 * 在应用启动时执行数据库初始化操作 - 直接执行 db_init.sql 脚本
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始执行数据库初始化（每次启动都会清空并重新初始化数据）...");
        
        try {
            // 直接执行 SQL 脚本（包含清空表和重新插入数据）
            executeSqlScript();
            logger.info("数据库初始化完成");
        } catch (Exception e) {
            logger.error("数据库初始化失败", e);
            throw e;
        }
    }



    /**
     * 执行 SQL 脚本文件
     */
    private void executeSqlScript() throws Exception {
        logger.info("执行数据库初始化脚本: db_init.sql");
        
        ClassPathResource resource = new ClassPathResource("db_init.sql");
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // 跳过注释行和空行
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line).append(" ");
                
                // 如果遇到分号，执行SQL语句
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    if (!sql.isEmpty()) {
                        executeSqlStatement(sql);
                    }
                    sqlBuilder.setLength(0);
                }
            }
            
            // 执行最后一个SQL语句（如果没有以分号结尾）
            String remainingSql = sqlBuilder.toString().trim();
            if (!remainingSql.isEmpty()) {
                executeSqlStatement(remainingSql);
            }
            
        } catch (Exception e) {
            logger.error("读取或执行SQL脚本失败", e);
            throw e;
        }
    }

    /**
     * 执行单个SQL语句
     */
    private void executeSqlStatement(String sql) {
        try {
            // 移除末尾的分号
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            
            // 跳过某些不需要执行的语句
            String upperSql = sql.toUpperCase().trim();
            if (upperSql.startsWith("CREATE DATABASE") || 
                upperSql.startsWith("USE ") ||
                upperSql.equals("COMMIT")) {
                logger.debug("跳过语句: {}", sql.substring(0, Math.min(50, sql.length())));
                return;
            }
            
            // 对于关键的清理操作，记录详细信息
            if (upperSql.startsWith("TRUNCATE") || upperSql.startsWith("DELETE")) {
                logger.info("执行关键清理操作: {}", sql);
            } else {
                logger.debug("执行SQL: {}", sql.substring(0, Math.min(100, sql.length())));
            }
            
            jdbcTemplate.execute(sql);
            
            // 对于关键的清理操作，记录成功信息
            if (upperSql.startsWith("TRUNCATE") || upperSql.startsWith("DELETE")) {
                logger.info("清理操作执行成功: {}", sql);
            }
            
        } catch (Exception e) {
            // 对于关键的数据清理操作，应该抛出异常而不是仅记录警告
            String upperSql = sql.toUpperCase().trim();
            if (upperSql.startsWith("DELETE") || 
                upperSql.startsWith("TRUNCATE") ||
                upperSql.startsWith("SET FOREIGN_KEY_CHECKS") ||
                upperSql.startsWith("ALTER TABLE") && upperSql.contains("AUTO_INCREMENT")) {
                logger.error("关键SQL语句执行失败: {} - 错误: {}", 
                           sql.substring(0, Math.min(100, sql.length())), 
                           e.getMessage());
                throw new RuntimeException("数据库初始化关键步骤失败: " + e.getMessage(), e);
            }
            
            // 对于CREATE TABLE等操作失败是正常的（比如表已存在）
            if (upperSql.startsWith("CREATE TABLE") || upperSql.startsWith("CREATE INDEX")) {
                logger.debug("表或索引可能已存在，跳过: {}", sql.substring(0, Math.min(50, sql.length())));
            } else {
                logger.warn("执行SQL语句失败: {} - 错误: {}", 
                           sql.substring(0, Math.min(50, sql.length())), 
                           e.getMessage());
            }
        }
    }
} 