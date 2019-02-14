ALTER TABLE recommended_motd_bases MODIFY COLUMN base_date datetime(3) DEFAULT NULL;
UPDATE recommended_motd_bases AS b, recommended_motds AS m SET b.base_date = m.started_at WHERE b.id = m.base_id;

-- alter table recommended_motd_bases modify column base_date date default null;
-- DELETE FROM flyway_schema_history WHERE version='0100';