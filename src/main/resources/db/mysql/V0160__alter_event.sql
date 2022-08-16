alter table event add column banner_image_file char(16) null comment '배너 섬네일 이미지' after thumbnail_image_url;
alter table event rename column image_url to image_file;
alter table event rename column thumbnail_image_url to thumbnail_image_file;
alter table event rename column share_square_image_url to share_square_image_file;
alter table event rename column share_rectangle_image_url to share_rectangle_image_file;
alter table event_product rename column image_url to image_file;

alter table mybeautip.member_blocks add column status varchar(20) not null comment '블락 여부' after you;

# alter table member_blocks drop column status;
# alter table event drop column banner_image_file;
# alter table event rename column image_file to image_url;
# alter table event rename column thumbnail_image_file to thumbnail_image_url;
# alter table event rename column share_square_image_file to share_square_image_url;
# alter table event rename column share_rectangle_image_file to share_rectangle_image_url;
# alter table event_product rename column image_file to image_url;
#
# delete from flyway_schema_history where version = '0160';
