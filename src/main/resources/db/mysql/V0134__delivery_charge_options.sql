--
-- delivery charge options
--
CREATE TABLE `delivery_charge_options` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `delivery_charge_id` INT NOT NULL,
  `extra_info` VARCHAR(40) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_options_delivery_charge` FOREIGN KEY (`delivery_charge_id`) REFERENCES `delivery_charge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 134;
-- drop table delivery_charge_options;