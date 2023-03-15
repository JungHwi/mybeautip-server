create table influencer
(
    id              bigint primary key comment '회원 아이디',
    status          varchar(20) not null comment '상태',
    broadcast_count int         not null comment '방송 횟수',
    earned_at       datetime comment '인플루언서 권한 마지막 획득 일시'
) comment '인플루언서 정보';

create table system_option
(
    id    varchar(20) not null primary key comment '옵션',
    value varchar(20) not null comment '옵션 값'
) comment '시스템 옵션 정보';

create table broadcast
(
    id               bigint auto_increment primary key comment '방송 아이디',
    category_id      bigint                                                                                        not null comment '카테고리 아이디',
    status           varchar(20)                                                                                   not null comment '상태',
    sorted_status    int generated always as (field(status, 'LIVE', 'READY', 'SCHEDULED', 'END', 'CANCEL')) stored not null,
    video_key        varchar(10)                                                                                   not null comment '플립플랍 비디오 키',
    chat_channel_key      varchar(30)                                                                                   not null comment '채팅룸 키',
    member_id        bigint                                                                                        not null comment '회원 아이디',
    title            varchar(25)                                                                                   not null comment '제목',
    url              varchar(200) comment '방송 URL',
    thumbnail        varchar(16)                                                                                   not null comment '방송 썸네일 파일',
    notice           varchar(100) comment '공지사항',
    pin              varchar(1000) comment '핀 채팅',
    report_count     int                                                                                           not null comment '신고 수',
    heart_count      int                                                                                           not null comment '하트 수',
    viewer_count     int                                                                                           not null comment '시청자 수',
    max_viewer_count int                                                                                           not null comment '최대 시청자 수',
    paused_at        datetime                                                                                      comment '일시정지 일시',
    started_at       datetime                                                                                      not null comment '방송 시작 일시',
    ended_at         datetime comment '방송 종료 일시',
    created_at       datetime                                                                                      not null comment '생성 일시'
) charset = utf8mb4 comment '방송 정보';

create table broadcast_viewer
(
    id              bigint auto_increment primary key comment '시청자 아이디',
    broadcast_id    bigint      not null comment '방송 아이디',
    member_id       bigint comment '회원 아이디',
    sorted_username varchar(20) not null comment '정렬된 회원명',
    type            varchar(20) not null comment '회원 구분',
    status          varchar(20) not null comment '상태',
    is_suspended    boolean     not null comment '정지 여부',
    suspended_at    datetime comment '정지 일시',
    joined_at       datetime    not null comment '참여 일시'
) comment '시청자 정보';

create table vod
(
    id                bigint auto_increment primary key comment 'VOD 아이디',
    category_id       bigint                                                              not null comment '카테고리 아이디',
    member_id         bigint                                                              not null comment '회원 아이디',
    video_key         bigint                                                              not null comment '외부 비디오 키',
    status            varchar(20)                                                         not null comment '상태',
    is_visible        boolean                                                             not null comment '노출여부',
    title             varchar(200)                                                        not null comment '제목',
    thumbnail         varchar(16)                                                         not null comment '썸네일 파일',
    view_count        int                                                                 not null comment '조회수',
    report_count      int                                                                 not null,
    total_heart_count int generated always as (vod_heart_count + live_heart_count) stored not null comment '총 하트수',
    vod_heart_count   int                                                                 not null comment '하트수',
    live_heart_count  int                                                                 not null comment '라이브 하트수',
    duration          bigint                                                              not null comment '영상 길이',
    created_at        datetime                                                            not null comment 'VOD 생성시간',
    modified_at       datetime                                                            not null comment 'VOD 수정시간'
) charset = utf8mb4 comment 'VOD 정보';

create table broadcast_category
(
    id          bigint auto_increment comment '라이브 카테고리 아이디' primary key,
    parent_id   bigint comment '부모 카테고리 아이디',
    sort        int comment '정렬순서',
    title       varchar(20)  not null comment '카테고리 제목',
    description varchar(100) not null comment '카테고리 설명'
) charset = utf8mb4 comment '라이브 카테고리 정보';

create table vod_report
(
    id          bigint auto_increment comment 'VOD 신고 아이디' primary key,
    reporter_id bigint   not null comment '신고자 아이디',
    reported_id bigint   not null comment '피신고자 아이디',
    vod_id      bigint   not null comment 'VOD 아이디',
    description varchar(100) comment '신고 사유',
    created_at  datetime not null comment '생성시간',
    modified_at datetime not null comment '수정시간'
) charset = utf8mb4 comment 'VOD 신고 정보';

create table jwt(
    id varchar(255) comment '아이디' primary key,
    refresh_token varchar(1000) not null comment 'refresh token',
    expiry_at datetime not null comment '만료시간'
) comment 'JWT 정보';

create table broadcast_report
(
    id           bigint auto_increment comment '방송 신고 아이디' primary key,
    reporter_id  bigint   not null comment '신고자 아이디',
    reported_id  bigint   not null comment '피신고자 아이디',
    broadcast_id bigint   not null comment '방송 아이디',
    description  varchar(100) comment '신고 사유',
    created_at   datetime not null comment '생성시간',
    modified_at  datetime not null comment '수정시간'
) charset = utf8mb4 comment '방송 신고 정보';

# drop table influencer;
# drop table system_option;
# drop table broadcast_viewer;
# drop table vod;
# drop table vod_report;
# drop table broadcast_category;
# drop table jwt;
# drop table vod_report;
# delete from flyway_schema_history where version = '0157';

