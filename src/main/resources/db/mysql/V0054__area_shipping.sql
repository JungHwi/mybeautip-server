--
-- Delivery Charge
--
CREATE TABLE `delivery_charge_area` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `area` VARCHAR(255) NOT NULL UNIQUE,
  `part1` VARCHAR(20) NOT NULL,
  `part2` VARCHAR(20) NOT NULL,
  `part3` VARCHAR(20) NOT NULL,
  `part4` VARCHAR(20) NOT NULL,
  `price` INT NOT NULL DEFAULT 0,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `addresses` ADD COLUMN `area_shipping` INT NOT NULL DEFAULT 0 after `detail_address`;


--
-- Remove cart unique key (uk_carts_goods_option)
--
ALTER TABLE `carts` DROP FOREIGN KEY fk_carts_goods;
ALTER TABLE `carts` DROP FOREIGN KEY fk_carts_options;
ALTER TABLE `carts` DROP INDEX uk_carts_goods_option;
ALTER TABLE `carts` ADD CONSTRAINT `fk_carts_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`);
ALTER TABLE `carts` ADD CONSTRAINT `fk_carts_options` FOREIGN KEY (`option_id`) REFERENCES `goods_options` (`sno`);