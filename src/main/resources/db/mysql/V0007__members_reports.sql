--
-- members reports
--
CREATE TABLE `members_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `reason` VARCHAR(400) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_reports` (`me`, `you`),
  CONSTRAINT `fk_reports_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_reports_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;