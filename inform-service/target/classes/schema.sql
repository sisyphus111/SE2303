-- 创建通知表（优化版）
CREATE TABLE IF NOT EXISTS informs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收通知的用户ID',
    type ENUM('COMMENT', 'LIKE') NOT NULL COMMENT '通知类型：评论或点赞',
    content TEXT COMMENT '通知内容：评论时存储评论内容，点赞时为空',
    related_id VARCHAR(255) COMMENT '相关ID：帖子ID等',
    from_user_id BIGINT COMMENT '发起操作的用户ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_type (type)
);

-- 插入测试数据（优化版）
INSERT INTO informs (user_id, type, content, related_id, from_user_id, is_read, created_at) VALUES
(1, 'COMMENT', '这个减压方法真的很有用，谢谢分享！', '123', 2, FALSE, NOW()),
(1, 'LIKE', '', '123', 3, FALSE, NOW()),
(1, 'COMMENT', '我也有同样的感受，很有共鸣', '124', 4, FALSE, NOW()),
(2, 'LIKE', '', '125', 1, TRUE, NOW()),
(2, 'COMMENT', '非常实用的建议，已经开始尝试了', '126', 5, FALSE, NOW());
