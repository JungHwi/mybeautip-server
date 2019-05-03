--
-- time sale goods
--
CREATE TABLE `time_sales` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `fixed_price` DECIMAL(12,2),
  `goods_price` DECIMAL(12,2),
  `broker` BIGINT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_time_sale_member` FOREIGN KEY (`broker`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- time sale goods options
--
CREATE TABLE `time_sale_options` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` INT NOT NULL,
  `option_no` TINYINT UNSIGNED NOT NULL,
  `option_price` INT NOT NULL DEFAULT 0,
  `broker` BIGINT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_time_sale_options_member` FOREIGN KEY (`broker`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;