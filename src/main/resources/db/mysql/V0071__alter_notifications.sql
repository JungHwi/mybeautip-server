-- Add new column
ALTER TABLE `notifications` ADD COLUMN `resource_ids` VARCHAR(200) NOT NULL AFTER `resource_id`;