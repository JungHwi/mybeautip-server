--
-- MOTD Recommendations
--
CREATE TABLE `motd_recommendations` (
  `video_key` VARCHAR(100) NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`video_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;