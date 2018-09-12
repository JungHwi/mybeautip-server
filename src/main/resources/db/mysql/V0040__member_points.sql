--
-- member points
--
CREATE TABLE `member_points` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `state` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: Will be earned, 1: Earned points, 2: Use points, 3: Expired points',
  `point` INT NOT NULL DEFAULT 0,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `earned_at` DATETIME DEFAULT NULL,
  `expired_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_member_points_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `members` CHANGE COLUMN `coin` `point` INT DEFAULT 0;

-- Revert member points
-- ALTER TABLE `members` CHANGE COLUMN `point` `coin` INT DEFAULT 0; delete from flyway_schema_history where installed_rank = 40; drop table member_points;