--
-- Member Leave Log
--
CREATE TABLE `member_leave_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `reason` VARCHAR(255) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;