ALTER TABLE `goods` MODIFY COLUMN `all_cd` VARCHAR(100) DEFAULT NULL;

--DELETE FROM flyway_schema_history WHERE version='0121';
--ALTER TABLE `goods` MODIFY COLUMN `all_cd` VARCHAR(50) DEFAULT NULL;