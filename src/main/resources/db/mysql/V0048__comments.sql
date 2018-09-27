drop table post_comment_likes;
drop table post_comments;
drop table video_comment_likes;
drop table video_comments;

--
-- Comments
--
CREATE TABLE `comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: post, 2: video',
  `post_id` BIGINT DEFAULT NULL,
  `video_id` BIGINT DEFAULT NULL,
  `comment` VARCHAR(500) NOT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_comments_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_comments_posts` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `fk_comments_videos` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Comment Likes
--
CREATE TABLE `comment_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_comment_likes` (`created_by`, `comment_id`),
  CONSTRAINT `fk_comment_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_comment_likes_comment` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
