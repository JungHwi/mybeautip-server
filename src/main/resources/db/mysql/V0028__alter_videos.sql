ALTER TABLE `videos` ADD COLUMN `related_goods_thumbnail_url` VARCHAR(255) after `comment_count`;
ALTER TABLE `videos` ADD COLUMN `related_goods_count` TINYINT DEFAULT 0 after `comment_count`;


ALTER TABLE `goods` ADD COLUMN `option_name` VARCHAR(255) DEFAULT NULL after `detail_image_data`;
ALTER TABLE `goods` ADD COLUMN `option_fl` VARCHAR(1) DEFAULT NULL after `detail_image_data`;
ALTER TABLE `goods` ADD COLUMN `goods_must_info` TEXT DEFAULT NULL after `detail_image_data`;


ALTER TABLE `goods` MODIFY COLUMN `goods_discount` INT NOT NULL DEFAULT 0;
ALTER TABLE `goods` MODIFY COLUMN `goods_price` INT NOT NULL DEFAULT 0;
ALTER TABLE `goods` MODIFY COLUMN `fixed_price` INT NOT NULL DEFAULT 0;

--
-- Goods OptionData
--
CREATE TABLE `goods_options` (
  `sno` BIGINT NOT NULL,
  `goods_no` INT NOT NULL,
  `option_no` TINYINT UNSIGNED NOT NULL,
  `option_value1` VARCHAR(40) DEFAULT NULL,
  `option_price` INT NOT NULL DEFAULT 0,
  `option_cost_price` INT NOT NULL DEFAULT 0,
  `stock_cnt` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
