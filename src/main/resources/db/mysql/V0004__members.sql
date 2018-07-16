--
-- members followings
--
CREATE TABLE `members_followings` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_followings` (`me`, `you`),
  CONSTRAINT `fk_followings_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_followings_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- members blocks
--
CREATE TABLE `members_blocks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_block` (`me`, `you`),
  CONSTRAINT `fk_block_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_block_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
