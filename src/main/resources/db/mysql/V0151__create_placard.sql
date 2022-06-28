create table placard (
     id bigint auto_increment comment '플래카드 아이디' primary key,
     status varchar(20) not null comment '상태',
     link_type varchar(20) not null comment '이동 방식 구분',
     link_argument varchar(300) comment '이동 방식에 따른 argument',
     image_url varchar(200) comment 'placard image url',
     description varchar(50) comment '플래카드 설명',
     started_at datetime not null comment '플래카드 게시 시작일시',
     ended_at datetime not null comment '플래카드 게시 종료일시',
     created_at datetime not null comment '생성일'
) comment '플래카드 정보' charset = utf8mb4;

create index idx_placard_tab_status on placard (status);

# drop table placard;
#
# delete from flyway_schema_history where version = '0151';