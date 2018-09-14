--
-- Delivery Charge
--
CREATE TABLE `delivery_charge` (
  `id` INT NOT NULL,
  `scm_no` INT NOT NULL,
  `method` VARCHAR(40) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `collect_fl` VARCHAR(5) NOT NULL COMMENT '배송비 결제방법: pre = 선불, later = 착불, both = 선착불',
  `fix_fl` VARCHAR(6) NOT NULL COMMENT '배송비 유형 fixed = 고정, free = 무료, price = 금액별, count = 수량별, weight = 무게별'
  `charge_data` TEXT DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;