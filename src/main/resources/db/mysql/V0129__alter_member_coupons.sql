ALTER TABLE `member_coupons` ADD COLUMN `expired_at` DATETIME(3) DEFAULT NULL AFTER `expiry_at`;

-- DELETE FROM flyway_schema_history WHERE version='0129';
-- ALTER TABLE `member_coupons` DROP COLUMN `expired_at`;
