--
-- Carts
--
CREATE TABLE `carts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `option_no` INT NOT NULL DEFAULT 0,
  `scm_no` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_carts_goods_option` (`goods_no`, `option_no`),
  CONSTRAINT `fk_carts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_carts_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `stores` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `stores` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `stores` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `store_likes` modify COLUMN `created_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `banned_words` modify COLUMN `created_at` DATETIME(3) DEFAULT NULL;

