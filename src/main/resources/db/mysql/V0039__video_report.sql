--
-- Video reports
--
CREATE TABLE `video_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `reason` VARCHAR(80) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_video_reports_video` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`),
  CONSTRAINT `fk_video_reports_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
