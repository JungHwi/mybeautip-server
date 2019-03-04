--
-- schedules
--
CREATE TABLE `schedules` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `thumbnail_url` VARCHAR(200) NOT NULL,
  `started_at` DATETIME NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_schedules_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- drop table video_schedules;
-- delete from flyway_schema_history where installed_rank = 107;

-- insert into schedules (title, thumbnail_url, started_at, created_by, created_at, modified_at) values ('리빙코랄 최애템', 'https://s3.ap-northeast-2.amazonaws.com/mybeautip/trend/20190110_193657/thumbnail.png?time=1550719504707', '2019-03-04 9:00:00', 370, now(), now());
-- insert into schedules (title, thumbnail_url, started_at, created_by, created_at, modified_at) values ('레드립 컬렉션', 'https://s3.ap-northeast-2.amazonaws.com/mybeautip/trend/20190215_191949/thumbnail.png?time=1550719504707', '2019-03-05 13:00:00', 376, now(), now());
-- insert into schedules (title, thumbnail_url, started_at, created_by, created_at, modified_at) values ('남자 눈썹 다듬기', 'https://s3.ap-northeast-2.amazonaws.com/mybeautip/trend/20190215_183503/thumbnail.png?time=1550719504707', '2019-03-06 13:00:00', 371, now(), now());
