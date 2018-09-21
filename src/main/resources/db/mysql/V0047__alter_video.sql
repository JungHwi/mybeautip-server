ALTER TABLE `videos` ADD COLUMN `total_watch_count` INT NOT NULL DEFAULT 0 after `watch_count`;
ALTER TABLE `videos` ADD COLUMN `order_count` INT NOT NULL DEFAULT 0 after `like_count`;



-- Alter DATETIME to DATETIME(3)
ALTER TABLE `order_deliveries` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `order_payments` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `order_payments` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `order_payments` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `order_purchases` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `order_purchases` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `coupons` modify COLUMN `started_at` DATETIME(3) NOT NULL;
ALTER TABLE `coupons` modify COLUMN `ended_at` DATETIME(3) NOT NULL;
ALTER TABLE `coupons` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `coupons` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `coupons` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `member_coupons` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `member_coupons` modify COLUMN `used_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `member_points` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `member_points` modify COLUMN `earned_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `member_points` modify COLUMN `expired_at` DATETIME(3) DEFAULT NULL;