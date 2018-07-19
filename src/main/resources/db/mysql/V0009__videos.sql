--
-- Videos
--
CREATE TABLE `videos` (
	id serial,
	video_key VARCHAR(100) NOT NULL,
	type VARCHAR(11) NOT NULL,
	thumbnail_url VARCHAR(200) NOT NULL,
	goods_no VARCHAR(10) NOT NULL,
	member_id BIGINT(20) NOT NULL,
	created_at DATETIME NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;