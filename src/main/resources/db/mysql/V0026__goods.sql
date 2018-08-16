ALTER TABLE `goods` ADD `all_cd` VARCHAR(50) NULL DEFAULT NULL AFTER `cate_cd`;
ALTER TABLE `goods` ADD `video_url` VARCHAR(200) NULL DEFAULT NULL AFTER `detail_image_Data`;

DROP TABLE `store_likes`;
DROP TABLE `stores`;

--
-- stores
--
CREATE TABLE `stores` (
  `id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255),
  `image_url` VARCHAR(255),
  `thumbnail_url` VARCHAR(255),
  `like_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- store likes
--
CREATE TABLE `store_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_store_likes` (`created_by`, `store_id`),
  CONSTRAINT `fk_store_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_store_likes_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



