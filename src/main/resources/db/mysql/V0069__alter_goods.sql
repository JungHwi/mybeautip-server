-- Remove unused columns
ALTER TABLE `goods` DROP COLUMN `reg_dt`;
ALTER TABLE `goods` DROP COLUMN `mod_dt`;

-- Modify column property
ALTER TABLE `goods` MODIFY COLUMN `all_cd` VARCHAR(50) DEFAULT NULL;

-- Add new features
ALTER TABLE `goods` ADD COLUMN `goods_icon_cd` VARCHAR(255) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_icon_cd_period` VARCHAR(255) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_icon_end_ymd` VARCHAR(20) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_icon_start_ymd` VARCHAR(20) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `only_adult_fl` CHAR(1) NOT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_open_dt` VARCHAR(20) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_sell_fl` VARCHAR(255) DEFAULT NULL AFTER `goods_search_word`;
ALTER TABLE `goods` ADD COLUMN `goods_display_fl` VARCHAR(255) DEFAULT NULL AFTER `goods_search_word`;


-- Remove unused columns
ALTER TABLE `goods_options` DROP COLUMN `created_at`;
ALTER TABLE `goods_options` DROP COLUMN `modified_at`;

ALTER TABLE `delivery_charge` DROP COLUMN `charge_data`;

