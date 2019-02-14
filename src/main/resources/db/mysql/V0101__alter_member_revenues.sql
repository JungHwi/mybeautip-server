ALTER TABLE `member_revenues` DROP COLUMN `confirmed_at`;
ALTER TABLE `member_revenues` ADD COLUMN `confirmed` TINYINT(1) DEFAULT NULL;
UPDATE `member_revenues` SET confirmed=0;


-- alter table member_revenues add column confirmed_at datetime(3) default null;
-- alter table member_revenues drop column confirmed;
-- DELETE FROM flyway_schema_history WHERE version='0101';