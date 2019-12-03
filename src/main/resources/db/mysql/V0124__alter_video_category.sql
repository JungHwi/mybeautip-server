CREATE TABLE `video_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT NOT NULL,
  `category` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_category_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--DELETE FROM flyway_schema_history WHERE version='0124';
--DROP TABLE `video_categories`;

