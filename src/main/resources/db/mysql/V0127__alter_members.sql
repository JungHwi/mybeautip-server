ALTER TABLE `members` MODIFY COLUMN `avatar_url` VARCHAR(500) DEFAULT NULL;

-- DELETE FROM flyway_schema_history WHERE version='0127';
-- ALTER TABLE `members` MODIFY COLUMN `avatar_url` VARCHAR(200) DEFAULT NULL;

