--
-- Tags
--
CREATE TABLE `tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `ref_count` INT DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Keyword Recommendations
--
CREATE TABLE `keyword_recommendations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: member, 2: tag',
  `member_id` BIGINT DEFAULT NULL,
  `tag_id` BIGINT DEFAULT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `started_at` DATETIME(3) DEFAULT NULL,
  `ended_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_keyword_recommendations_members` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_keyword_recommendations_tags` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;