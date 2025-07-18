-- 数据库初始化脚本
-- 用于创建WebSocket监控功能所需的数据库和表

-- 创建bot-service数据库
CREATE DATABASE IF NOT EXISTS bot_service 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建alert-service数据库
CREATE DATABASE IF NOT EXISTS alert_service 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用bot_service数据库
USE bot_service;

-- 创建WebSocket会话记录表
CREATE TABLE IF NOT EXISTS websocket_session_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    connection_start_time DATETIME NOT NULL,
    connection_end_time DATETIME,
    duration_minutes BIGINT,
    is_long_session BOOLEAN DEFAULT FALSE,
    alert_sent BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    INDEX idx_session_id (session_id),
    INDEX idx_connection_start_time (connection_start_time),
    INDEX idx_is_long_session (is_long_session)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 使用alert_service数据库
USE alert_service;

-- 创建警报表
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(255) NOT NULL,
    service_name VARCHAR(255) NOT NULL,
    session_id VARCHAR(255),
    description TEXT,
    duration_minutes BIGINT,
    connection_start_time DATETIME,
    connection_end_time DATETIME,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    status ENUM('NEW', 'ACKNOWLEDGED', 'RESOLVED', 'CLOSED') DEFAULT 'NEW',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    acknowledged_at DATETIME,
    acknowledged_by VARCHAR(255),
    INDEX idx_alert_type (alert_type),
    INDEX idx_service_name (service_name),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入一些测试数据（可选）
USE alert_service;

-- 插入测试警报数据
INSERT INTO alerts (
    alert_type, 
    service_name, 
    session_id, 
    description, 
    duration_minutes, 
    connection_start_time, 
    connection_end_time, 
    severity, 
    status, 
    created_at
) VALUES 
(
    'LONG_WEBSOCKET_SESSION',
    'bot-service',
    'test-session-001',
    '测试长时间会话警报 - 会话持续了55分钟',
    55,
    NOW() - INTERVAL 55 MINUTE,
    NOW(),
    'MEDIUM',
    'NEW',
    NOW()
),
(
    'LONG_WEBSOCKET_SESSION',
    'bot-service',
    'test-session-002',
    '测试长时间会话警报 - 会话持续了120分钟',
    120,
    NOW() - INTERVAL 120 MINUTE,
    NOW(),
    'HIGH',
    'ACKNOWLEDGED',
    NOW() - INTERVAL 1 HOUR
);

-- 显示创建的表
SELECT 'bot_service.websocket_session_records' as table_name, COUNT(*) as record_count 
FROM bot_service.websocket_session_records
UNION ALL
SELECT 'alert_service.alerts' as table_name, COUNT(*) as record_count 
FROM alert_service.alerts;

-- 显示数据库信息
SELECT 
    'bot_service' as database_name,
    COUNT(*) as table_count
FROM information_schema.tables 
WHERE table_schema = 'bot_service'
UNION ALL
SELECT 
    'alert_service' as database_name,
    COUNT(*) as table_count
FROM information_schema.tables 
WHERE table_schema = 'alert_service'; 