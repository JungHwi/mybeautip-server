--
-- App Info
--
CREATE TABLE `app_info` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `os` VARCHAR(255) NOT NULL, -- android, ios, web
  `version` VARCHAR(20) NOT NULL,
  `data` VARCHAR(255), -- reserved
  `message` VARCHAR(255),  -- reserved
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;