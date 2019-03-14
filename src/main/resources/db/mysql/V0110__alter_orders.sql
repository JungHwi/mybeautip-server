ALTER TABLE `orders` ADD COLUMN `mall_order_id` VARCHAR(16) DEFAULT NULL AFTER `number`;

ALTER TABLE `order_purchases` ADD COLUMN `mall_order_goods_id` INTEGER DEFAULT NULL AFTER `order_id`;


--ALTER TABLE `orders` DROP COLUMN `mall_order_id`;
--ALTER TABLE `order_purchases` DROP COLUMN `mall_order_goods_id`;
--delete from flyway_schema_history where installed_rank = 110;