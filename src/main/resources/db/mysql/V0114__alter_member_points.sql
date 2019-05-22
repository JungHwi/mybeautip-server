ALTER TABLE `member_points` ADD COLUMN `expiry_at` DATETIME(3) DEFAULT NULL AFTER `earned_at`;


--DELETE FROM flyway_schema_history WHERE version='0114';
--ALTER TABLE `member_points` DROP COLUMN `expiry_at`;