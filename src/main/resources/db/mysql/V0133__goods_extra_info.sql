--
-- member point details
--
CREATE TABLE `goods_extra_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(255) NOT NULL,
  `state` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1: MMS',
  `extra_fee_info` VARCHAR(40) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_goods_extra_info_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 133;
-- drop table goods_extra_info;
