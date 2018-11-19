ALTER TABLE `members` ADD COLUMN `total_video_count` INT UNSIGNED NOT NULL DEFAULT 0 AFTER `video_count`;
ALTER TABLE `members` ADD COLUMN `visible` TINYINT DEFAULT 0 AFTER `id`;