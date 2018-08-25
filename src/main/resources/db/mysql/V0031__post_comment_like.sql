CREATE TABLE `post_comment_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_post_comment_likes` (`created_by`, `comment_id`),
  CONSTRAINT `fk_post_comment_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_post_comment_likes_comment` FOREIGN KEY (`comment_id`) REFERENCES `post_comments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `post_comments` ADD COLUMN `like_count` INT NOT NULL DEFAULT 0 after `comment_count`;