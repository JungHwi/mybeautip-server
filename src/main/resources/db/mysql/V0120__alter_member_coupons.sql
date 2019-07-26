ALTER TABLE `member_coupons` ADD COLUMN `expiry_at` DATETIME(3) DEFAULT NULL AFTER `created_at`;

UPDATE `member_coupons` SET `expiry_at` = `created_at` + INTERVAL 7 DAY;

--DELETE FROM flyway_schema_history WHERE version='0120';
--ALTER TABLE `member_coupons` DROP COLUMN `expiry_at`;