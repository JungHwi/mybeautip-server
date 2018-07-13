--
-- addresses
--
CREATE TABLE `addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_by` BIGINT NOT NULL,
  `base` TINYINT NOT NULL DEFAULT 0 COMMENT '1:primary 0:extra',
  `title` VARCHAR(20) NOT NULL,
  `recipient` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `zip_no` VARCHAR(10) NOT NULL,
  `road_addr_part1` VARCHAR(255) NOT NULL,
  `road_addr_part2` VARCHAR(255) NOT NULL,
  `jibun_addr` VARCHAR(255) NOT NULL,
  `detail_address` VARCHAR(100) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_addresses_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

