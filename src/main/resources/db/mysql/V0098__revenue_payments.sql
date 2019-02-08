--
-- Revenue payments
--
CREATE TABLE `revenue_payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `target_date` VARCHAR(6) NOT NULL,  -- YYYYMM
  `state` TINYINT DEFAULT 0,  -- 0: not paid, 1: paid, 2: n/a
  `estimated_amount` INT DEFAULT 0,
  `final_amount` INT DEFAULT 0,
  `payment_method` VARCHAR(80) DEFAULT NULL,
  `payment_date` VARCHAR(8) DEFAULT NULL, -- YYYYMMDD
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_revenue_returns_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_revenue_returns_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



DROP TABLE `revenue_payments`;
delete from flyway_schema_history where version='0098';