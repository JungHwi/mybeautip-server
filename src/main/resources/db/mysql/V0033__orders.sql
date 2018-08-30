--
-- orders
--
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `number` BIGINT NOT NULL,
  `goods_count` INT NOT NULL,
  `price` BIGINT NOT NULL,
  `point` INT DEFAULT 0,
  `method` VARCHAR(20) DEFAULT "card",
  `status` VARCHAR(20) DEFAULT NULL COMMENT 'ordered, paid, ...',
  `video_id` BIGINT DEFAULT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_orders_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order delivery
--
CREATE TABLE `order_deliveries` (
  `id` BIGINT NOT NULL,
  `recipient` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `zip_no` VARCHAR(10) NOT NULL,
  `road_addr_part1` VARCHAR(255) NOT NULL,
  `road_addr_part2` VARCHAR(255) NOT NULL,
  `jibun_addr` VARCHAR(255) NOT NULL,
  `detail_address` VARCHAR(100) NOT NULL,
  `carrier` VARCHAR(20) DEFAULT NULL,
  `invoice` VARCHAR(30) DEFAULT NULL,
  `carrier_message` VARCHAR(50) DEFAULT "",
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_deliveries_order` FOREIGN KEY (`id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order payments
--
CREATE TABLE `order_payments` (
  `id` BIGINT NOT NULL,
  `payment_id` VARCHAR(30) DEFAULT NULL,
  `price` BIGINT NOT NULL,
  `method` VARCHAR(20) NOT NULL COMMENT 'iamport',
  `state` TINYINT NOT NULL DEFAULT 0 COMMENT '0:created, 1: stopped, 2: failed, ...',
  `message` VARCHAR(255) DEFAULT NULL,
  `receipt` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_payments_order` FOREIGN KEY (`id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order purchases
--
CREATE TABLE `order_purchases` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `goods_no` VARCHAR(10) NOT NULL,
  `goods_price` BIGINT NOT NULL,
  `option_id` VARCHAR(10) NOT NULL,
  `option_value` VARCHAR(40) NOT NULL,
  `option_price` BIGINT DEFAULT NULL,
  `quantity` BIGINT NOT NULL,
  `total_price` BIGINT DEFAULT NULL,
  `video_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_purchases_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_purchases_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

