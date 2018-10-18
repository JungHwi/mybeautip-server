
ALTER TABLE `admin_members` CHANGE COLUMN `admin_id` `email` VARCHAR(50) NOT NULL;

ALTER TABLE `admin_members` ADD COLUMN `store_id` BIGINT DEFAULT NULL AFTER `member_id`;

-- DELETE FROM `flyway_schema_history` WHERE `installed_rank` = 56;
-- ALTER TABLE `admin_members` CHANGE COLUMN `email` `admin_id` VARCHAR(20) NOT NULL;
-- ALTER TABLE `admin_members` DROP COLUMN `store_id`;
