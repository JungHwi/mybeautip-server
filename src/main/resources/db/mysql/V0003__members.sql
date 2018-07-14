--
-- members followings
--
CREATE TABLE `members_followings` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `i` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` BIGINT(13) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_followings` (`i`, `you`),
  CONSTRAINT `fk_followings_i` FOREIGN KEY (`i`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_followings_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- members block
--
CREATE TABLE `members_block` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `i` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` BIGINT(13) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
