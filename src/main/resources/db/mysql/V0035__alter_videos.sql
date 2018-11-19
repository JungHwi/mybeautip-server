--
-- Videos
--
ALTER TABLE `videos` MODIFY `like_count` INT NOT NULL DEFAULT 0 after `comment_count`;
ALTER TABLE `videos` ADD COLUMN `view_count` INT NOT NULL DEFAULT 0 after `video_key`;
ALTER TABLE `videos` ADD COLUMN `heart_count` INT NOT NULL DEFAULT 0 after `video_key`;
ALTER TABLE `videos` ADD COLUMN `watch_count` INT NOT NULL DEFAULT 0 after `video_key`;
ALTER TABLE `videos` ADD COLUMN `data` VARCHAR(2000) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `duration` INT NOT NULL DEFAULT 0 after `video_key`;
ALTER TABLE `videos` ADD COLUMN `chat_room_id` VARCHAR(200) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `thumbnail_url` VARCHAR(200) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `url` VARCHAR(400) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `content` VARCHAR(400) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `title` VARCHAR(100) after `video_key`;
ALTER TABLE `videos` ADD COLUMN `visibility` VARCHAR(10) NOT NULL after `video_key`;
ALTER TABLE `videos` ADD COLUMN `state` VARCHAR(20) NOT NULL after `video_key`;
ALTER TABLE `videos` ADD COLUMN `type` VARCHAR(11) NOT NULL after `video_key`;


--
-- Insert Admin members
--
INSERT INTO `members` (username, created_at, modified_at) values ('mybeautip.flipflop', now(), now());

-- Password is encoed by BCryptPasswordEncoder
INSERT INTO `admin_members` (admin_id, password, member_id, created_at)
values ('mybeautip.flipflop', '$2a$10$1aIM4MHeFu.LTi0yDOIbPOoMgnwp8RK7fzYyxHJ0httG2m17BDSwO', (select id from members where username='mybeautip.flipflop'), now());


INSERT INTO `members` (username, created_at, modified_at) values ('mybeautip.s3', now(), now());

-- Password is encoed by BCryptPasswordEncoder
INSERT INTO `admin_members` (admin_id, password, member_id, created_at)
values ('mybeautip.s3', '$2a$10$1aIM4MHeFu.LTi0yDOIbPOoMgnwp8RK7fzYyxHJ0httG2m17BDSwO', (select id from members where username='mybeautip.s3'), now());