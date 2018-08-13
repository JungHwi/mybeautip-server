--
-- Banned words
--
CREATE TABLE `banned_words` (
  `word` VARCHAR(50) NOT NULL,
  `category` TINYINT UNSIGNED DEFAULT '0' COMMENT '1: username, 2: banned word',
  `clean` VARCHAR(50) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('뷰팁', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('어드민', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('관리자', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('관리인', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('매니져', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('매니저', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('테스트', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('태스트', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('체험단', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('공지사항', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('오쿠스', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('요쿠스', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('조쿠스', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('죠쿠스', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('beautip', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('admin', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('dev', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('manager', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('jocoos', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('yocoos', 1, now());
INSERT INTO `banned_words`(`word`, `category`, `created_at`) VALUES('test', 1, now());