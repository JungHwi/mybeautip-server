--
-- Revenue payments
--
CREATE TABLE `revenue_payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `date` VARCHAR(7) NOT NULL,  -- YYYY-MM
  `state` TINYINT DEFAULT 0,  -- 0: not paid, 1: paid, 2: n/a
  `estimated_amount` INT DEFAULT 0,
  `final_amount` INT DEFAULT 0,
  `payment_method` VARCHAR(80) DEFAULT NULL,
  `payment_date` VARCHAR(10) DEFAULT NULL, -- YYYY-MM-DD
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_revenue_returns_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_revenue_returns_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `member_revenues` ADD COLUMN `revenue_payment_id` BIGINT DEFAULT NULL;
ALTER TABLE `member_revenues` ADD COLUMN `confirmed_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `member_revenues` ADD CONSTRAINT `fk_member_revenues_payments` FOREIGN KEY (`revenue_payment_id`) REFERENCES `revenue_payments` (`id`);


--ALTER TABLE `member_revenues` DROP FOREIGN KEY `fk_member_revenues_payments`;
--ALTER TABLE `member_revenues` DROP COLUMN `revenue_payment_id`;
--ALTER TABLE `member_revenues` DROP COLUMN `confirmed_at`;
--DROP TABLE `revenue_payments`;
--DELETE FROM flyway_schema_history WHERE version='0098';