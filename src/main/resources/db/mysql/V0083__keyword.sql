--
-- Search History
--
CREATE TABLE `search_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL, -- 0: member, 1: video, 2: goods, 3: post
  `is_guest` TINYINT(1) NOT NULL,
  `created_by` BIGINT,
  `count` INT UNSIGNED NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_search_history_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Keywords
--
CREATE TABLE `keywords` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(255) NOT NULL,
  `count` INT UNSIGNED NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Tag History
--
CREATE TABLE `tag_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `tag` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL, -- 0: member, 1: video, 2: comment, 3: post
  `is_guest` TINYINT(1) NOT NULL,
  `created_by` BIGINT,
  `count` INT UNSIGNED NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_tag_history_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `recommended_keywords` CHANGE COLUMN `member_id` `member` BIGINT DEFAULT NULL;
ALTER TABLE `recommended_keywords` CHANGE COLUMN `tag_id` `tag` BIGINT DEFAULT NULL;
ALTER TABLE `recommended_keywords` DROP FOREIGN KEY `fk_keyword_recommendations_members`;
ALTER TABLE `recommended_keywords` DROP FOREIGN KEY `fk_keyword_recommendations_tags`;
ALTER TABLE `recommended_keywords` ADD CONSTRAINT `fk_keyword_recommendations_members` FOREIGN KEY (`member`) REFERENCES `members` (`id`);
ALTER TABLE `recommended_keywords` ADD CONSTRAINT `fk_keyword_recommendations_tags` FOREIGN KEY (`tag`) REFERENCES `tags` (`id`);