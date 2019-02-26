ALTER TABLE `push_messages` ADD COLUMN `fail_count` INT UNSIGNED DEFAULT 0 AFTER `target_device_count`;
ALTER TABLE `push_messages` ADD COLUMN `success_count` INT UNSIGNED DEFAULT 0 AFTER `target_device_count`;

ALTER TABLE `orders` MODIFY COLUMN `delivered_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `order_purchases` MODIFY COLUMN `delivered_at` DATETIME(3) DEFAULT NULL;

-- DELETE FROM flyway_schema_history WHERE version='0104';