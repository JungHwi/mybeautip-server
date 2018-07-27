--
-- stores
--
CREATE TABLE `stores` (
  `id` BIGINT NOT NULL,
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
  `store_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_store_likes` (`created_by`, `store_id`),
  CONSTRAINT `fk_store_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_store_likes_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;