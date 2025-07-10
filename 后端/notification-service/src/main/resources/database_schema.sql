-- Notification Service 数据库表结构
-- 数据库名: notification_service

-- 创建数据库
CREATE DATABASE IF NOT EXISTS notification_service
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE notification_service;

DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS musics;
DROP TABLE IF EXISTS carousel_images;

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    introduction TEXT COMMENT '文章简介',
    description TEXT COMMENT '文章描述',
    cover VARCHAR(500) COMMENT '封面图片URL',
    link VARCHAR(500) COMMENT '文章链接',
    category VARCHAR(100) DEFAULT 'mental-health' COMMENT '文章分类',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 音乐表
CREATE TABLE IF NOT EXISTS musics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '音乐标题',
    category VARCHAR(255) COMMENT '音乐分类',
    cover VARCHAR(500) COMMENT '封面图片URL',
    url VARCHAR(500) NOT NULL COMMENT '音乐文件URL',
    duration INT DEFAULT 0 COMMENT '时长（秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 轮播图表
CREATE TABLE IF NOT EXISTS carousel_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    title VARCHAR(255) COMMENT '标题',
    description TEXT COMMENT '描述',
    category VARCHAR(100) DEFAULT 'resource' COMMENT '分类',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 插入示例数据
INSERT INTO articles (title, introduction, description, cover, link, category) VALUES
('如何保持良好的心理健康', '分享一些日常保持心理健康的小技巧，包括规律作息、适度运动、保持社交联系等实用方法。', '心理健康是我们整体健康的重要组成部分。保持良好的心理健康需要我们在日常生活中养成一些健康的习惯。首先，保持规律的作息时间，充足的睡眠对心理健康至关重要。其次，适度的运动不仅能改善身体状况，还能释放内啡肽，提升心情。保持积极的社交联系也很重要，与家人朋友的交流能够提供情感支持。此外，学会管理压力，通过深呼吸、冥想等放松技巧来缓解紧张情绪。最后，培养兴趣爱好，给生活增添乐趣和意义。', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80', '#', 'mental-health'),
('压力管理的有效方法', '学会管理生活和工作中的压力，通过时间管理、放松技巧和心态调整来应对挑战。', '现代生活中，压力无处不在，学会有效管理压力是维护心理健康的关键。首先要识别压力源，了解什么情况下会感到压力。时间管理是减压的有效方法，合理安排时间，避免任务堆积。学会说''不''，不要过度承担责任。建立支持网络，在需要时寻求帮助。掌握放松技巧如深呼吸、渐进性肌肉放松等。保持积极心态，将挑战视为成长机会。定期进行体育锻炼，这是天然的压力释放方式。最重要的是，给自己留出休息和娱乐的时间。', 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80', '#', 'mental-health'),
('情绪调节的艺术', '学习如何识别、理解和调节自己的情绪，建立健康的情绪管理机制。', '冥想是一种古老而有效的心理健康练习方法。正念冥想能帮助我们专注当下，减少焦虑和抑郁情绪。开始时，每天只需10-15分钟的练习。找一个安静的地方坐下，闭上眼睛，专注于呼吸。当思绪飘散时，温和地将注意力带回呼吸上。身体扫描是另一种有效技巧，从头到脚依次关注身体各部位的感觉。渐进性肌肉放松法通过紧张和放松不同肌肉群来释放压力。引导冥想音频对初学者很有帮助。坚持练习，你会发现心情更平静，注意力更集中。', 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=400&q=80', '#', 'mental-health'),
('睡眠质量改善指南', '提供改善睡眠质量的方法和技巧，帮助你获得更好的休息。', '睡眠与心理健康密切相关，睡眠不足会影响情绪调节和认知功能。建立规律的睡眠习惯非常重要。每天在同一时间上床睡觉和起床，包括周末。睡前1-2小时避免使用电子设备，蓝光会干扰褪黑素分泌。创造良好的睡眠环境：保持房间凉爽、安静、黑暗。睡前进行放松活动，如阅读、听轻音乐或泡温水澡。避免睡前摄入咖啡因和大量液体。如果躺在床上20分钟仍无法入睡，起床做些安静的活动直到困倦再回到床上。保持良好的睡眠卫生习惯，你的心理健康会得到显著改善。', 'https://images.unsplash.com/photo-1541781774459-bb2a2a0b2f65?auto=format&fit=crop&w=400&q=80', '#', 'mental-health');

INSERT INTO musics (title, category, cover, url, duration) VALUES
('森林白噪音', 'Nature Sounds', 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=120&q=80', 'example.invalid/birdsing.mp3', 300),
('舒缓钢琴', 'Piano Music', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80', 'example.invalid/piano.mp3', 240),
('冥想音乐', 'Meditation Sounds', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=120&q=80', 'example.invalid/meditation.mp3', 600),
('雨声白噪音', 'Rain Sounds', 'https://images.unsplash.com/photo-1518837695005-2083093ee35b?auto=format&fit=crop&w=120&q=80', 'example.invalid/rain.mp3', 480);

INSERT INTO carousel_images (image_url, title, description, category, sort_order) VALUES
('https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80', '心理健康小贴士', '保持积极心态，关注心理健康', 'resource', 1),
('https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=800&q=80', '压力管理', '学会管理压力，保持平衡', 'resource', 2),
('https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=800&q=80', '情绪调节', '掌握情绪调节技巧', 'resource', 3); 