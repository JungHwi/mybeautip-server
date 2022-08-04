alter table member_points add column activity_type varchar(50) not null comment '활동 포인트 타입' after event_id;
alter table member_points add column activity_domain_id bigint not null comment '활동 포인트 도메인 아이디' after activity_type;

alter table member_point_details add column activity_type varchar(50) not null comment '활동 포인트 타입' after event_id;

alter table video_scraps add column status varchar(20) not null comment '영상 스크랩 여부' after created_by;
alter table video_likes add column status varchar(20) not null comment '영상 좋아요 여부' after created_by;
alter table comment_likes add column status varchar(20) not null comment '댓글 좋아요 여부' after created_by;

# alter table member_points drop column activity_type;
# alter table member_points drop column activity_domain_id;
#
# alter table member_point_details drop column activity_type;
#
# alter table video_scraps drop column status;
# alter table video_likes drop column status;
# alter table comment_likes drop column status;
#
# delete from flyway_schema_history where version = '0158';
