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

--
-- members reports
--
CREATE TABLE `members_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `me` BIGINT(20) NOT NULL,
  `you` BIGINT(20) NOT NULL,
  `reason` VARCHAR(400) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_reports` (`me`, `you`),
  CONSTRAINT `fk_reports_me` FOREIGN KEY (`me`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_reports_you` FOREIGN KEY (`you`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- goods categories
--
CREATE TABLE `goods_categories` (
  `code` VARCHAR(6) NOT NULL,
  `parent_code` VARCHAR(3) NOT NULL,
  `category_name` VARCHAR(100) NOT NULL,
  `thumbnail_url` VARCHAR(255),
  `display_on_pc` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
  `display_on_mobile` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
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
  `goods_discount` DECIMAL(12,2),
  `goods_discount_unit` VARCHAR(7),
  `goods_price_string` VARCHAR(60),
  `goods_price` DECIMAL(12,2),
  `fixed_price` DECIMAL(12,2),
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
  `reg_dt` VARCHAR(20),
  `mod_dt` VARCHAR(20),
  `like_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL ,
  PRIMARY KEY(`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Video with Goods
--
CREATE TABLE `video_goods` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  video_key VARCHAR(100) NOT NULL,
  type VARCHAR(11) NOT NULL,
  thumbnail_url VARCHAR(200) NOT NULL,
  goods_no VARCHAR(10) NOT NULL,
  member_id BIGINT(20) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- stores
--
CREATE TABLE `stores` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255),
  `image_url` VARCHAR(255),
  `thumbnail_url` VARCHAR(255),
  `like_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- store likes
--
CREATE TABLE `store_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
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
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
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
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME DEFAULT NULL,
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  PRIMARY KEY(`goods_no`),
  CONSTRAINT `fk_goods_recommendations_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods`
  (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Goods likes
--
CREATE TABLE `goods_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `goods_no` VARCHAR(10) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_goods_likes` (`created_by`, `goods_no`),
  CONSTRAINT `fk_goods_likes_created_by` FOREIGN KEY (`created_by`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_goods_likes_goods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


