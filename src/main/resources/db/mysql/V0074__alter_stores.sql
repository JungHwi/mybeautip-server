ALTER TABLE `stores` ADD COLUMN `goods_count` INT DEFAULT 0 AFTER `like_count`;

-- Migration goods count to store
update stores, (select count(*) as goods_count, scm_no from goods group by scm_no) goods set stores.goods_count = goods.goods_count where id = goods.scm_no;


-- DELETE FROM flyway_schema_history WHERE installed_rank = 74;
-- ALTER TABLE `stores` DROP COLUMN `goods_count`;