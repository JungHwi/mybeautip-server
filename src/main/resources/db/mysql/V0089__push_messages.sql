--
-- (Instant) Push History
--
CREATE TABLE `push_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `target` BIGINT DEFAULT NULL,  -- target member
  `os` VARCHAR(10) DEFAULT NULL,  -- "ios", "android" ...
  `resource_type` VARCHAR(20) DEFAULT NULL,
  `resource_ids` VARCHAR(20) DEFAULT NULL,
  `title` VARCHAR(255) NOT NULL,
  `body` VARCHAR(4096) NOT NULL,
  `success` TINYINT(1) DEFAULT 0,  -- true(1): send success, false(0): exception
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_push_history_target` FOREIGN KEY (`target`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_push_history_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



--
-- Delete duplicated view logs
--
delete from view_recodings where id not in (select * from(select min(id) from view_recodings group by item_id, category, created_by) as temp);

--delete from flyway_schema_history where version='0089';
--drop table push_history;

