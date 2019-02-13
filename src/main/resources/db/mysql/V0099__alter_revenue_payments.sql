ALTER TABLE `revenue_payments` DROP FOREIGN KEY `fk_revenue_returns_created_by`;
ALTER TABLE `revenue_payments` DROP COLUMN `created_by`;

--DELETE FROM flyway_schema_history WHERE version='0099';
