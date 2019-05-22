
UPDATE `member_points` SET expiry_at = DATE_ADD(earned_at, INTERVAL 1 YEAR) WHERE expiry_at is null AND earned_at is not null;


--DELETE FROM flyway_schema_history WHERE version='0115';
