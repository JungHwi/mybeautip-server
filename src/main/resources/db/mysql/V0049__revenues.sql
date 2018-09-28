--
-- Member Revenues
--
CREATE TABLE `member_revenues` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `purchase_id` BIGINT NOT NULL,
  `video_id` BIGINT NOT NULL,
  `revenue` INT DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_member_revenues_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_member_revenues_videos` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`),
  CONSTRAINT `fk_member_revenues_purchases` FOREIGN KEY (`purchase_id`) REFERENCES `order_purchases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `members` ADD COLUMN revenue INT DEFAULT 0 after `video_count`;


-- DROP TABLE IF EXISTS `member_revenues`;
-- ALTER TABLE `members` DROP COLUMN revenue;
