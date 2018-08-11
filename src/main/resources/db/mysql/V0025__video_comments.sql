--
-- Videos
--
CREATE TABLE `videos` (
  `video_key` BIGINT NOT NULL,
  `type` VARCHAR(11) NOT NULL,
  `thumbnail_url` VARCHAR(200) NOT NULL,
  `comment_count` INT NOT NULL DEFAULT 0,
  `owner` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`video_key`),
  CONSTRAINT `fk_videos_owner` FOREIGN KEY (`owner`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Video Comments
--
CREATE TABLE `video_comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_key` BIGINT NOT NULL,
  `comment` VARCHAR(500) DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_video_comments_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_video_comments_videos` FOREIGN KEY (`video_key`) REFERENCES `videos` (`video_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE `video_goods`;
--
-- Video with Goods
--
CREATE TABLE `video_goods` (
	id BIGINT NOT NULL AUTO_INCREMENT,
	video_key BIGINT NOT NULL,
	goods_no VARCHAR(10) NOT NULL,
	created_at DATETIME(3) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT `fk_video_goods_videos` FOREIGN KEY (`video_key`) REFERENCES `videos` (`video_key`),
	CONSTRAINT `fk_video_goods_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `motd_recommendations` modify COLUMN `video_key` BIGINT NOT NULL;

ALTER TABLE `motd_recommendations` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `motd_recommendations` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `motd_recommendations` modify COLUMN `started_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `motd_recommendations` modify COLUMN `ended_at` DATETIME(3) DEFAULT NULL;
