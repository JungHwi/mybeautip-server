ALTER TABLE `video_views` MODIFY COLUMN `created_by` BIGINT NULL;
ALTER TABLE `video_views` ADD COLUMN `guest_name` VARCHAR(19) DEFAULT NULL AFTER `created_by`;
ALTER TABLE `video_views` ADD COLUMN `view_count` INT NOT NULL DEFAULT 0 AFTER `guest_name`;

ALTER TABLE `videos` DROP COLUMN `view_count_by_guest`;