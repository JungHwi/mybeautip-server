ALTER TABLE `order_purchases` MODIFY COLUMN `option_value` VARCHAR(400) DEFAULT NULL;

-- DELETE FROM flyway_schema_history WHERE version='0126';
-- ALTER TABLE `order_purchases` MODIFY COLUMN `option_value` VARCHAR(40) DEFAULT NULL;

