--
-- Videos
--
ALTER TABLE `comments` ADD `state` TINYINT DEFAULT 0 AFTER like_count;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 140;
-- ALTER TABLE `comments` DROP COLUMN `state`;

