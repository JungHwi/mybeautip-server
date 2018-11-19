--
-- Delivery Charge
--
CREATE TABLE `delivery_charge_details` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `delivery_charge_id` INT NOT NULL,
  `unit_start` INT NOT NULL DEFAULT 0,
  `unit_end` INT NOT NULL DEFAULT 0,
  `price` INT NOT NULL DEFAULT 0,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_details_delivery_charge` FOREIGN KEY (`delivery_charge_id`) REFERENCES `delivery_charge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `carts`;

CREATE TABLE `carts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `checked` TINYINT NOT NULL DEFAULT 1,
  `goods_no` VARCHAR(10) NOT NULL,
  `option_id` BIGINT DEFAULT NULL,
  `store_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_carts_goods_option` (`goods_no`, `option_id`),
  CONSTRAINT `fk_carts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_carts_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`),
  CONSTRAINT `fk_carts_options` FOREIGN KEY (`option_id`) REFERENCES `goods_options` (`sno`),
  CONSTRAINT `fk_carts_stores` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `goods` ADD COLUMN `total_stock` INT NOT NULL DEFAULT 0 AFTER `goods_weight`;