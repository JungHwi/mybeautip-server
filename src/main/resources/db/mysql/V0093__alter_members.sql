ALTER TABLE `members` ADD COLUMN `permission` INT UNSIGNED DEFAULT 0 AFTER `link`;
ALTER TABLE `videos` ADD COLUMN `report_count` INT UNSIGNED DEFAULT 0 AFTER `order_count`;


--delete from `flyway_schema_history` where version='0093';
--alter table `members` drop column `permission`;
--alter table `videos` drop column `report_count`;
