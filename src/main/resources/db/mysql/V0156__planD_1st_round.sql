rename table notices to health_check;

create table notice (
                        id bigint auto_increment comment '아이디' primary key,
                        status varchar(20) not null comment '상태',
                        is_visible boolean not null comment '노출 여부',
                        is_important boolean null comment '중요',
                        title varchar(200) not null comment '제목',
                        description varchar(1000) not null comment '내용',
                        view_count int not null default 0 comment '조회수',
                        modified_by bigint not null comment '수정자',
                        modified_at datetime not null comment '수정 일시',
                        created_by bigint not null comment '작성자',
                        created_at datetime not null comment '작성 일시'
) comment '공지사항 정보' charset = utf8mb4;

create table notice_file (
                             id bigint auto_increment comment '아이디' primary key,
                             notice_id char(32) not null comment '공지사항 아이디',
                             type varchar(20) not null comment '파일 구분',
                             file char(16) not null comment '파일명'
) comment '공지사항 파일 정보';

alter table community_comment add column file char(16) after contents;
alter table community_comment change column contents contents text comment '내용';
alter table comments add column file char(16) after comment;
alter table community_file add column type varchar(20) after community_id;

# Migration
# update community_file set type='IMAGE';

# rename health_check to notices;
# drop table notice;
# drop table notice_file;
# alter table community_comment drop column file;
# alter table community_comment change column contents contents text not null comment '내용';
# alter table comments drop column file;
# alter table community_file drop column type;
# delete from flyway_schema_history where version = '0156';
