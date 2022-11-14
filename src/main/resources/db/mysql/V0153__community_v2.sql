create table community_vote
(
    id bigint auto_increment comment '커뮤니티 투표 아이디' primary key,
    community_file_id bigint not null comment '커뮤니티 파일 아이디',
    community_id bigint not null comment '커뮤니티 아이디',
    vote_count int not null default 0 comment '좋아요수'
) comment '커뮤니티 투표 정보 ' charset = utf8mb4;

create table community_vote_member
(
    id bigint auto_increment comment '커뮤니티 투표 아이디' primary key,
    member_id bigint not null comment '멤버 아이디',
    community_id bigint not null comment '커뮤니티 아이디',
    community_vote_id bigint not null comment '커뮤니티 투표 아이디'
) comment '커뮤니티 멤버 투표 정보 ' charset = utf8mb4;

create table member_activity_count
(
    id bigint auto_increment comment 'id' primary key,
    community_count int comment '게시물 작성수',
    community_comment_count int comment '게시물 댓글 작성수',
    video_comment_count int comment '비디오 댓글 작성수',
    created_at datetime not null comment '생성일',
    modified_at datetime not null comment '수정일'
)comment '멤버 활동 카운트 정보' charset = utf8mb4;

create table member_memo
(
    id bigint comment '멤버 id' primary key,
    memo varchar(2000) comment '메모',
    created_at datetime not null comment '생성일',
    modified_at datetime not null comment '수정일'
)comment '멤버 어드민 메모' charset = utf8mb4;

create table scrap
(
    id          bigint auto_increment comment '스크랩 아이디' primary key,
    type        varchar(10) not null comment '스크랩 대상 서비스',
    relation_id bigint      not null comment '스크랩 대상 아이디',
    member_id   bigint      not null comment '회원 아이디',
    is_scrap    tinyint(1)  not null comment '스크랩 여부',
    modified_at datetime    not null comment '수정일시',
    created_at  datetime    not null comment '등록일시'
)comment '스크랩 정보' charset = utf8mb4;


create table video_category (
    id int auto_increment comment '비디오 카테고리 아이디' primary key,
    parent_id int null comment '부모 카테고리 아이디',
    type varchar(10) not null comment '카테고리 구분',
    mask_type varchar(20) comment '카테고리 마스크 구분',
    sort int not null comment '정렬 순서',
    title varchar(20) not null comment '카테고리명',
    description varchar(100) not null comment '카테고리 설명',
    shape_file char(16) not null comment '카테고리 Shape File'
) comment '비디오 카테고리 정보' charset = utf8mb4;


-- 커뮤니티 카테고리 테이블 홈 화면 표시 컬럼 추가
alter table community_category add column is_in_summary boolean after hint;

-- 커뮤니티 테이블 상단 고정 컬럼 추가
alter table community add column is_top_fix boolean after is_win;

-- 플랜카드 테이블 컬럼 추가
alter table placard add column title varchar(50) not null default 'placard title' after link_argument;
alter table placard add column color varchar(20) not null default '#000000' after description;

-- 신고 테이블 컬럼 추가
alter table community_report add column created_at datetime after description;
alter table community_report add column modified_at datetime after description;
alter table community_comment_report add column created_at datetime after description;
alter table community_comment_report add column modified_at datetime after description;
alter table comment_reports add column reported_id bigint after created_by;
alter table community_comment_report add column reported_id bigint after member_id;
alter table community_report add column reported_id bigint after member_id;

-- Video Category
alter table video_categories rename video_category_mapping;

alter table video_category_mapping change column category category_id int not null;

-- EVENT
alter table event add column reservation_at datetime after end_at;

alter table event add column is_visible boolean not null after status;


-- --------------------------------------------------------------------------------
-- Migration

-- 결정픽 VOTE 카테고리로
update community_category set type='VOTE' where title='결정픽';
update community_category set is_in_summary = true where type in ('DRIP', 'NORMAL');

-- 플래카드 제목, 배경색


-- 카테고리 데이터 삽입
insert into video_category (type, mask_type, sort, title, description, shape_file)
values ('GROUP', null, 1, '전체', '전체 카테고리', ''),
       ('NORMAL', 'CLOVER', 2, '😆웃겨주마', '😆웃겨주마 카테고리', 'shape_1'),
       ('NORMAL', 'SQUARE', 3, '📚알려주마', '📚알려주마 카테고리', 'shape_2'),
       ('NORMAL', 'SEMI_CIRCLE', 4, '🛒보여주마', '🛒보여주마 카테고리', 'shape_3'),
       ('NORMAL', 'HEART', 5, '🌱채워주마', '🌱채워주마 카테고리', 'shape_4'),
       ('NORMAL', 'CLOUD', 6, '💤재워주마', '💤재워주마 카테고리', 'shape_5');

-- 멤버 활동 정보 업데이트
insert into member_activity_count (id, community_count, community_comment_count, video_comment_count, created_at, modified_at)
select members.id, count(distinct c.id), count(distinct cc.id), count(distinct c2.id), now(), now()
from members
         left join community c on members.id = c.member_id
         left join community_comment cc on members.id = cc.member_id
         left join comments c2 on members.id = c2.created_by
         left join member_activity_count on members.id = member_activity_count.id
where member_activity_count.id is null
group by members.id;

-- 신고 테이블들 업데이트
update comment_reports
set reported_id = (select comments.created_by from comments where comment_reports.comment_id=comments.id);

update community_comment_report
set reported_id = (select community_comment.member_id  from community_comment where community_comment_report.comment_id=community_comment.id);

update community_report
set reported_id = (select community.member_id  from community where community_report.community_id=community.id);

update community_report
set created_at = date_add(now(), interval id second );

update community_comment_report
set created_at = date_add(now(), interval id second );

-- 이벤트 노출여부
update event set is_visible = true;

update placard
set color = '#340071', title = '설레는 첫 만남!\n오늘부터 1일'
where description = '회원가입 이벤트 플래카드';

update placard
set color = '#041F79', title = '친구 초대할 때마다\n포인트가 적립 된다구?'
where description = '친구초대 이벤트 플래카드';

update placard
set color = '#003B58', title = '영화 보러 같이 갈래?\n소소한 재미와 꿀팁, 여기 있어!'
where description = '비디오탭 플래카드';

update placard
set color = '#441F11', title = '살인가 붓기인가,\n나만의 살&붓기 빼는 TIP'
where id = 13


-- --------------------------------------------------------------------------------
-- Rollback
-- drop table community_vote;
-- drop table community_vote_member;
-- drop table member_activity_count;
-- drop table member_memo;
-- drop table video_category;
-- drop table scrap;

-- alter table community_category drop column is_in_summary;
-- alter table community drop column is_top_fix;
-- alter table placard drop column title;
-- alter table placard drop column color;
-- alter table event drop column reservation_at;
-- alter table event drop column is_visible;

-- alter table community_report drop column created_at;
-- alter table community_report drop column modified_at;
-- alter table community_report drop column reported_id;

-- alter table community_comment_report drop column created_at;
-- alter table community_comment_report drop column modified_at;
-- alter table community_comment_report drop column reported_id;

-- alter table comment_reports drop column reported_id;
-- alter table video_category_mapping rename video_categories;

-- delete from flyway_schema_history where version = '0153';


-- --------------------------------------------------------------------------------
-- index community
-- create index idx_category_status on community(category_id , status );
-- create index idx_is_top_sorted_at on community(is_top_fix desc, sorted_at desc);

-- index scrap
-- create index idx_scrap on scrap (member_id, relation_id);

-- Blind 분리
-- insert into community_category (type, sort, title, description, hint)
-- values ('GENERAL', 1, '일반 게시판', '일반 게시판 카테고리', '일반 게시판 카테고리 힌트'),
--       ('ANONYMOUS', 2, '익명 게시판', '익명 게시판 카테고리', '익명 게시판 카테고리 힌트');
       
-- select * from community_category;

-- update community_category set parent_id = 7, type = 'GROUP' where id = 1;
-- update community_category set parent_id = 7 where id in (1, 3, 4, 5, 6);
-- update community_category set parent_id = 8 where id = 2;

-- select * from community_category;
