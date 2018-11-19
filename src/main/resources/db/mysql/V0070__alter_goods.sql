-- Modify column property
ALTER TABLE `goods` MODIFY COLUMN `like_count` INT NOT NULL DEFAULT 0 AFTER `video_url`;

-- Add new features
ALTER TABLE `goods` ADD COLUMN `state` TINYINT(1) DEFAULT 1 AFTER `goods_no`;