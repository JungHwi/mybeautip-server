ALTER TABLE `posts` ADD `type_id` TINYINT DEFAULT 0 AFTER `category`;

ALTER TABLE `posts` ADD `report_count` BIGINT DEFAULT 0 AFTER `comment_count`;

--
-- Post reports
--
CREATE TABLE `post_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `reason_code` TINYINT UNSIGNED DEFAULT 0,
  `reason` VARCHAR(80) DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_post_reports_video` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `fk_post_reports_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post labels
--
CREATE TABLE `post_labels` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(80) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO post_labels(name, created_at, modified_at) values('질문', now(), now());
INSERT INTO post_labels(name, created_at, modified_at) values('일상', now(), now());
INSERT INTO post_labels(name, created_at, modified_at) values('정보', now(), now());
INSERT INTO post_labels(name, created_at, modified_at) values('기타', now(), now());

-- DELETE FROM flyway_schema_history WHERE installed_rank = 143;
-- ALTER TABLE `posts` DROP COLUMN `label_id`;
-- ALTER TABLE `posts` DROP COLUMN `report_count`;
-- DROP TABLE post_labels;
-- DROP TABLE post_reports;
