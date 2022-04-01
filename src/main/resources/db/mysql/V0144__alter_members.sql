ALTER TABLE `members` ADD `last_login_at` DATETIME DEFAULT NULL AFTER modified_at;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 144;
-- ALTER TABLE `members` DROP COLUMN `last_login_at`;
