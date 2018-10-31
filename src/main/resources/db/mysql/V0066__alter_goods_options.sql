ALTER TABLE `goods_options` ADD COLUMN `option_value2` VARCHAR(40) DEFAULT NULL AFTER `option_value1`;
ALTER TABLE `goods_options` ADD COLUMN `option_value3` VARCHAR(40) DEFAULT NULL AFTER `option_value2`;
ALTER TABLE `goods_options` ADD COLUMN `option_value4` VARCHAR(40) DEFAULT NULL AFTER `option_value3`;
ALTER TABLE `goods_options` ADD COLUMN `option_value5` VARCHAR(40) DEFAULT NULL AFTER `option_value4`;

ALTER TABLE `goods_options` ADD COLUMN `option_image` VARCHAR(255) NOT NULL AFTER `option_cost_price`;
ALTER TABLE `goods_options` ADD COLUMN `option_memo` VARCHAR(255) NOT NULL AFTER `option_cost_price`;
ALTER TABLE `goods_options` ADD COLUMN `option_code` VARCHAR(64) NOT NULL AFTER `option_cost_price`;
ALTER TABLE `goods_options` ADD COLUMN `option_sell_fl` VARCHAR(1) NOT NULL AFTER `option_cost_price`;
ALTER TABLE `goods_options` ADD COLUMN `option_view_fl` VARCHAR(1) NOT NULL AFTER `option_cost_price`;