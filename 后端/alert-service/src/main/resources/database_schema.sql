-- Alert Service 数据库表结构
-- 数据库名: alert_service

-- 创建数据库
CREATE DATABASE IF NOT EXISTS alert_service
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE alert_service;

DROP TABLE IF EXISTS alert;

-- 警报表
CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '警报ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',

    alert_type ENUM('DANGEROUS_OPERATION', 'AI_TIMEOUT', 'EMOTIONAL_CRISIS', 'SELF_HARM', 'VIOLENT_TENDENCY', 'OTHER') NOT NULL COMMENT '警报类型',
    alert_level ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM' COMMENT '警报级别',
    status ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '警报状态',
    description TEXT COMMENT '警报描述',
    operation_details TEXT COMMENT '操作详情',
    ai_session_id VARCHAR(100) COMMENT 'AI会话ID',
    ai_timeout_duration INT COMMENT 'AI超时时长（分钟）',
    assigned_counselor_id VARCHAR(50) COMMENT '分配的心理咨询师ID',

    follow_up_notes TEXT COMMENT '回访备注',
    follow_up_date DATETIME COMMENT '回访日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_alert_type (alert_type),
    INDEX idx_assigned_counselor_id (assigned_counselor_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_counselor_status (assigned_counselor_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='警报表';

-- 插入示例数据
INSERT INTO alert (user_id, alert_type, alert_level, description, operation_details) VALUES
('1', 'DANGEROUS_OPERATION', 'HIGH', '用户进行了危险操作', '用户尝试访问危险网站'),
('2', 'AI_TIMEOUT', 'MEDIUM', 'AI会话超时', '用户使用AI服务超过30分钟'),
('3', 'EMOTIONAL_CRISIS', 'URGENT', '检测到情绪异常', '用户情绪状态不稳定'),
('4', 'SELF_HARM', 'URGENT', '检测到自残倾向', '用户表达了自残想法'),
('5', 'VIOLENT_TENDENCY', 'HIGH', '检测到暴力倾向', '用户表达了暴力想法');
