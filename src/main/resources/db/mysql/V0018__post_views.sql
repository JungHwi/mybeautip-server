CREATE TABLE `viewed_posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_viewed_posts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_viewed_posts_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;