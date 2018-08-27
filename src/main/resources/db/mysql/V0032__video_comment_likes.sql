--
-- Video Comment likes
--
CREATE TABLE `video_comment_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_video_comment_likes` (`created_by`, `comment_id`),
  CONSTRAINT `fk_video_comment_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members`
  (`id`),
  CONSTRAINT `fk_video_comment_likes_comment` FOREIGN KEY (`comment_id`) REFERENCES `video_comments`
   (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;