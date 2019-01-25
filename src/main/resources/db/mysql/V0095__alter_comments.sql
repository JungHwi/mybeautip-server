ALTER TABLE `comments` ADD COLUMN `locked` TINYINT(1) DEFAULT 0 AFTER `video_id`;
ALTER TABLE `comments` ADD COLUMN `original_comment` VARCHAR(500) DEFAULT NULL AFTER `comment`;


--delete from flyway_schema_history where version='0095';
--alter table comments drop column locked;