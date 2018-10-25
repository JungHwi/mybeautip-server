ALTER TABLE `orders` ADD COLUMN `state` TINYINT DEFAULT 0 AFTER `status`;

UPDATE `orders` SET state = 1 WHERE status = 'ordered';
UPDATE `orders` SET state = 2 WHERE status = 'paid';
UPDATE `orders` SET state = 3 WHERE status = 'preparing';
UPDATE `orders` SET state = 4 WHERE status = 'delivering';
UPDATE `orders` SET state = 5 WHERE status = 'delivered';

UPDATE `orders` SET state = 11 WHERE status = 'order_cancelling';
UPDATE `orders` SET state = 12 WHERE status = 'order_cancelled';

UPDATE `orders` SET state = 21 WHERE status = 'payment_cancelling';
UPDATE `orders` SET state = 22 WHERE status = 'payment_cancelled';


-- DELETE FROM `flyway_schema_history` WHERE `installed_rank` = 63;
-- ALTER TABLE `orders` DROP COLUMN state;
