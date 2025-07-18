-- Mood Service 数据库表结构
-- 数据库名: mood_service

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mood_service
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE mood_service;

DROP TABLE IF EXISTS mood_item;
DROP TABLE IF EXISTS mood;

-- 创建mood表
CREATE TABLE IF NOT EXISTS mood (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    score INT CHECK (score >= 1 AND score <= 10),
    score_committed TINYINT(1) DEFAULT 0,
    diary_title VARCHAR(255),
    diary_description TEXT,
    diary_committed TINYINT(1) DEFAULT 0,
    weather VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_date (date),
    INDEX idx_user_date (user_id, date)
) COMMENT '用户心情记录表';

-- 创建mood_item表
CREATE TABLE IF NOT EXISTS mood_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(50) NOT NULL,
    mood_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT mood_item_ibfk_1 FOREIGN KEY (mood_id) REFERENCES mood (id) ON DELETE CASCADE,
    INDEX idx_label (label),
    INDEX idx_mood_id (mood_id)
) COMMENT '心情标签表'; 

CREATE TABLE IF NOT EXISTS mood_analysis(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL,
    analysis TEXT NOT NULL,
    valid BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '心情分析表';