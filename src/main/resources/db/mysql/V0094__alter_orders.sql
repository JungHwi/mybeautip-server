ALTER TABLE `order_payments` ADD COLUMN `card_name` VARCHAR(30) DEFAULT NULL AFTER `method`;

--delete from flyway_schema_history where version='0094';
--alter table order_payments drop column card_name;