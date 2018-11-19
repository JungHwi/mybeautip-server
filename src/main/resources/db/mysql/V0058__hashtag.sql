ALTER TABLE `tags` MODIFY `name` VARCHAR(255) NOT NULL UNIQUE;

ALTER TABLE `videos` ADD COLUMN `tag_info` TEXT AFTER `owner`;
ALTER TABLE `posts` ADD COLUMN `tag_info` TEXT AFTER `created_by`;
