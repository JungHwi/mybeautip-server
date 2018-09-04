--
-- members
--
CREATE TABLE `members` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) DEFAULT NULL,
  `avatar_url` VARCHAR(200) DEFAULT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  `coin` INT DEFAULT 0,
  `intro` VARCHAR(200) DEFAULT NULL,
  `link` TINYINT UNSIGNED DEFAULT '0' COMMENT 'or flag, 1:facebook 2:naver 4:kakao',
  `follower_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `following_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `video_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- facebook members
--
CREATE TABLE `facebook_members` (
  `facebook_id` VARCHAR(20) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`facebook_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- kakao members
--
CREATE TABLE `kakao_members` (
  `kakao_id` VARCHAR(30) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
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
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`naver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- admin members
--
CREATE TABLE `admin_members` (
  `admin_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Insert admin account
--
INSERT INTO `members` (username, created_at, modified_at) values ('mybeautip.tv', now(), now());

-- Password is encoed by BCryptPasswordEncoder
INSERT INTO `admin_members` (admin_id, password, member_id, created_at)
values ('mybeautip.tv', '$2a$10$1aIM4MHeFu.LTi0yDOIbPOoMgnwp8RK7fzYyxHJ0httG2m17BDSwO', (select id from members where username='mybeautip.tv'), now());

--
-- members reports
--
CREATE TABLE `members_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `reason` VARCHAR(400) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_reports` (`me`, `you`),
  CONSTRAINT `fk_reports_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_reports_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- members followings
--
CREATE TABLE `members_followings` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_followings` (`me`, `you`),
  CONSTRAINT `fk_followings_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_followings_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- members blocks
--
CREATE TABLE `members_blocks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_block` (`me`, `you`),
  CONSTRAINT `fk_block_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_block_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- goods categories
--
CREATE TABLE `goods_categories` (
  `code` VARCHAR(6) NOT NULL,
  `parent_code` VARCHAR(3) NOT NULL,
  `category_name` VARCHAR(100) NOT NULL,
  `display_on_pc` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
  `display_on_mobile` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
  `thumbnail_url` VARCHAR(255),
  PRIMARY KEY(`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- goods
--
CREATE TABLE `goods` (
  `goods_no` VARCHAR(10) NOT NULL,
  `goods_nm_fl` VARCHAR(1) NOT NULL,
  `goods_nm` VARCHAR(255) NOT NULL,
  `scm_no` INT(10) NOT NULL,
  `cate_cd` VARCHAR(12) NOT NULL,
  `all_cd` VARCHAR(50) NULL DEFAULT NULL,
  `goods_state` VARCHAR(1) NOT NULL,
  `goods_color` VARCHAR(255),
  `brand_cd` VARCHAR(12),
  `maker_nm` VARCHAR(40),
  `origin_nm` VARCHAR(40),
  `make_ymd` VARCHAR(20),
  `launch_ymd` VARCHAR(20),
  `effective_start_ymd` VARCHAR(20),
  `effective_end_ymd` VARCHAR(20),
  `goods_weight` DECIMAL(7,2),
  `stock_fl` VARCHAR(1) NOT NULL,
  `sold_out_fl` VARCHAR(1) NOT NULL,
  `sales_unit` INT(10),
  `min_order_cnt` INT(5),
  `max_order_cnt` INT(5),
  `sales_start_ymd` VARCHAR(20),
  `sales_end_ymd` VARCHAR(20),
  `goods_discount_fl` VARCHAR(1),
  `goods_discount` INT NOT NULL DEFAULT 0,
  `goods_discount_unit` VARCHAR(7),
  `goods_price_string` VARCHAR(60),
  `goods_price` INT NOT NULL DEFAULT 0,
  `fixed_price` INT NOT NULL DEFAULT 0,
  `delivery_sno` INT(10),
  `short_description` VARCHAR(255),
  `goods_description` MEDIUMTEXT,
  `goods_description_mobile` MEDIUMTEXT,
  `order_cnt` INT(10),
  `hit_cnt` INT(10),
  `review_cnt` INT(10),
  `add1image_data` VARCHAR(255),
  `add2image_data` VARCHAR(255),
  `list_image_data` VARCHAR(255),
  `main_image_data` VARCHAR(255),
  `magnify_image_data` VARCHAR(255),
  `detail_image_data` VARCHAR(255),
  `goods_must_info` TEXT DEFAULT NULL,
  `option_fl` VARCHAR(1) DEFAULT NULL,
  `option_name` VARCHAR(255) DEFAULT NULL,
  `video_url` VARCHAR(200) NULL DEFAULT NULL,
  `reg_dt` VARCHAR(20),
  `mod_dt` VARCHAR(20),
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL ,
  `like_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY(`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Goods likes
--
CREATE TABLE `goods_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_goods_likes` (`created_by`, `goods_no`),
  CONSTRAINT `fk_goods_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_goods_likes_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- addresses
--
CREATE TABLE `addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_by` BIGINT NOT NULL,
  `base` TINYINT NOT NULL DEFAULT 0 COMMENT '1:primary 0:extra',
  `title` VARCHAR(20) NOT NULL,
  `recipient` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `zip_no` VARCHAR(10) NOT NULL,
  `road_addr_part1` VARCHAR(255) NOT NULL,
  `road_addr_part2` VARCHAR(255) NOT NULL,
  `jibun_addr` VARCHAR(255) NOT NULL,
  `detail_address` VARCHAR(100) NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_addresses_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- devices
--
CREATE TABLE `devices` (
  `id` VARCHAR(500) CHARACTER SET ascii NOT NULL,
  `arn` VARCHAR(128) NOT NULL,
  `os` VARCHAR(10) NOT NULL,
  `os_version` VARCHAR(10) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `language` VARCHAR(4) NOT NULL,
  `timezone` VARCHAR(40) NOT NULL,
  `app_version` VARCHAR(10) NOT NULL,
  `pushable` TINYINT DEFAULT 1,
  `created_by` BIGINT DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_devices_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- notices
--
CREATE TABLE `notices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(30) NOT NULL,
  `os` VARCHAR(10) NOT NULL,
  `message` VARCHAR(500) NOT NULL,
  `min_version` VARCHAR(10) NOT NULL,
  `max_version` VARCHAR(10) NOT NULL,
  `created_by` BIGINT DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- accounts
--
CREATE TABLE `accounts` (
  `member_id` BIGINT NOT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  `bank_name` VARCHAR(50) DEFAULT NULL,
  `bank_account` VARCHAR(50) DEFAULT NULL,
  `bank_depositor` VARCHAR(50) DEFAULT NULL,
  `validity` TINYINT NOT NULL DEFAULT 1 COMMENT '0:invalid, 1: ok',
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`member_id`),
  CONSTRAINT `fk_accounts_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Videos
--
CREATE TABLE `videos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_key` VARCHAR(10) NOT NULL,
  `type` VARCHAR(11) NOT NULL,
  `thumbnail_url` VARCHAR(200) NOT NULL,
  `comment_count` INT NOT NULL DEFAULT 0,
  `related_goods_count` TINYINT DEFAULT 0,
  `related_goods_thumbnail_url` VARCHAR(255),
  `owner` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_videos` (`id`, `video_key`),
  CONSTRAINT `fk_videos_owner` FOREIGN KEY (`owner`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Video Comments
--
CREATE TABLE `video_comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT NOT NULL,
  `comment` VARCHAR(500) DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_video_comments_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_video_comments_videos` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Video with Goods
--
CREATE TABLE `video_goods` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  video_id BIGINT NOT NULL,
  goods_no VARCHAR(10) NOT NULL,
  created_at DATETIME(3) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT `fk_video_goods_videos` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`),
  CONSTRAINT `fk_video_goods_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Stores
--
CREATE TABLE `stores` (
  `id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255),
  `image_url` VARCHAR(255),
  `thumbnail_url` VARCHAR(255),
  `like_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Store likes
--
CREATE TABLE `store_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_store_likes` (`created_by`, `store_id`),
  CONSTRAINT `fk_store_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_store_likes_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- member recommendations
--
CREATE TABLE `member_recommendations` (
  `member_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `started_at` DATETIME(3) DEFAULT NULL,
  `ended_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`member_id`),
  CONSTRAINT `fk_member_recommendations_members` FOREIGN KEY (`member_id`) REFERENCES `members`
  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- goods recommendations
--
CREATE TABLE `goods_recommendations` (
  `goods_no` VARCHAR(10) NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `started_at` DATETIME(3) DEFAULT NULL,
  `ended_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`goods_no`),
  CONSTRAINT `fk_goods_recommendations_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods`
  (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- MOTD Recommendations
--
CREATE TABLE `motd_recommendations` (
  `video_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `started_at` DATETIME(3) DEFAULT NULL,
  `ended_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Posts
--
CREATE TABLE `posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(32) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  `thumbnail_url` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: trend, 2: card news',
  `progress` int DEFAULT 0,
  `view_count` INT NOT NULL DEFAULT 0,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `goods` TINYINT NOT NULL DEFAULT 0 COMMENT '0: no goods, 1: exist goods',
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_posts_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post contents
--
CREATE TABLE `post_contents` (
  `post_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: text, 2: image, 4: video',
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY(`post_id`, `seq`),
  CONSTRAINT `fk_post_contents` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post goods
--
CREATE TABLE `post_goods` (
  `post_id` BIGINT NOT NULL,
  `seq` INT NOT NULL,
  `goods_no` VARCHAR(10) NOT NULL,
  PRIMARY KEY(`post_id`, `seq`),
  CONSTRAINT `fk_post_goods` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post views
--
CREATE TABLE `view_recodings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `item_id` VARCHAR(10) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: post, 2: goods',
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_viewed_posts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post likes
--
CREATE TABLE `post_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_post_likes` (`created_by`, `post_id`),
  CONSTRAINT `fk_post_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_post_likes_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Post comments
--
CREATE TABLE `post_comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `comment` VARCHAR(500) DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `comment_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_post_comments_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_post_comments_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

--
-- banners
--
CREATE TABLE `banners` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(22) NOT NULL,
  `description` VARCHAR(34) NOT NULL,
  `thumbnail_url` VARCHAR(255) NOT NULL,
  `category` TINYINT NOT NULL DEFAULT 1 COMMENT '1: post, 2: goods, 3: goods list, 4: video',
  `seq` INT NOT NULL,
  `link` VARCHAR(255) NOT NULL,
  `started_at` DATETIME(3) DEFAULT NULL,
  `ended_at` DATETIME(3) DEFAULT NULL,
  `view_count` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  `deleted_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_banners_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Carts
--
CREATE TABLE `carts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `option_no` INT NOT NULL DEFAULT 0,
  `scm_no` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_carts_goods_option` (`goods_no`, `option_no`),
  CONSTRAINT `fk_carts_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_carts_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Banned words
--
CREATE TABLE `banned_words` (
  `word` VARCHAR(50) NOT NULL,
  `category` TINYINT UNSIGNED DEFAULT '0' COMMENT '1: username, 2: banned word',
  `clean` VARCHAR(50) DEFAULT NULL,
  `created_at` DATETIME(3) NOT NULL,
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

--
-- Goods OptionData
--
CREATE TABLE `goods_options` (
  `sno` BIGINT NOT NULL,
  `goods_no` INT NOT NULL,
  `option_no` TINYINT UNSIGNED NOT NULL,
  `option_value1` VARCHAR(40) DEFAULT NULL,
  `option_price` INT NOT NULL DEFAULT 0,
  `option_cost_price` INT NOT NULL DEFAULT 0,
  `stock_cnt` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL,
  `modified_at` DATETIME(3) DEFAULT NULL,
  PRIMARY KEY(`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- orders
--
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `number` BIGINT NOT NULL,
  `goods_count` INT NOT NULL,
  `price` BIGINT NOT NULL,
  `point` INT DEFAULT 0,
  `method` VARCHAR(20) DEFAULT "card",
  `status` VARCHAR(20) DEFAULT NULL COMMENT 'ordered, paid, ...',
  `video_id` BIGINT DEFAULT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_orders_member` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order delivery
--
CREATE TABLE `order_deliveries` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `recipient` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `zip_no` VARCHAR(10) NOT NULL,
  `road_addr_part1` VARCHAR(255) NOT NULL,
  `road_addr_part2` VARCHAR(255) NOT NULL,
  `jibun_addr` VARCHAR(255) NOT NULL,
  `detail_address` VARCHAR(100) NOT NULL,
  `carrier` VARCHAR(20) DEFAULT NULL,
  `invoice` VARCHAR(30) DEFAULT NULL,
  `carrier_message` VARCHAR(50) DEFAULT "",
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_deliveries_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order payments
--
CREATE TABLE `order_payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `payment_id` VARCHAR(30) DEFAULT NULL,
  `price` BIGINT NOT NULL,
  `method` VARCHAR(20) NOT NULL COMMENT 'iamport',
  `state` TINYINT NOT NULL DEFAULT 0 COMMENT '0:created, 1: stopped, 2: failed, ...',
  `message` VARCHAR(255) DEFAULT NULL,
  `receipt` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- order purchases
--
CREATE TABLE `order_purchases` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `goods_no` VARCHAR(10) NOT NULL,
  `goods_price` BIGINT NOT NULL,
  `option_id` VARCHAR(10) NOT NULL,
  `option_value` VARCHAR(40) NOT NULL,
  `option_price` BIGINT DEFAULT NULL,
  `quantity` BIGINT NOT NULL,
  `total_price` BIGINT DEFAULT NULL,
  `video_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `fk_purchases_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_purchases_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

