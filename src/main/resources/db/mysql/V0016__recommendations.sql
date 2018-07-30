--
-- member recommendations
--
CREATE TABLE `member_recommendations` (
  `member_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`member_id`),
  CONSTRAINT `fk_member_recommendations_members` FOREIGN KEY (`member_id`) REFERENCES `members`
  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- goods recommendations
--
CREATE TABLE `goods_recommendations` (
  `goods_no` VARCHAR(10) NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`goods_no`),
  CONSTRAINT `fk_goods_recommendations_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods`
  (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;