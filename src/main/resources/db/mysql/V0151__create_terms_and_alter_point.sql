-- MEMBER_INVITATION_INFO -- 재훈님
create table member_invitation_info (
    id bigint auto_increment comment '친구 초대 정보 아이디' primary key,
    title varchar(30) comment '친구 초대 정보 제목',
    description varchar(100) comment '친구 초대 정보 설명',
    share_square_image_filename varchar(200) comment '친구 초대 정사각 이미지 파일명',
    share_rectangle_image_filename varchar(200) comment '친구 초대 웹용 직사각 이미지 파일명'
);

-- TERMS -- 재훈님
create table terms
(
    id                    bigint auto_increment comment '약관 아이디' primary key,
    type                  varchar(30) not null comment '약관 타입',
    title                 varchar(50) comment '약관 제목',
    content               varchar(3000) comment '약관 내용',
    current_term_status   varchar(20) not null comment '현재 약관 타입',
    used_in               varchar(20) comment '약관이 쓰이는 곳',
    version               varchar(5) not null comment '약관 버전',
    version_change_status varchar(20) not null comment '약관 업데이트 내용의 타입',
    created_at            datetime not null comment '약관 생성 시간',
    modified_at           datetime not null comment '약관 수정 시간'
) comment '약관 정보' charset = utf8mb4;

create table member_term
(
    id          bigint auto_increment comment '멤버 약관 아이디' primary key,
    is_accept   boolean not null comment '멤버의 약관 동의 여부',
    version     varchar(5) not null comment '멤버가 행동을 취한 약관의 버전',
    term_id     bigint not null comment '약관 아이디',
    member_id   bigint not null comment '멤버 아이디',
    created_at  datetime not null comment '생성 시간',
    modified_at datetime not null comment '수정 시간',
    unique (term_id, member_id),
    foreign key (member_id) references members (id),
    foreign key (term_id) references terms (id)
) comment '멤버 약관 행동 내역 정보' charset = utf8mb4;

create table term_history
(
    id                    bigint auto_increment comment '히스토리 아이디' primary key,
    history_type          varchar(10) not null comment '히스토리 타입(삽입, 수정, 삭제)',
    history_created_at    datetime not null comment '히스토리 생성 시간',
    term_id               bigint not null comment '약관 아이디',
    title                 varchar(50) comment '약관 제목',
    content               varchar(3000) comment '약관 내용',
    current_term_status   varchar(20) not null comment '현재 약관 타입',
    used_in               varchar(20) comment '약관이 쓰이는 곳',
    version               varchar(5) not null comment '약관 버전',
    version_change_status varchar(20) not null comment '약관 업데이트 내용의 타입'
)comment '약관 히스토리 정보' charset = utf8mb4;

-- VIDEO -- 재훈님
alter table video_scraps add column status varchar(20) not null comment '영상 스크랩 여부' after created_by;
alter table video_likes add column status varchar(20) not null comment '영상 좋아요 여부' after created_by;

-- COMMENT_LIKE -- 재훈님
alter table comment_likes add column status varchar(20) not null comment '댓글 좋아요 여부' after created_by;

-- POINT
alter table member_points add column event_id bigint after order_id;
alter table member_points add column activity_type varchar(50) comment '활동 포인트 타입' after event_id; -- 재훈님
alter table member_points add column activity_domain_id bigint comment '활동 포인트 도메인 아이디' after activity_type; -- 재훈님

alter table member_point_details add column event_id bigint after order_id; -- 재훈님
alter table member_point_details add column activity_type varchar(50) comment '활동 포인트 타입' after event_id; -- 재훈님


-- MEMBER
alter table apple_members add column refresh_token varchar(500)  not null  comment 'refresh token' after member_id;

alter table member_blocks add column status varchar(20) not null comment '블락 여부' after you; -- 재훈님

# drop table member_invitation_info;
# drop table member_term;
# drop table terms;
# drop table term_history;
# alter table video_scraps drop column status;
# alter table video_likes drop column status;
# alter table comment_likes drop column status;
# alter table member_points drop column event_id;
# alter table member_points drop column activity_type;
# alter table member_points drop column activity_domain_id;
# alter table member_point_details drop column event_id;
# alter table member_point_details drop column activity_type;
# alter table apple_members drop column refresh_token;
# alter table member_blocks drop column status;

-- delete from flyway_schema_history where version = '0151';