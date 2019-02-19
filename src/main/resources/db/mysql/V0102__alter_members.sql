ALTER TABLE `members` ADD COLUMN `phone_number` VARCHAR(20) DEFAULT NULL AFTER `email`;
UPDATE `members` SET `phone_number`='';

ALTER TABLE `orders` ADD COLUMN `buyer_phone_number` VARCHAR(20) NOT NULL AFTER `state`;

-- ALTER TABLE `members` DROP COLUMN `phone_number`;
-- ALTER TABLE `orders` DROP COLUMN `buyer_phone_number`;
-- DELETE FROM flyway_schema_history WHERE version='0102';