ALTER TABLE `members` ADD COLUMN `permission` INT UNSIGNED DEFAULT 0 AFTER `link`;
ALTER TABLE `videos` ADD COLUMN `report_count` BIGINT UNSIGNED DEFAULT 0 AFTER `order_count`;
ALTER TABLE `video_reports` ADD COLUMN `reason_code` TINYINT UNSIGNED DEFAULT 0 AFTER `created_by`;
ALTER TABLE `member_reports` ADD COLUMN `reason_code` TINYINT UNSIGNED DEFAULT 0 AFTER `you`;
ALTER TABLE `member_reports` ADD COLUMN `video_id` BIGINT DEFAULT NULL AFTER `reason`;
ALTER TABLE `member_reports` ADD CONSTRAINT `fk_member_reports_videos` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`);


--DELETE FROM `flyway_schema_history` WHERE version='0093';
--ALTER TABLE `members` DROP COLUMN `permission`;
--ALTER TABLE `videos` DROP COLUMN `report_count`;
--ALTER TABLE `video_reports` DROP COLUMN `reason_code`;
--ALTER TABLE `member_reports` DROP COLUMN `reason_code`;
--ALTER TABLE `member_reports` DROP COLUMN `video_id`;
--ALTER TABLE `member_reports` DROP FOREIGN KEY `fk_member_reports_videos`;
