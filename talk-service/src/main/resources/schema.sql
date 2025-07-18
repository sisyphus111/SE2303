-- 聊天服务数据库初始化脚本

-- 创建messages表（如果不存在）
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL COMMENT '发送者用户ID',
    content TEXT COMMENT '消息内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    valid BOOLEAN NOT NULL DEFAULT TRUE COMMENT '消息是否有效',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '消息是否已删除',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '消息是否已读',
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_valid (valid),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

