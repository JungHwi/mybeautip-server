ALTER TABLE `view_recodings` ADD COLUMN `view_count` INT DEFAULT 0 AFTER `created_by`;
ALTER TABLE `view_recodings` ADD COLUMN `modified_at` DATETIME(3) DEFAULT NULL AFTER `created_at`;


--ALTER TABLE `view_recodings` DROP COLUMN `view_count`;
--ALTER TABLE `view_recodings` DROP COLUMN `modified_at`;