--
-- notifications
--
CREATE TABLE `notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `target_member` BIGINT NOT NULL,
  `source_member` BIGINT DEFAULT NULL,
  `type` VARCHAR(50) NOT NULL,
  `read` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `resource_type` VARCHAR(50) DEFAULT NULL,
  `resource_id` BIGINT DEFAULT NULL,
  `resource_owner` BIGINT DEFAULT NULL,
  `image_url` VARCHAR(250) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_notification_target_member` FOREIGN KEY (`target_member`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- notification message arguments
--

CREATE TABLE `notification_args` (
  `notification_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `arg` VARCHAR(255) NOT NULL,
  PRIMARY KEY(`notification_id`, `seq`),
  CONSTRAINT `fk_notification_args` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- notification custom arguments
--
CREATE TABLE `notification_customs` (
  `notification_id` BIGINT NOT NULL,
  `key` VARCHAR(100) NOT NULL,
  `value` VARCHAR(100) NOT NULL,
  PRIMARY KEY(`notification_id`, `key`),
  CONSTRAINT `fk_notification_customs` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
