--
-- Modify order purchases
--

ALTER TABLE `order_purchases` ADD COLUMN `status` VARCHAR(20) DEFAULT NULL after `order_id`;
UPDATE order_purchases p, orders o SET p.status = o.status WHERE p.order_id = o.id;

--
-- Modify order inquiries
--
ALTER TABLE `order_inquiries` DROP FOREIGN KEY `fk_order_inquiries_order`;

ALTER TABLE `order_inquiries` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE `order_inquiries` ADD COLUMN `order_id` BIGINT NOT NULL after `reason`;

ALTER TABLE `order_inquiries` ADD COLUMN `purchase_id` BIGINT DEFAULT NULL after `order_id`;

update order_inquiries set order_id = id;

ALTER TABLE `order_inquiries` ADD CONSTRAINT `fk_order_inquiries_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `order_inquiries` ADD CONSTRAINT `fk_order_inquiries_purchase` FOREIGN KEY (`purchase_id`) REFERENCES `order_purchases` (`id`);


-- Revert flyway history
-- delete from flyway_schema_history where installed_rank = 42;

-- Revert order purchases
-- ALTER TABLE `order_purchases` DROP COLUMN `status`;

-- Revert order inquiries
-- ALTER TABLE `order_inquiries` DROP FOREIGN KEY `fk_order_inquiries_order`;
-- ALTER TABLE `order_inquiries` DROP FOREIGN KEY `fk_order_inquiries_purchase`;
-- ALTER TABLE `order_inquiries` DROP COLUMN `order_id`;
-- ALTER TABLE `order_inquiries` DROP COLUMN `purchase_id`;
-- ALTER TABLE `order_inquiries` MODIFY COLUMN `id` BIGINT NOT NULL;
-- ALTER TABLE `order_inquiries` ADD CONSTRAINT `fk_order_inquiries_order` FOREIGN KEY (`id`) REFERENCES `orders` (`id`);
