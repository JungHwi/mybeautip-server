ALTER TABLE `revenue_payments` MODIFY COLUMN `payment_date` DATETIME(3) DEFAULT NULL;

--DELETE FROM flyway_schema_history WHERE version='0122';
--ALTER TABLE `revenue_payments` MODIFY COLUMN `payment_date` VARCHAR(10) DEFAULT NULL;