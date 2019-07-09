--
-- member point details
--
CREATE TABLE `member_point_details` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `state` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1: Earned points, 2: Use points',
  `point` INT NOT NULL DEFAULT 0,
  `parent_id` BIGINT NULL,
  `member_point_id` BIGINT NOT NULL,
  `order_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `expiry_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_point_details_members` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_point_details_member_points` FOREIGN KEY (`member_point_id`) REFERENCES `member_points` (`id`),
  CONSTRAINT `fk_point_details_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 119;
-- drop table member_point_details;