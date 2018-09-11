--
-- coupons
--
CREATE TABLE `coupons` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: welcome, 1: fixed price, 2: fixed rate, 3: etc',
  `title` VARCHAR(20) DEFAULT NULL,
  `description` VARCHAR(128) DEFAULT NULL,
  `condition` VARCHAR(128) DEFAULT NULL,
  `discount_price` INT UNSIGNED DEFAULT NULL,
  `discount_rate` TINYINT DEFAULT NULL,
  `condition_price` INT UNSIGNED DEFAULT NULL,
  `started_at` DATETIME NOT NULL,
  `ended_at` DATETIME NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_coupons_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- member coupons
--
CREATE TABLE `member_coupons` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `coupon_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `used_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_member_coupons_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`),
  CONSTRAINT `fk_member_coupons_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `orders` ADD COLUMN `coupon_id` BIGINT DEFAULT NULL after `status`;
ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_member_coupons` FOREIGN KEY (`coupon_id`) REFERENCES `member_coupons` (`id`);

