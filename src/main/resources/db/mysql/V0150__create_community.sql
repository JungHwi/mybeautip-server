-- COMMUNITY
create table community_category (
    id bigint auto_increment comment '커뮤니티 카테고리 아이디' primary key,
    parent_id bigint comment '부모 카테고리 아이디',
    type varchar(20) not null comment '카테고리 구분',
    sort int not null comment '정렬순서',
    title varchar(20) not null comment '카테고리 제목',
    description varchar(100) not null comment '카테고리 설명',
    hint varchar(100) not null comment '카테고리 힌트'
) comment '커뮤니티 카테고리 정보' charset = utf8mb4;

create table community (
    id bigint auto_increment comment '아이디' primary key,
    category_id bigint not null comment '카테고리 아이디',
    event_id bigint comment '이벤트 아이디',
    is_win boolean comment '이벤트 당첨 여부',
    member_id bigint not null comment '작성자 아이디',
    status varchar(20) not null comment '상태',
    title varchar(200) comment '제목',
    contents text not null comment '내용',
    view_count int not null default 0 comment '조회수',
    like_count int not null default 0 comment '좋아요수',
    comment_count int not null default 0 comment '댓글수',
    report_count int not null default 0 comment '신고수',
    sorted_at datetime not null comment '정렬용 일시',
    modified_at datetime not null comment '수정 일시',
    created_at datetime not null comment '생성 일시'
) comment '커뮤니티 정보' charset = utf8mb4;

create index idx_community_sorted_at_member_id on community(sorted_at, member_id);
create index idx_community_sorted_at_category_id on community(sorted_at, category_id, event_id, is_win);

create table community_file (
    id bigint auto_increment comment '아이디' primary key,
    community_id bigint not null comment '커뮤니티 아이디',
    file char(16) not null comment '파일명'
) comment '커뮤니티 파일 정보' charset = utf8mb4;

create index idx_community_file_community_id on community_file(community_id);

create table community_like (
    id bigint auto_increment comment '아이디' primary key,
    community_id bigint not null comment '커뮤니티 아이디',
    member_id bigint not null comment '유저 아이디',
    is_like boolean not null comment '좋아요 여부',
    modified_at datetime not null comment '수정일시',
    created_at datetime not null comment '작성일시'
) comment '커뮤니티 좋아요 정보' charset = utf8mb4;

create index idx_community_like_community_id on community_like(community_id, member_id);
create index idx_community_like_member_id on community_like(member_id, community_id);

create table community_report (
    id bigint auto_increment comment '아이디' primary key,
    community_id bigint not null comment '커뮤니티 아이디',
    member_id bigint not null comment '유저 아이디',
    is_report boolean not null comment '신고 여부',
    description varchar(200) comment '신고 사유'
) comment '커뮤니티 신고 정보' charset = utf8mb4;

create index idx_community_report_community_id on community_report(community_id, member_id);
create index idx_community_report_member_id on community_report(member_id, community_id);

-- Community Comment
create table community_comment (
    id bigint auto_increment comment '커뮤니티 대/댓글 아이디' primary key,
    category_id bigint not null comment '카테고리 아이디',
    community_id bigint not null comment '커뮤니티 아이디',
    parent_id bigint comment '부모 댓글 아이디',
    member_id bigint not null comment '작성자 아이디',
    status varchar(20) not null comment '상태',
    contents text not null comment '내용',
    like_count int not null default 0 comment '좋아요수',
    comment_count int not null default 0 comment '댓글수',
    report_count int not null default 0 comment '신고수',
    modified_at datetime not null comment '수정 일시',
    created_at datetime not null comment '생성 일시'
) comment '커뮤니티 대/댓글 정보' charset = utf8mb4;

create index idx_community_comment_community_id on community_comment(community_id, created_at);

-- Community Comment Like
create table community_comment_like (
    id bigint auto_increment comment '아이디' primary key,
    comment_id bigint not null comment '커뮤니티 댓글 아이디',
    member_id bigint not null comment '유저 아이디',
    is_like boolean not null comment '좋아요 여부'
) comment '커뮤니티 댓글 좋아요 정보' charset = utf8mb4;

create index idx_community_comment_like_community_id on community_comment_like(comment_id, member_id);
create index idx_community_comment_like_member_id on community_comment_like(member_id, comment_id);

-- Community Comment Report
create table community_comment_report (
    id bigint auto_increment comment '아이디' primary key,
    comment_id bigint not null comment '커뮤니티 댓글 아이디',
    member_id bigint not null comment '유저 아이디',
    is_report boolean not null comment '신고 여부',
    description varchar(200) comment '신고 사유'
) comment '커뮤니티 댓글 신고 정보' charset = utf8mb4;

create index idx_community_comment_report_community_id on community_comment_report(comment_id, member_id);
create index idx_community_comment_report_member_id on community_comment_report(member_id, comment_id);

-- drop table community_category;
-- drop table community;
-- drop table community_file;
-- drop table community_like;
-- drop table community_report;
-- drop table community_comment;
-- drop table community_comment_like;
-- drop table community_comment_report;

-- delete from flyway_schema_history where version = '0150';