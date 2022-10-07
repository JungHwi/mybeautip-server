create table popup_notice
(
    id bigint auto_increment comment '공지 팝업 아이디 'primary key,
    status varchar(20) not null comment '공지 팝업 상태',
    filename varchar(100) comment '공지 팝업 파일이름',
    link_type varchar(20) comment '공지 팝업 링크 타입',
    link_argument varchar(300) comment '공지 팝업 링크 파라미터',
    started_at datetime comment '공지 팝업 시작일',
    ended_at datetime comment '공지 팝업 종료일',
    created_at datetime not null comment '공지 팝업 생성일'
);

# drop table popup_notice;
-- delete from flyway_schema_history where version = '0152';
