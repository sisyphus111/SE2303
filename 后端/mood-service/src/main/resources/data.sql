-- 数据库初始化脚本
-- 这个脚本会在应用启动时自动执行

-- 插入一些测试数据
INSERT INTO mood (date, user_id, score, score_committed, diary_description, diary_committed) 
VALUES ('2024-01-15', 1, 4, true, '今天心情很好，工作顺利', true)
ON DUPLICATE KEY UPDATE score = VALUES(score);

INSERT INTO mood_item (label, mood_id) 
VALUES ('开心', 1), ('满足', 1), ('充满活力', 1)
ON DUPLICATE KEY UPDATE label = VALUES(label); 