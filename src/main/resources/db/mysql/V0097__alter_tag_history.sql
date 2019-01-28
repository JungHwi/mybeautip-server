TRUNCATE TABLE `tag_history`;

ALTER TABLE `tag_history` ADD COLUMN `resource_id` BIGINT NOT NULL AFTER `category`;
ALTER TABLE `tag_history` CHANGE COLUMN `tag` `tag_id` BIGINT NOT NULL;
ALTER TABLE `tag_history` DROP COLUMN `count`;
ALTER TABLE `tag_history` ADD CONSTRAINT `fk_tag_history_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`);
ALTER TABLE `videos` DROP COLUMN `tag_info`;


--delete from flyway_schema_history where version='0097';
--ALTER TABLE `tag_history` DROP FOREIGN KEY `fk_tag_history_tag`;
--ALTER TABLE `tag_history` DROP COLUMN `resource_id`;
--ALTER TABLE `tag_history` ADD COLUMN `count` INT DEFAULT 0;
--ALTER TABLE `tag_history` CHANGE COLUMN `tag_id` `tag` BIGINT NOT NULL;
--ALTER TABLE `videos` ADD COLUMN `tag_info` VARCHAR(255);
