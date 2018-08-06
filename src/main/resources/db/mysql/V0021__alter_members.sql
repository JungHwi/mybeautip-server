--
-- members
--
ALTER TABLE `members` CHANGE `avatar_url` `avatar_url` VARCHAR(200) DEFAULT NULL;


-- Alter date column data type from DATETIME to DATETIME(3)
ALTER TABLE `accounts` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `accounts` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `addresses` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `addresses` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `addresses` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `banners` modify COLUMN `started_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `banners` modify COLUMN `ended_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `banners` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `banners` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `banners` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `devices` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `devices` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `goods` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `goods` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `goods_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `goods_recommendations` modify COLUMN `started_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `goods_recommendations` modify COLUMN `ended_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `goods_recommendations` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `goods_recommendations` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `admin_members` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `facebook_members` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `kakao_members` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `naver_members` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `members` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `members` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `members` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `member_recommendations` modify COLUMN `started_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `member_recommendations` modify COLUMN `ended_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `member_recommendations` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `member_recommendations` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `members_blocks` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `members_followings` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `members_reports` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `notices` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `notices` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `posts` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `posts` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `posts` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `post_comments` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `post_comments` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `post_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `stores` modify COLUMN `created_at` DATETIME(3) NOT NULL;
ALTER TABLE `stores` modify COLUMN `modified_at` DATETIME(3) DEFAULT NULL;
ALTER TABLE `stores` modify COLUMN `deleted_at` DATETIME(3) DEFAULT NULL;

ALTER TABLE `store_likes` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `video_goods` modify COLUMN `created_at` DATETIME(3) NOT NULL;

ALTER TABLE `view_recodings` modify COLUMN `created_at` DATETIME(3) NOT NULL;