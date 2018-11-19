--
-- Video watches
--
CREATE TABLE `video_watches` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_video_watches_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`),
  CONSTRAINT `fk_video_watches_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
ALTER TABLE `notifications` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `video_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `post_comment_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `video_comment_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `orders` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `orders` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `orders` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `order_inquiries` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `order_inquiries` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;