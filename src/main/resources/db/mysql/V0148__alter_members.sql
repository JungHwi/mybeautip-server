ALTER TABLE `members` ADD `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' AFTER tag;

# alter table members drop column status;