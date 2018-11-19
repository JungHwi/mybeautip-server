--
-- members
--
CREATE TABLE `members` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) DEFAULT NULL,
  `avatar_url` VARCHAR(100) DEFAULT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  `coin` INT DEFAULT 0,
  `intro` VARCHAR(200) DEFAULT NULL,
  `link` TINYINT UNSIGNED DEFAULT '0' COMMENT 'or flag, 1:facebook 2:naver 4:kakao',
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- facebook members
--
CREATE TABLE `facebook_members` (
  `facebook_id` VARCHAR(20) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`facebook_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- kakao members
--
CREATE TABLE `kakao_members` (
  `kakao_id` VARCHAR(30) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`kakao_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- naver members
--
CREATE TABLE `naver_members` (
  `naver_id` VARCHAR(30) NOT NULL,
  `nickname` VARCHAR(30) DEFAULT NULL,
  `gender` VARCHAR(1) DEFAULT NULL,
  `age` VARCHAR(10) DEFAULT NULL,
  `birthday` VARCHAR(10) DEFAULT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`naver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

