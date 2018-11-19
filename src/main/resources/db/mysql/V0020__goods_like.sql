--
-- Goods likes
--
CREATE TABLE `goods_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_goods_likes` (`created_by`, `goods_no`),
  CONSTRAINT `fk_goods_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_goods_likes_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Goods
--
ALTER TABLE `goods` ADD `like_count` INT NOT NULL DEFAULT 0;