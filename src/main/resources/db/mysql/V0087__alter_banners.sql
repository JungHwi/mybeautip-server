ALTER TABLE `banners` ADD COLUMN `post_id` BIGINT NOT NULL AFTER `link`;

UPDATE `banners` SET post_id = SUBSTRING(link, 14);

-- DELETE FROM flyway_schema_history WHERE installed_rank = 86;
-- ALTER TABLE `banners` DROP COLUMN `post_id`;