ALTER TABLE `goods` MODIFY COLUMN `delivery_sno` INT(10) NOT NULL DEFAULT 0;
ALTER TABLE `goods` ADD COLUMN `delivery_method` VARCHAR(40) NOT NULL DEFAULT '' AFTER `delivery_sno`;
ALTER TABLE `goods` ADD COLUMN `delivery_fix_fl` VARCHAR(6) NOT NULL DEFAULT '' AFTER `delivery_sno`;
ALTER TABLE `goods` ADD COLUMN `deleted_at` DATETIME(3) AFTER `modified_at`;

ALTER TABLE `coupons` ADD COLUMN `condition_price_min` INT UNSIGNED DEFAULT NULL AFTER `condition_price`;
ALTER TABLE `coupons` ADD COLUMN `condition_price_max` INT UNSIGNED DEFAULT NULL AFTER `condition_price`;
