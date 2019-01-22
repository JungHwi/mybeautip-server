ALTER TABLE `members` ADD COLUMN `permission` INT UNSIGNED DEFAULT 0 AFTER `link`;


--delete from flyway_schema_history where version='0093';
--alter table members drop column permission;
