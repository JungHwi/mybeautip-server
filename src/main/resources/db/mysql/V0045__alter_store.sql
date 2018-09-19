ALTER TABLE `stores` ADD COLUMN `as_url` VARCHAR(255) NOT NULL after `thumbnail_url`;
ALTER TABLE `stores` ADD COLUMN `refund_url` VARCHAR(255) NOT NULL after `thumbnail_url`;