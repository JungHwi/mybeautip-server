--
-- banners
--
CREATE TABLE `banners` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(22) NOT NULL,
  `description` VARCHAR(34) NOT NULL,
  `thumbnail_url` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: post, 2: goods, 3: goods list, 4: video',
  `seq` INT NOT NULL,
  `link` VARCHAR(255) NOT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  `view_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_banners_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE `trends`;

ALTER TABLE `posts` DROP COLUMN `banner_text`;
