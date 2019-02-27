ALTER TABLE `member_points` ADD COLUMN `order_id` BIGINT DEFAULT 0 AFTER `member_id`;
--  order_id can be null when STATE_EXPIRED_POINT
ALTER TABLE `member_points` ADD CONSTRAINT `fk_member_points_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);


--TRUNCATE TABLE `member_points`;
--UPDATE `members` SET `point`=0;
--ALTER TABLE `member_points` DROP FOREIGN KEY `fk_member_points_orders`;
--ALTER TABLE `member_points` DROP COLUMN `order_id`;
--DELETE FROM flyway_schema_history WHERE version='0105';