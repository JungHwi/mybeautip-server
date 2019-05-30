ALTER TABLE `goods_categories` ADD COLUMN `seq` INT DEFAULT 0 AFTER `parent_code`;


--DELETE FROM flyway_schema_history WHERE version='0118';
--ALTER TABLE `goods_categories` DROP COLUMN `seq`;

