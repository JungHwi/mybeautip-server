ALTER TABLE `members` ADD COLUMN `phone_number` VARCHAR(20) DEFAULT NULL AFTER `email`;
UPDATE `members` SET `phone_number`='';

-- ALTER TABLE `members` DROP COLUMN `phone_number`;
-- DELETE FROM flyway_schema_history WHERE version='0102';