ALTER TABLE `notifications` MODIFY COLUMN `resource_ids` VARCHAR(200) DEFAULT NULL;

INSERT INTO `notification_customs`
SELECT id, 'system_detail', 'point' FROM `notifications` WHERE type = 'point';

UPDATE `notifications` SET type = 'system_message', image_url = null WHERE type = 'point';



--DELETE FROM flyway_schema_history WHERE version='0116';
--UPDATE `notifications` SET type = 'point' WHERE type = 'system_message';
--DELETE FROM notification_customs WHERE notification_id in (SELECT id FROM `notifications` WHERE type = 'point');

