--
-- Posts
--
CREATE TABLE `posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(32) NOT NULL,
  `banner_text` VARCHAR(32) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  `thumbnail_url` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: trend, 2: card news',
  `view_count` INT NOT NULL DEFAULT 0,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `goods` TINYINT NOT NULL DEFAULT 0 COMMENT '0: no goods, 1: exist goods',
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME NOT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_posts_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post contents
--
CREATE TABLE `post_contents` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT DEFAULT NULL,
  `priority` INT NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: text, 2: image, 4: video',
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post goods
--
CREATE TABLE `post_goods` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT DEFAULT NULL,
  `priority` INT NOT NULL,
  `goods_no` VARCHAR(10) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




