-- 测试数据：在 database.sql 建表并插入初始分类后执行本脚本
-- 用于前端展示：课程分类、题目与选项。建议只执行一次。

USE wy_edu;

-- ========== 1. 课程分类（type=1，首页「可滑动分类」用） ==========
INSERT INTO `categories` (`name`, `icon`, `bg_color`, `sort_order`, `type`, `parent_id`, `status`) VALUES
('精品课', '▶', '#00d26a', 10, 1, 0, 1),
('播客听书', '◉', '#00c8a0', 11, 1, 0, 1),
('考试考证', '📋', '#5A7FFF', 12, 1, 0, 1),
('职场', '💼', '#4A90E2', 13, 1, 0, 1),
('心理', '♥', '#5A7FFF', 14, 1, 0, 1),
('外语学习', 'A', '#00c8a0', 15, 1, 0, 1),
('理工学科', '⚗', '#00d26a', 16, 1, 0, 1),
('语言文学', '文', '#4A90E2', 17, 1, 0, 1);

-- ========== 2. 默认用户与教师（匿名提交/题目归属用） ==========
-- 密码为 123456 的 bcrypt 哈希
INSERT INTO `users` (`id`, `phone`, `password`, `nickname`, `status`, `is_deleted`) VALUES
(1, '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 1, 0)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

INSERT INTO `teachers` (`id`, `user_id`, `name`, `title`, `introduction`, `status`) VALUES
(1, 1, '张老师', '高级讲师', '中小学学科辅导，多年一线教学经验', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ========== 3. 题目（小学数学 category_id=2，需先有 database.sql 中的题目分类） ==========
INSERT INTO `questions` (
  `content`, `image_url`, `video_url`, `type`, `category_id`, `teacher_id`,
  `tip`, `explanation`, `correct_answer`, `answer_count`, `correct_count`, `difficulty`, `status`, `is_deleted`
) VALUES
('小明有 12 个苹果，吃了 3 个，又买来 5 个。现在小明一共有几个苹果？', NULL, NULL, 1, 3, 1,
 '先减后加，注意顺序。', '12 - 3 = 9（个），9 + 5 = 14（个）。现在一共有 14 个苹果。', 2, 0, 0, 1, 1, 0),
('下列哪个数是 24 的因数？', NULL, NULL, 1, 3, 1,
 '24 = 1×24 = 2×12 = 3×8 = 4×6。', '24 的因数有 1、2、3、4、6、8、12、24，因此 8 是 24 的因数。', 1, 0, 0, 2, 1, 0),
('一个长方形的长是 8 厘米，宽是 5 厘米，它的面积是多少平方厘米？', NULL, NULL, 1, 3, 1,
 '长方形面积 = 长 × 宽。', '面积 = 8 × 5 = 40（平方厘米）。', 0, 0, 0, 1, 1, 0),
('“碧玉妆成一树高”的下一句是？', NULL, NULL, 1, 2, 1,
 '贺知章《咏柳》。', '出自贺知章《咏柳》：碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。', 0, 0, 0, 1, 1, 0),
('下列词语中，书写完全正确的一组是（　）。', NULL, NULL, 1, 2, 1,
 '注意同音字、形近字辨析。', '“穿流不息”应为“川流不息”；“金壁辉煌”应为“金碧辉煌”；“再接再励”应为“再接再厉”。只有 A 组全部正确。', 0, 0, 0, 2, 1, 0);

-- 插入后 5 题的 id 为 LAST_INSERT_ID() 到 LAST_INSERT_ID()+4
SET @q1 = LAST_INSERT_ID();
SET @q2 = @q1 + 1;
SET @q3 = @q1 + 2;
SET @q4 = @q1 + 3;
SET @q5 = @q1 + 4;

-- ========== 4. 题目选项 ==========
INSERT INTO `question_options` (`question_id`, `letter`, `content`, `sort_order`) VALUES
(@q1, 'A', '10 个', 0), (@q1, 'B', '13 个', 1), (@q1, 'C', '14 个', 2), (@q1, 'D', '15 个', 3),
(@q2, 'A', '5', 0), (@q2, 'B', '8', 1), (@q2, 'C', '9', 2), (@q2, 'D', '10', 3),
(@q3, 'A', '40', 0), (@q3, 'B', '26', 1), (@q3, 'C', '13', 2), (@q3, 'D', '35', 3),
(@q4, 'A', '万条垂下绿丝绦', 0), (@q4, 'B', '二月春风似剪刀', 1), (@q4, 'C', '不知细叶谁裁出', 2), (@q4, 'D', '春眠不觉晓', 3),
(@q5, 'A', '川流不息、金碧辉煌、再接再厉', 0), (@q5, 'B', '穿流不息、金壁辉煌、再接再励', 1), (@q5, 'C', '川流不息、金壁辉煌、再接再厉', 2), (@q5, 'D', '川流不息、金碧辉煌、再接再励', 3);
