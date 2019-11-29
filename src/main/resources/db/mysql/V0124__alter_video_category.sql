CREATE TABLE `video_categories` (
  `video_id` BIGINT NOT NULL,
  `category` TINYINT(1) DEFAULT 0,
  PRIMARY KEY(`video_id`, `category`),
  CONSTRAINT `fk_category_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--DELETE FROM flyway_schema_history WHERE version='0124';
--DROP TABLE `video_categories`;

