create table community_category (
    id bigint auto_increment comment '커뮤니티 카테고리 아이디' primary key,
    parent_id bigint comment '부모 카테고리 아이디',
    type varchar(20) not null comment '카테고리 구분',
    sort int not null comment '정렬순서',
    title varchar(20) not null comment '카테고리 제목',
    description varchar(100) not null comment '카테고리 설명',
    hint varchar(100) not null comment '카테고리 힌트'
) comment '커뮤니티 카테고리 정보' charset = utf8mb4;

# drop table community_category;
#
# delete from flyway_schema_history where version = '0159';
