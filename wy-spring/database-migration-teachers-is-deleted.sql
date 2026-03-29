-- 若 teachers 表已存在且缺少 is_deleted，执行本脚本一次（与 MyBatis-Plus 逻辑删除一致）
ALTER TABLE `teachers`
  ADD COLUMN `is_deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '软删除：0正常 1删除' AFTER `updated_at`;
