--
-- member points
--
CREATE TABLE `member_points` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: Get points, 1: Use points, 2: Points expired',
  `usage` VARCHAR(30) NOT NULL,
  `point` INT NOT NULL DEFAULT 0,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `earned_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_member_points_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `members` CHANGE COLUMN `coin` `point` INT DEFAULT 0;