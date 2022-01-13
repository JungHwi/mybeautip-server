ALTER TABLE `videos` ADD `started_at` DATETIME DEFAULT NULL AFTER owner;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 142;
-- ALTER TABLE `videos` DROP COLUMN `started_at`;
