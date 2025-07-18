-- 数据库初始化脚本
-- 用于 user-service 微服务

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS mental_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mental_user;

-- 创建认证表
CREATE TABLE IF NOT EXISTS `auth` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '姓名',
    `jaccount` VARCHAR(255) NOT NULL UNIQUE COMMENT 'jaccount账号',
    `sjtu_id` VARCHAR(255) NOT NULL UNIQUE COMMENT 'SJTU学工号',
    `is_user` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否为普通用户',
    `valid` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否被启用',
    `telephone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_jaccount` (`jaccount`),
    INDEX `idx_sjtu_id` (`sjtu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证信息表';



-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(100) COMMENT '昵称',
    `avatar` VARCHAR(500) NOT NULL COMMENT '头像URL',
    `gender` VARCHAR(10) COMMENT '性别：男、女、保密',
    `birthday` DATE COMMENT '生日',
    `location` VARCHAR(200) COMMENT '地区',
    `bio` VARCHAR(500) COMMENT '个人简介',
    `tags` VARCHAR(1000) COMMENT '用户标签（JSON格式）',
    `profile_visibility` VARCHAR(20) DEFAULT 'public' COMMENT '个人资料可见性：public/private',
    `registration_date` DATE COMMENT '注册时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`id`) REFERENCES `auth`(`id`) ON DELETE CASCADE,
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';


-- 创建用户偏好设置表
CREATE TABLE IF NOT EXISTS `user_preferences` (
    `id` BIGINT PRIMARY KEY COMMENT '用户ID，与user表的id一致',
    -- 通知设置 - 通知方式
    `push_notifications` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '推送通知',
    `email_notifications` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '邮件通知',
    -- 通知设置 - 通知类型
    `comment_notifications` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '评论通知',
    `like_notifications` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '点赞通知',
    `system_notifications` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '系统通知',
    -- 个人偏好 - 媒体设置
    `sound_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '声音开启',
    -- 个人偏好 - 社区交互
    `show_post_stats` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '显示帖子统计',
    `enable_post_recommendation` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '启用帖子推荐',
    `block_sensitive_content` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '屏蔽敏感内容',
    -- 心理健康设置
    `crisis_intervention_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '危机干预启用',
    `mood_tracking_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '情绪追踪启用',
    `anonymous_posting` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '匿名发帖',
    `mental_health_data_sharing` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '心理健康数据共享',
    -- 时间戳
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`id`) REFERENCES `auth`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好设置表';

-- 清空表数据（如果表存在）
-- 注意：由于外键约束，需要按依赖关系的逆序删除
SET FOREIGN_KEY_CHECKS = 0; -- 临时禁用外键检查

-- 使用TRUNCATE更彻底地清空表（比DELETE更快，会重置自增ID）
SELECT 'Debug: About to create admin user' as debug_message;
TRUNCATE TABLE user_preferences;
TRUNCATE TABLE user;
TRUNCATE TABLE auth;


SET FOREIGN_KEY_CHECKS = 1; -- 重新启用外键检查

-- 创建索引以优化查询性能
CREATE INDEX `idx_user_registration_date` ON `user`(`registration_date`);
CREATE INDEX `idx_user_preferences_notifications` ON `user_preferences`(`push_notifications`, `email_notifications`);

COMMIT; 