-- Add new column
ALTER TABLE `devices` ADD COLUMN `valid` TINYINT(1) NOT NULL DEFAULT 0 AFTER `app_version`;

-- Add new column
ALTER TABLE `members` ADD COLUMN `pushable` TINYINT(1) NOT NULL DEFAULT 0 AFTER `revenue_modified_at`;