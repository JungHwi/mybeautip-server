--
-- devices
--
CREATE TABLE `devices` (
  `id` VARCHAR(500) CHARACTER SET ascii NOT NULL,
  `arn` VARCHAR(128) NOT NULL,
  `os` VARCHAR(10) NOT NULL,
  `os_version` VARCHAR(10) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `language` VARCHAR(4) NOT NULL,
  `timezone` VARCHAR(40) NOT NULL,
  `app_version` VARCHAR(10) NOT NULL,
  `pushable` TINYINT DEFAULT 1,
  `created_by` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_devices_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- notices
--
CREATE TABLE `notices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(30) NOT NULL,
  `os` VARCHAR(10) NOT NULL,
  `message` VARCHAR(500) NOT NULL,
  `min_version` VARCHAR(10) NOT NULL,
  `max_version` VARCHAR(10) NOT NULL,
  `created_by` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
