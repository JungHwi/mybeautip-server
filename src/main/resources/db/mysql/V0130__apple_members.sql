--
-- apple members
--
CREATE TABLE `apple_members` (
  `apple_id` VARCHAR(48) NOT NULL,
  `email` VARCHAR(256) NOT NULL,
  `name` VARCHAR(256) DEFAULT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`apple_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Change link level of the store from 8 to 32
UPDATE `members` SET `link` = 32 WHERE `link` = 8;

-- DELETE FROM flyway_schema_history WHERE version='0130';
-- DROP TABLE `apple_members`;

