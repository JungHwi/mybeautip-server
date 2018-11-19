ALTER TABLE `video_watches` ADD COLUMN `username` VARCHAR(50) DEFAULT NULL after `video_id`;
ALTER TABLE `video_watches` ADD COLUMN `is_guest` TINYINT(1) NOT NULL after `username`;
ALTER TABLE `video_watches` MODIFY COLUMN `created_by` BIGINT DEFAULT NULL;