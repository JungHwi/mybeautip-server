--
-- Comment reports
--
CREATE TABLE `comment_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `reason_code` TINYINT UNSIGNED DEFAULT 0,
  `reason` VARCHAR(80) DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_comment_reports_video` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`),
  CONSTRAINT `fk_comment_reports_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `comments` ADD `report_count` BIGINT DEFAULT 0 AFTER like_count;

-- DELETE FROM flyway_schema_history WHERE installed_rank = 141;
-- DROP TABLE `comment_reports`;
-- ALTER TABLE `comments` DROP COLUMN `report_count`;
