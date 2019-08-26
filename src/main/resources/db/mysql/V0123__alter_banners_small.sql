ALTER TABLE `banners` ADD COLUMN `slim_thumbnail_url` VARCHAR(255) DEFAULT NULL AFTER `thumbnail_url`;

--DELETE FROM flyway_schema_history WHERE version='0123';
--ALTER TABLE `banners` DROP COLUMN `slim_thumbnail_url`;
