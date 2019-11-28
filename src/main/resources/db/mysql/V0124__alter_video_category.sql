ALTER TABLE `videos` ADD COLUMN `category` TINYINT(1) DEFAULT 0 AFTER `visibility`;

--DELETE FROM flyway_schema_history WHERE version='0124';
--ALTER TABLE `videos` DROP COLUMN `category`;
