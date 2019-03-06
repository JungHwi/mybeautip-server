ALTER TABLE `schedules` ADD COLUMN `instant_title` VARCHAR(30) DEFAULT NULL;
ALTER TABLE `schedules` ADD COLUMN `instant_message` VARCHAR(120) DEFAULT NULL;

--
-- ALTER TABLE `schedules` DROP COLUMN `instant_message`;
-- ALTER TABLE `schedules` DROP COLUMN `instant_title`;
-- delete from flyway_schema_history where installed_rank = 108;
--