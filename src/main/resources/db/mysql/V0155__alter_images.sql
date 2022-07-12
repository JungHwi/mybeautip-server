alter table event add column thumbnail_image_url varchar(200) not null comment '섬네일 이미지' after image_url;
alter table event add column share_square_image_url varchar(200) not null comment '공유용 정사각형 이미지' after thumbnail_image_url;
alter table event add column share_rectangle_image_url varchar(200) not null comment '공유용 직사각형 이미지' after share_square_image_url;

alter table placard drop column image_url;

create table placard_detail (
    id bigint auto_increment comment '플래카드 이미지 아이디' primary key,
    placard_id bigint not null comment '플래카드 아이디',
    tab_type varchar(20) not null comment 'tab 타입',
    image_url varchar(200) not null comment '이미지 URL'
) comment '플래카드 이미지 정보' charset = utf8mb4;


# alter table event drop column thumbnail_image_url;
# alter table event drop column share_square_image_url;
# alter table event drop column share_rectangle_image_url;
#
# alter table placard drop column tab_type;
# alter table placard add column image_url varchar(200) not null after link_argument;
# drop table placard_detail;

# delete from flyway_schema_history where version = '0155';
