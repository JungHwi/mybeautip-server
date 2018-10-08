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