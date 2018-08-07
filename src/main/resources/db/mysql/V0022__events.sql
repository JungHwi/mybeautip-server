--
-- Post winners
--
CREATE TABLE `post_winners` (
  `post_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  PRIMARY KEY(`post_id`, `member_id`),
  CONSTRAINT `fk_event_winners_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `fk_event_winners_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `posts` ADD COLUMN `progress` int DEFAULT 0 after `category`;

