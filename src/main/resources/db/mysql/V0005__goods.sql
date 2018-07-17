ALTER TABLE `goods` CHANGE `updated_at` `modified_at` LONG;
ALTER TABLE `goods` CHANGE `created_at` `created_at` DATETIME NOT NULL;
ALTER TABLE `goods` CHANGE `modified_at` `modified_at` DATETIME DEFAULT NULL;