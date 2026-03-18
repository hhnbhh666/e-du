-- 网易云课堂 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS wy_edu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wy_edu;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号，登录账号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码，bcrypt加密',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除：0正常 1删除',

  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 教师表
CREATE TABLE IF NOT EXISTS `teachers` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '教师ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '职称/头衔',
  `introduction` TEXT COMMENT '个人简介',
  `subjects` VARCHAR(500) DEFAULT NULL COMMENT '教授科目，JSON数组',
  `credentials` VARCHAR(500) DEFAULT NULL COMMENT '资质证书图片，JSON数组',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0待审核 1通过 2拒绝',
  `review_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 分类表
CREATE TABLE IF NOT EXISTS `categories` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标代码/类名',
  `bg_color` VARCHAR(20) DEFAULT NULL COMMENT '背景色，如#4A90E2',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序，越小越靠前',
  `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '类型：1课程分类 2题目分类',
  `parent_id` INT UNSIGNED DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

  KEY `idx_type_parent` (`type`, `parent_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 课程表
CREATE TABLE IF NOT EXISTS `courses` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
  `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '副标题/简介',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `description` TEXT COMMENT '课程详细介绍',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类ID',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '讲师ID',
  `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '现价',
  `original_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价',
  `episodes_count` INT UNSIGNED DEFAULT 0 COMMENT '总集数',
  `students_count` INT UNSIGNED DEFAULT 0 COMMENT '学员数量',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序权重',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,

  KEY `idx_category` (`category_id`),
  KEY `idx_status_sort` (`status`, `sort_order`),
  KEY `idx_price` (`price`),
  FULLTEXT KEY `ft_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 课程章节表
CREATE TABLE IF NOT EXISTS `chapters` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `course_id` BIGINT UNSIGNED NOT NULL COMMENT '所属课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频播放地址',
  `duration` INT UNSIGNED DEFAULT 0 COMMENT '视频时长（秒）',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

  KEY `idx_course` (`course_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 大学表
CREATE TABLE IF NOT EXISTS `universities` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL COMMENT '大学名称',
  `logo` VARCHAR(500) DEFAULT NULL COMMENT 'Logo图片',
  `course_count` INT UNSIGNED DEFAULT 0 COMMENT '课程数量',
  `sort_order` INT UNSIGNED DEFAULT 0,
  `status` TINYINT UNSIGNED DEFAULT 1,

  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大学表';

-- 题目表
CREATE TABLE IF NOT EXISTS `questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
  `content` TEXT NOT NULL COMMENT '题目内容',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '题目配图',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '讲解视频',
  `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '题型：1单选 2多选 3判断',
  `category_id` INT UNSIGNED NOT NULL COMMENT '所属分类',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '出题老师ID',
  `tip` TEXT COMMENT '答题技巧/口诀，支持富文本HTML',
  `explanation` LONGTEXT COMMENT '详细解析，支持富文本HTML',
  `correct_answer` TINYINT UNSIGNED NOT NULL COMMENT '正确答案索引：0=A,1=B...',
  `answer_count` INT UNSIGNED DEFAULT 0 COMMENT '被答次数统计',
  `correct_count` INT UNSIGNED DEFAULT 0 COMMENT '答对次数统计',
  `difficulty` TINYINT UNSIGNED DEFAULT 2 COMMENT '难度：1简单 2中等 3困难',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝 3已下架',
  `review_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,

  KEY `idx_category` (`category_id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_type` (`type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 题目选项表
CREATE TABLE IF NOT EXISTS `question_options` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '所属题目ID',
  `letter` VARCHAR(10) NOT NULL COMMENT '选项字母：A/B/C/D',
  `content` VARCHAR(500) NOT NULL COMMENT '选项内容',
  `sort_order` TINYINT UNSIGNED DEFAULT 0 COMMENT '排序',

  KEY `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 用户答题记录表
CREATE TABLE IF NOT EXISTS `user_answer_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `selected_option` TINYINT UNSIGNED NOT NULL COMMENT '选择的选项索引',
  `is_correct` TINYINT UNSIGNED NOT NULL COMMENT '是否正确：1正确 0错误',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',

  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_correct` (`is_correct`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录表';

-- 收藏题目表
CREATE TABLE IF NOT EXISTS `favorite_questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',

  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏题目表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
  `target_type` TINYINT UNSIGNED NOT NULL COMMENT '对象类型：1题目 2课程',
  `target_id` BIGINT UNSIGNED NOT NULL COMMENT '对象ID',
  `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父评论ID，0为顶级评论',
  `likes` INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
  `is_author` TINYINT UNSIGNED DEFAULT 0 COMMENT '是否官方/讲师回复：1是 0否',
  `status` TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：1正常 0屏蔽',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,

  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- OCR识别记录表
CREATE TABLE IF NOT EXISTS `ocr_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `teacher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '老师ID',
  `original_image` VARCHAR(500) NOT NULL COMMENT '原始上传图片URL',
  `ocr_text` LONGTEXT COMMENT 'OCR识别的原始文本',
  `parsed_questions` JSON DEFAULT NULL COMMENT '解析后的题目JSON',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0处理中 1成功 2失败',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',

  KEY `idx_user` (`user_id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OCR识别记录表';

-- 试卷表
CREATE TABLE IF NOT EXISTS `exam_papers` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL COMMENT '试卷标题',
  `teacher_id` BIGINT UNSIGNED NOT NULL COMMENT '创建老师ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '所属分类',
  `description` TEXT COMMENT '试卷描述',
  `total_questions` INT UNSIGNED DEFAULT 0 COMMENT '总题数',
  `total_score` INT UNSIGNED DEFAULT 100 COMMENT '总分',
  `time_limit` INT UNSIGNED DEFAULT 0 COMMENT '时间限制（分钟），0为不限',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
  `word_url` VARCHAR(500) DEFAULT NULL COMMENT '导出的Word文档URL',
  `pdf_url` VARCHAR(500) DEFAULT NULL COMMENT '导出的PDF文档URL',
  `status` TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0草稿 1已发布 2已下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT UNSIGNED DEFAULT 0,

  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS `paper_questions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `paper_id` BIGINT UNSIGNED NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  `score` INT UNSIGNED DEFAULT 1 COMMENT '该题分值',
  `sort_order` INT UNSIGNED DEFAULT 0 COMMENT '排序',

  UNIQUE KEY `uk_paper_question` (`paper_id`, `question_id`),
  KEY `idx_paper` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 插入初始分类数据（题目分类 type=2：中小学学科）
INSERT INTO `categories` (`name`, `icon`, `bg_color`, `sort_order`, `type`, `parent_id`, `status`) VALUES
('全部分类', '⊞', '#4A90E2', 0, 2, 0, 1),
('小学语文', '文', '#E74C3C', 1, 2, 0, 1),
('小学数学', '🔢', '#3498DB', 2, 2, 0, 1),
('小学英语', 'A', '#2ECC71', 3, 2, 0, 1),
('初中语文', '书', '#F39C12', 4, 2, 0, 1),
('初中数学', '∑', '#9B59B6', 5, 2, 0, 1),
('初中英语', 'En', '#1ABC9C', 6, 2, 0, 1),
('初中物理', '⚗', '#E67E22', 7, 2, 0, 1);
