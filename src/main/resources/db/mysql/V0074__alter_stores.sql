ALTER TABLE `stores` ADD COLUMN `goods_count` INT DEFAULT 0 AFTER `like_count`;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 74;
-- ALTER TABLE `stores` ADD COLUMN `goods_count`;