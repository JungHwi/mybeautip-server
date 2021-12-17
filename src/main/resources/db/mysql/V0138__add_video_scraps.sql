--
-- Video scraps
--
CREATE TABLE `video_scraps` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Videos
--
ALTER TABLE `videos` ADD `scrap_count` INT NOT NULL DEFAULT 0 AFTER like_count;


-- DELETE FROM flyway_schema_history WHERE installed_rank = 138;
-- drop table video_scraps;
-- ALTER TABLE `videos` DROP COLUMN `scrap_count`;

