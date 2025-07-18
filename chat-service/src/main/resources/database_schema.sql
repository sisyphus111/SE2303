-- Chat Service 数据库表结构
-- 数据库名: chat_service

-- 创建数据库
CREATE DATABASE IF NOT EXISTS chat_service
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE chat_service;

DROP TABLE IF EXISTS messages;

-- 创建messages表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    valid BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否有效',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_valid (valid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表'; 