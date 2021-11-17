--
-- member billings
--
CREATE TABLE `member_billings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `base` TINYINT NOT NULL DEFAULT 0 COMMENT '1:primary 0:extra',
  `customer_id` VARCHAR(256) DEFAULT NULL,
  `salt` VARCHAR(16) DEFAULT NULL,
  `member_id` BIGINT NOT NULL,
  `valid` TINYINT NOT NULL DEFAULT 0 COMMENT '1:valid 0:invalid',
  `card_name` VARCHAR(16) DEFAULT NULL,
  `card_number` VARCHAR(24) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_billings_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DROP TABLE `member_billings`;
-- DELETE FROM flyway_schema_history WHERE version='0136';
