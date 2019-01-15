ALTER TABLE `posts` ADD COLUMN `opened` TINYINT(1) DEFAULT 0 AFTER `category`;

ALTER TABLE `posts` ADD COLUMN `started_at` DATETIME DEFAULT NULL AFTER `opened`;

ALTER TABLE `posts` ADD COLUMN `ended_at` DATETIME DEFAULT NULL AFTER `started_at`;

UPDATE `posts` SET opened = 1, started_at = now(), ended_at = '2019-02-28 23:59:59';


-- DELETE FROM flyway_schema_history WHERE installed_rank = 88;
-- ALTER TABLE `posts` DROP COLUMN `opened`;
-- ALTER TABLE `posts` DROP COLUMN `started_at`;
-- ALTER TABLE `posts` DROP COLUMN `ended_at`;

