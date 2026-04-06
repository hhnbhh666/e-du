-- 插入测试直播间（ID 为 testroom123 对应的直播间）
USE wy_edu;

-- 先插入一个用户和教师作为主播
INSERT INTO `users` (`id`, `phone`, `password`, `nickname`, `avatar`, `status`, `is_deleted`) VALUES
(100, '13800138100', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试主播', 'https://picsum.photos/200/200?random=99', 1, 0)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

INSERT INTO `teachers` (`id`, `user_id`, `name`, `title`, `introduction`, `avatar`, `status`) VALUES
(100, 100, '测试主播', '主播', '热爱直播的主播', 'https://picsum.photos/200/200?random=99', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入直播间，手动设置 ID=100，这样我们知道确切的 ID
-- 注意：我们的推流码是 testroom123，但直播间在数据库里是 ID 自增的
-- 所以我们创建一个直播间，然后记住它的 ID
INSERT INTO `live_rooms` (`id`, `anchor_id`, `title`, `cover_image`, `description`, `push_url`, `pull_url`, `status`, `viewer_count`, `total_viewer_count`, `total_watch_duration`, `like_count`, `start_time`, `category_id`, `product_count`, `created_at`, `updated_at`, `is_deleted`) VALUES
(100, 100, '我的测试直播', 'https://picsum.photos/400/300?random=99', '这是一个测试直播间', 'rtmp://localhost/live/testroom123', 'http://localhost:8081/live/testroom123.flv', 1, 10, 100, 3600, 50, NOW(), 1, 0, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`), `title` = VALUES(`title`);
