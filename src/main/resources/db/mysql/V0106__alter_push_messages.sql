ALTER TABLE `push_messages` DROP FOREIGN KEY `fk_push_messages_created_by`;
ALTER TABLE `push_messages` DROP COLUMN `created_by`;


--DELETE FROM flyway_schema_history WHERE version='0106';