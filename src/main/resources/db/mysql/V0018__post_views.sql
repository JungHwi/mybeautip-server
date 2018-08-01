CREATE TABLE `view_recodings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `item_id` VARCHAR(10) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: post, 2: goods',
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_viewed_posts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
