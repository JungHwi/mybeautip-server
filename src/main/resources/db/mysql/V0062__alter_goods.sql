ALTER TABLE `goods` MODIFY COLUMN `delivery_sno` INT(10) NOT NULL DEFAULT 0;
ALTER TABLE `goods` ADD COLUMN `delivery_method` VARCHAR(40) NOT NULL DEFAULT '' AFTER `delivery_sno`;
ALTER TABLE `goods` ADD COLUMN `delivery_fix_fl` VARCHAR(6) NOT NULL DEFAULT '' AFTER `delivery_sno`;
