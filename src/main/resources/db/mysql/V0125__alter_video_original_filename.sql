ALTER TABLE `videos` ADD COLUMN `original_filename` VARCHAR(10) DEFAULT NULL AFTER `url`;

-- DELETE FROM flyway_schema_history WHERE version='0125';
-- ALTER TABLE `videos` DROP COLUMN `original_filename`;

