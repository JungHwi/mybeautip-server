ALTER TABLE `videos` ADD COLUMN `live_key` VARCHAR(30) DEFAULT NULL AFTER `duration`;
ALTER TABLE `videos` ADD COLUMN `output_type` VARCHAR(20) DEFAULT NULL AFTER `live_key`;


--ALTER TABLE `videos` DROP COLUMN `output_type`;
--ALTER TABLE `videos` DROP COLUMN `live_key`;
--delete from flyway_schema_history where installed_rank = 131;