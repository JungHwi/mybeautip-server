ALTER TABLE `comments` ADD COLUMN `locked` TINYINT(1) DEFAULT 0 AFTER `video_id`;

--delete from flyway_schema_history where version='0095';
--alter table comments drop column locked;