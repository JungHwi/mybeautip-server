--
-- cancel order
--
CREATE TABLE `order_inquiries` (
  `id` BIGINT NOT NULL,
  `state` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: cancel payment, 1: request exchange, 2: request return',
  `reason` VARCHAR(500) DEFAULT NULL,
  `comment` VARCHAR(500) DEFAULT NULL,
  `completed` TINYINT NOT NULL DEFAULT 0 COMMENT '0:not yet, 1: completed',
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_order_inquiries_order` FOREIGN KEY (`id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_inquiries_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
