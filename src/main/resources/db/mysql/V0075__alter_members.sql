ALTER TABLE `members` ADD COLUMN `report_count` INT DEFAULT 0 AFTER `following_count`;

-- Migration goods count to store
update members, (select count(*) as report_count, you from member_reports group by you) reports set members.report_count = reports.report_count where members.id = reports.you;


-- DELETE FROM flyway_schema_history WHERE installed_rank = 75;
-- ALTER TABLE `members` DROP COLUMN `report_count`;