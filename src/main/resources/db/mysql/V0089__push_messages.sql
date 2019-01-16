--
-- (Instant) Push Messages
--
CREATE TABLE `push_messages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` TINYINT DEFAULT 0,  -- 1: instant, ...
  `platform` TINYINT DEFAULT 0,  -- 1: ios, 2: android
  `resource_type` VARCHAR(20) DEFAULT NULL,
  `resource_ids` VARCHAR(20) DEFAULT NULL,
  `title` VARCHAR(255) NOT NULL,
  `body` VARCHAR(4096) NOT NULL,
  `target_device_count` INT DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_push_messages_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



--delete from flyway_schema_history where version='0089';
--drop table push_history;

