--
-- Videos
--
ALTER TABLE `order_inquiries` ADD `attachments` VARCHAR(2000) DEFAULT NULL AFTER comment;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 139;
-- ALTER TABLE `order_inquiries` DROP COLUMN `attachments`;

