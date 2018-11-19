CREATE TABLE `accounts` (
  `member_id` BIGINT NOT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  `bank_name` VARCHAR(50) DEFAULT NULL,
  `bank_account` VARCHAR(50) DEFAULT NULL,
  `bank_depositor` VARCHAR(50) DEFAULT NULL,
  `validity` TINYINT NOT NULL DEFAULT 1 COMMENT '0:invalid, 1: ok',
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`member_id`),
  CONSTRAINT `fk_accounts_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;