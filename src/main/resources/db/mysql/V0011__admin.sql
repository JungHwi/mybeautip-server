--
-- admin members
--
CREATE TABLE `admin_members` (
  `admin_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `member_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY(`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Insert admin account
--
INSERT INTO `members` (username, created_at) values ('mybeautip.tv', now());

-- Password is encoed by BCryptPasswordEncoder
INSERT INTO `admin_members` (admin_id, password, member_id, created_at)
values ('mybeautip.tv', '$2a$10$1aIM4MHeFu.LTi0yDOIbPOoMgnwp8RK7fzYyxHJ0httG2m17BDSwO', (select id from members where username='mybeautip.tv'), now());
