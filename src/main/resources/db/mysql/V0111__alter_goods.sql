ALTER TABLE `goods` ADD COLUMN `base_shipping` INT UNSIGNED DEFAULT 0 AFTER `delivery_fix_fl`;

ALTER TABLE `accounts` DROP COLUMN `email`;


--ALTER TABLE `accounts` ADD COLUMN `email` VARCHAR(50) DEFAULT NULL AFTER `member_id`;
--ALTER TABLE `goods` DROP COLUMN `base_shipping`;
--delete from flyway_schema_history where installed_rank = 111;