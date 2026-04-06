-- 直播模块测试数据
USE wy_edu;

-- 插入一些测试教师作为主播
INSERT INTO `teachers` (`id`, `user_id`, `name`, `title`, `introduction`, `avatar`, `status`) VALUES
(2, 2, '李老师', '金牌讲师', '专注电商直播带货，分享实用销售技巧', 'https://picsum.photos/200/200?random=10', 1),
(3, 3, '王老师', '游戏达人', '热门游戏主播，每天精彩直播', 'https://picsum.photos/200/200?random=11', 1),
(4, 4, '张老师', '知识博主', '分享有趣的知识和见解', 'https://picsum.photos/200/200?random=12', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入一些测试用户
INSERT INTO `users` (`id`, `phone`, `password`, `nickname`, `avatar`, `status`, `is_deleted`) VALUES
(2, '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李主播', 'https://picsum.photos/200/200?random=20', 1, 0),
(3, '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王游戏', 'https://picsum.photos/200/200?random=21', 1, 0),
(4, '13800138003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张知识', 'https://picsum.photos/200/200?random=22', 1, 0)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- 插入正在直播的房间（status=1）
INSERT INTO `live_rooms` (`anchor_id`, `title`, `cover_image`, `description`, `push_url`, `pull_url`, `status`, `viewer_count`, `total_viewer_count`, `total_watch_duration`, `like_count`, `start_time`, `category_id`, `product_count`, `created_at`, `updated_at`, `is_deleted`) VALUES
(2, '618好物推荐直播间', 'https://picsum.photos/400/300?random=1', '今天给大家带来超值好物！', 'rtmp://live.example.com/push/room1', 'https://pull.example.com/live/room1.flv', 1, 128, 520, 3600, 856, NOW(), 5, 3, NOW(), NOW(), 0),
(3, '王者荣耀巅峰赛', 'https://picsum.photos/400/300?random=2', '冲击王者段位！', 'rtmp://live.example.com/push/room2', 'https://pull.example.com/live/room2.flv', 1, 256, 1230, 7200, 2341, NOW(), 4, 0, NOW(), NOW(), 0),
(4, '有趣的历史小知识', 'https://picsum.photos/400/300?random=3', '每天学点历史知识', 'rtmp://live.example.com/push/room3', 'https://pull.example.com/live/room3.flv', 1, 89, 340, 1800, 567, NOW(), 3, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `status` = VALUES(`status`);

-- 插入直播商品
INSERT INTO `live_products` (`room_id`, `anchor_id`, `name`, `image`, `description`, `link`, `original_price`, `price`, `stock`, `sort_order`, `status`, `created_at`, `updated_at`, `is_deleted`) VALUES
(1, 2, '精选茶叶礼盒', 'https://picsum.photos/200/200?random=100', '高品质茶叶，送礼自用两相宜', 'https://example.com/product/1', 299.00, 199.00, 50, 1, 1, NOW(), NOW(), 0),
(1, 2, '智能保温杯', 'https://picsum.photos/200/200?random=101', '智能温控，随时随地喝热水', 'https://example.com/product/2', 199.00, 99.00, 100, 2, 1, NOW(), NOW(), 0),
(1, 2, '无线蓝牙耳机', 'https://picsum.photos/200/200?random=102', '高清音质，超长续航', 'https://example.com/product/3', 399.00, 259.00, 30, 3, 1, NOW(), NOW(), 0),
(3, 4, '历史知识书籍', 'https://picsum.photos/200/200?random=103', '通俗易懂的历史读物', 'https://example.com/product/4', 89.00, 59.00, 200, 1, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
