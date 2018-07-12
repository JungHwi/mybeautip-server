--
-- goods categories
--
CREATE TABLE `goods_categories` (
  `code` VARCHAR(6) NOT NULL,
  `parent_code` VARCHAR(3) NOT NULL,
  `category_name` VARCHAR(100) NOT NULL,
  `display_on_pc` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
  `display_on_mobile` enum('y', 'n') NOT NULL COMMENT 'or flag, 1:yes 2:no',
  PRIMARY KEY(`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- goods
--
CREATE TABLE `goods` (
  `goods_no` VARCHAR(10) NOT NULL,
  `goods_nm_fl` VARCHAR(1) NOT NULL,
  `goods_nm` VARCHAR(255) NOT NULL,
  `scm_no` INT(10) NOT NULL,
  `cate_cd` VARCHAR(12) NOT NULL,
  `goods_state` VARCHAR(1) NOT NULL,
  `goods_color` VARCHAR(255),
  `brand_cd` VARCHAR(12),
  `maker_nm` VARCHAR(40),
  `origin_nm` VARCHAR(40),
  `make_ymd` VARCHAR(20),
  `launch_ymd` VARCHAR(20),
  `effective_start_ymd` VARCHAR(20),
  `effective_end_ymd` VARCHAR(20),
  `goods_weight` DECIMAL(7,2),
  `stock_fl` VARCHAR(1) NOT NULL,
  `sold_out_fl` VARCHAR(1) NOT NULL,
  `sales_unit` INT(10),
  `min_order_cnt` INT(5),
  `max_order_cnt` INT(5),
  `sales_start_ymd` VARCHAR(20),
  `sales_end_ymd` VARCHAR(20),
  `goods_discount_fl` VARCHAR(1),
  `goods_discount` DECIMAL(12,2),
  `goods_discount_unit` VARCHAR(7),
  `goods_price_string` VARCHAR(60),
  `goods_price` DECIMAL(12,2),
  `fixed_price` DECIMAL(12,2),
  `delivery_sno` INT(10),
  `short_description` VARCHAR(255),
  `goods_description` MEDIUMTEXT,
  `goods_description_mobile` MEDIUMTEXT,
  `order_cnt` INT(10),
  `hit_cnt` INT(10),
  `review_cnt` INT(10),
  `add1image_data` VARCHAR(255),
  `add2image_data` VARCHAR(255),
  `list_image_data` VARCHAR(255),
  `main_image_data` VARCHAR(255),
  `magnify_image_data` VARCHAR(255),
  `detail_image_data` VARCHAR(255),
  `reg_dt` VARCHAR(20),
  `mod_dt` VARCHAR(20),
  `created_at` LONG,
  `updated_at` LONG,
  PRIMARY KEY(`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;