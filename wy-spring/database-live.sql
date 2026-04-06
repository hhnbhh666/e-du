-- 网易云课堂 - 直播模块数据库初始化脚本

USE wy_edu;

-- 直播分类表
CREATE TABLE IF NOT EXISTS `live_categories` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除',
  KEY `idx_sort` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播分类表';

-- 直播间表
CREATE TABLE IF NOT EXISTS `live_rooms` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '直播间ID',
  `anchor_id` BIGINT UNSIGNED NOT NULL COMMENT '主播用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '直播间标题',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '直播间封面图',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '直播间描述',
  `push_url` VARCHAR(500) DEFAULT NULL COMMENT '推流地址',
  `pull_url` VARCHAR(500) DEFAULT NULL COMMENT '拉流地址',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0未开播 1直播中 2已下播 3被封禁',
  `viewer_count` INT UNSIGNED DEFAULT 0 COMMENT '当前观看人数',
  `total_viewer_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '累计观看人数',
  `total_watch_duration` BIGINT UNSIGNED DEFAULT 0 COMMENT '累计观看时长（秒）',
  `like_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '点赞数',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始直播时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束直播时间',
  `category_id` INT UNSIGNED DEFAULT NULL COMMENT '分类ID',
  `product_count` INT UNSIGNED DEFAULT 0 COMMENT '商品数量',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除',
  KEY `idx_anchor` (`anchor_id`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播间表';

-- 直播弹幕表
CREATE TABLE IF NOT EXISTS `live_danmaku` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '弹幕ID',
  `room_id` BIGINT UNSIGNED NOT NULL COMMENT '直播间ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '发送者用户ID',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '发送者昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '发送者头像',
  `content` VARCHAR(100) NOT NULL COMMENT '弹幕内容',
  `color` VARCHAR(20) DEFAULT '#FFFFFF' COMMENT '弹幕颜色',
  `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '弹幕类型：1普通 2礼物弹幕 3系统消息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除',
  KEY `idx_room` (`room_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播弹幕表';

-- 直播商品表（挂载小黄车）
CREATE TABLE IF NOT EXISTS `live_products` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
  `room_id` BIGINT UNSIGNED NOT NULL COMMENT '直播间ID',
  `anchor_id` BIGINT UNSIGNED NOT NULL COMMENT '主播ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `image` VARCHAR(500) NOT NULL COMMENT '商品图片',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
  `link` VARCHAR(500) NOT NULL COMMENT '商品链接',
  `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
  `price` DECIMAL(10,2) NOT NULL COMMENT '直播价格',
  `discount` DECIMAL(3,2) DEFAULT NULL COMMENT '折扣比例',
  `stock` INT UNSIGNED DEFAULT 0 COMMENT '库存数量',
  `sold_count` INT UNSIGNED DEFAULT 0 COMMENT '已售数量',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0下架 1上架 2售罄',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除',
  KEY `idx_room` (`room_id`),
  KEY `idx_anchor` (`anchor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播商品表';

-- 直播观看记录表
CREATE TABLE IF NOT EXISTS `live_viewer_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `room_id` BIGINT UNSIGNED NOT NULL COMMENT '直播间ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `enter_time` DATETIME NOT NULL COMMENT '进入时间',
  `leave_time` DATETIME DEFAULT NULL COMMENT '离开时间',
  `watch_duration` INT UNSIGNED DEFAULT 0 COMMENT '观看时长（秒）',
  `liked` TINYINT UNSIGNED DEFAULT 0 COMMENT '是否点赞',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除',
  KEY `idx_room_user` (`room_id`, `user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播观看记录表';

-- 插入初始直播分类数据
INSERT INTO `live_categories` (`name`, `icon`, `bg_color`, `sort_order`, `status`) VALUES
('全部', '📺', '#4A90E2', 0, 1),
('才艺展示', '🎤', '#E74C3C', 1, 1),
('知识讲解', '📚', '#3498DB', 2, 1),
('游戏直播', '🎮', '#2ECC71', 3, 1),
('电商带货', '🛒', '#F39C12', 4, 1),
('户外探险', '🏔️', '#9B59B6', 5, 1),
('美食烹饪', '🍳', '#E67E22', 6, 1),
('美妆时尚', '💄', '#1ABC9C', 7, 1);