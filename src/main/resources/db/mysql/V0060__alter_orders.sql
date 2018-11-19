ALTER TABLE `orders` ADD COLUMN `expected_point` INT DEFAULT 0 AFTER `method`;
ALTER TABLE `orders` ADD COLUMN `shipping_amount` INT DEFAULT 0 AFTER `method`;
ALTER TABLE `orders` ADD COLUMN `deduction_amount` INT DEFAULT 0 AFTER `method`;
ALTER TABLE `orders` ADD COLUMN `price_amount` INT DEFAULT 0 AFTER `method`;