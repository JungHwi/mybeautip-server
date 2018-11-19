-- Move carrier and invoice to order_purchases
ALTER TABLE `order_purchases` ADD COLUMN `carrier` VARCHAR(20) DEFAULT NULL AFTER `total_price`;
ALTER TABLE `order_purchases` ADD COLUMN `invoice` VARCHAR(30) DEFAULT NULL AFTER `carrier`;
ALTER TABLE `order_purchases` ADD COLUMN `modified_at` DATETIME DEFAULT NULL;
ALTER TABLE `order_purchases` ADD COLUMN `delivered_at` DATETIME DEFAULT NULL;
ALTER TABLE `order_purchases` ADD COLUMN `state` TINYINT DEFAULT 0 AFTER `goods_no`;


UPDATE `order_purchases` SET state = 1 WHERE status = 'ordered';
UPDATE `order_purchases` SET state = 2 WHERE status = 'paid';
UPDATE `order_purchases` SET state = 3 WHERE status = 'preparing';
UPDATE `order_purchases` SET state = 4 WHERE status = 'delivering';
UPDATE `order_purchases` SET state = 5 WHERE status = 'delivered';

UPDATE `order_purchases` SET state = 11 WHERE status = 'order_cancelling';
UPDATE `order_purchases` SET state = 12 WHERE status = 'order_cancelled';

UPDATE `order_purchases` SET state = 3 WHERE status = 'order_exchanging';
UPDATE `order_purchases` SET state = 3 WHERE status = 'order_exchanged';
UPDATE `order_purchases` SET state = 3 WHERE status = 'order_returning';
UPDATE `order_purchases` SET state = 3 WHERE status = 'order_returned';


ALTER TABLE `order_deliveries` DROP COLUMN `carrier`;
ALTER TABLE `order_deliveries` DROP COLUMN `invoice`;

ALTER TABLE `orders` ADD COLUMN `delivered_at` DATETIME DEFAULT NULL;


-- DELETE FROM `flyway_schema_history` WHERE `installed_rank` = 64;
-- ALTER TABLE `order_purchases` DROP COLUMN `carrier`;
-- ALTER TABLE `order_purchases` DROP COLUMN `invoice`;
-- ALTER TABLE `order_purchases` DROP COLUMN `modified_at`;
-- ALTER TABLE `order_purchases` DROP COLUMN `delivered_at`;
-- ALTER TABLE `order_purchases` DROP COLUMN `state`;
--
-- ALTER TABLE `orders` DROP COLUMN `delivered_at`;
--
-- ALTER TABLE `order_deliveries` ADD COLUMN `carrier` VARCHAR(20) DEFAULT NULL AFTER `detail_address`;
-- ALTER TABLE `order_deliveries` ADD COLUMN `invoice` VARCHAR(30) DEFAULT NULL AFTER `carrier`;
