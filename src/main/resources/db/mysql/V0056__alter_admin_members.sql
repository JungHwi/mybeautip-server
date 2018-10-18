
ALTER TABLE `admin_members` CHANGE COLUMN `admin_id` `email` VARCHAR(50) NOT NULL;

ALTER TABLE `admin_members` ADD COLUMN `store_id` BIGINT DEFAULT NULL AFTER `member_id`;

--
-- Migration store to store members
--
insert into members (username, avatar_url, link, created_at)
select name, thumbnail_url, 8, now() from stores;

-- Default password is akdlqbxlq@@123
insert into admin_members (email, password, member_id, store_id, created_at)
select name, '$2a$10$qRBfbYVAnTR3irx9Vv9fA.wD6kAjO4ANfdt6loNM/ypy3AsGP6Kvm', m.id, s.id, now() from stores s, members m where s.name = m.username;


-- delete from members where link = 8;
-- delete from admin_members where store_id is not null;

-- DELETE FROM `flyway_schema_history` WHERE `installed_rank` = 56;
-- ALTER TABLE `admin_members` CHANGE COLUMN `email` `admin_id` VARCHAR(50) NOT NULL;
-- ALTER TABLE `admin_members` DROP COLUMN `store_id`;