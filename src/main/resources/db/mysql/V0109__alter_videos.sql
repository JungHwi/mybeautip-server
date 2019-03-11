ALTER TABLE `videos` ADD COLUMN `ended_at` DATETIME(3) DEFAULT NULL AFTER `created_at`;

ALTER TABLE `orders` ADD COLUMN `on_live` TINYINT(1) DEFAULT 0 AFTER `video_id`;
ALTER TABLE `order_purchases` ADD COLUMN `on_live` TINYINT(1) DEFAULT 0 AFTER `video_id`;
ALTER TABLE `order_purchases` DROP COLUMN `confirmed`;


--ALTER TABLE `order_purchases` ADD COLUMN `confirmed` TINYINT(1) DEFAULT NULL;
--ALTER TABLE `order_purchases` DROP COLUMN `on_live`;
--ALTER TABLE `orders` DROP COLUMN `on_live`;
--ALTER TABLE `videos` DROP COLUMN `ended_at`;
--delete from flyway_schema_history where installed_rank = 109;