ALTER TABLE `member_points` ADD COLUMN `remind` TINYINT(1) DEFAULT 0 AFTER `earned_at`;


--DELETE FROM flyway_schema_history WHERE version='0114';
--ALTER TABLE `member_points` DROP COLUMN `remind`;