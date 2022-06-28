create table popup (
    id bigint auto_increment comment '팝업 아이디' primary key,
    type varchar(20) not null comment '팝업 시점 구분',
    status varchar(20) not null comment '상태',
    image_url varchar(200) comment '팝업 이미지 Url',
    description varchar(50) comment '팝업 설명',
    started_at datetime not null comment '팝업 시작일시',
    ended_at datetime not null comment '팝업 종료일시',
    created_at datetime not null comment '생성일'
) comment '팝업 정보' charset = utf8mb4;

create table popup_button (
    id bigint auto_increment comment '팝업 버튼 아이디' primary key,
    popup_id int not null comment '팝업 아이디',
    name varchar(20) comment '버튼명',
    link_type varchar(20) comment '이동 방식 구분',
    link_argument varchar(300) comment '이동 방식에 따른 argument'
) comment '팝업 버튼 정보' charset = utf8mb4;

# drop table popup;
# drop table popup_button;
#
# delete from flyway_schema_history where version = '0152';