--
-- member billing auths
--
CREATE TABLE `member_billing_auths` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `username` VARCHAR(10) DEFAULT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  `password` VARCHAR(256) DEFAULT NULL,
  `salt` VARCHAR(20) DEFAULT NULL,
  `error_count` INT NOT NULL DEFAULT 0,
  `reset_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_billing_auths_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DROP TABLE `member_billing_auths`;
-- DELETE FROM flyway_schema_history WHERE version='0137';