--
-- Recommended motd bases
--
CREATE TABLE `recommended_motd_bases` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `base_date` DATE NOT NULL,
  `created_at` DATETIME NOT NULL,
  `motd_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO recommended_motd_bases (motd_count, base_date, created_at) SELECT count(started_at) AS count, DATE_FORMAT(started_at, '%Y-%m-%d'), now() AS base_date FROM recommended_motds GROUP BY started_at;

ALTER TABLE `recommended_motds` ADD COLUMN `base_id` BIGINT NOT NULL AFTER `seq`;
UPDATE recommended_motds, (SELECT * FROM recommended_motd_bases) AS base SET base_id = base.id WHERE DATE_FORMAT(started_at, '%Y-%m-%d') = base.base_date;

ALTER TABLE `recommended_motds` ADD CONSTRAINT `fk_recommended_motds_base` FOREIGN KEY (`base_id`) REFERENCES `recommended_motd_bases` (`id`);

--ALTER TABLE `recommended_motds` DROP FOREIGN KEY `fk_recommended_motds_base`;
--ALTER TABLE `recommended_motds` DROP COLUMN `base_id`;
--DROP TABLE `recommended_motd_bases`;
--DELETE FROM `flyway_schema_history` WHERE `installed_rank` = 79;
