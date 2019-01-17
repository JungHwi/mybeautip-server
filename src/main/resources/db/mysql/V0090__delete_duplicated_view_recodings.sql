--
-- Delete duplicated view logs
--
DELETE FROM view_recodings WHERE id NOT IN (SELECT * FROM(SELECT MIN(id) FROM view_recodings GROUP BY item_id, category, created_by) AS temp);

