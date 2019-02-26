ALTER TABLE `order_purchases` ADD COLUMN `confirmed` TINYINT(1) DEFAULT NULL;

-- ALTER TABLE `order_purchases` DROP COLUMN `confirmed`;
-- DELETE FROM flyway_schema_history WHERE version='0103';