ALTER TABLE `videos` ADD COLUMN `ended_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `order_purchases` DROP COLUMN `confirmed`;



-- ALTER TABLE `order_purchases` ADD COLUMN `confirmed` TINYINT(1) DEFAULT NULL;
-- ALTER TABLE `videos` DROP COLUMN `ended_at`;
-- delete from flyway_schema_history where installed_rank = 109;