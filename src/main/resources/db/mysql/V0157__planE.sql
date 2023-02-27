create table influencer (
    id bigint primary key comment '회원 아이디',
    status varchar(20) not null comment '상태',
    broadcast_count int not null comment '방송 횟수',
    earned_at datetime comment '인플루언서 권한 마지막 획득 일시'
) comment '인플루언서 정보';

create table system_option (
    id varchar(20) not null primary key comment '옵션',
    value varchar(20) not null comment '옵션 값'
) comment '시스템 옵션 정보';

create table broadcast (
    id bigint auto_increment primary key comment '방송 아이디',
    category_id bigint not null comment '카테고리 아이디',
    status varchar(20) not null comment '상태',
    sorted_status int generated always as (field(status, 'LIVE', 'READY', 'SCHEDULED', 'END', 'CANCEL')) virtual not null,
    video_key varchar(10) not null comment '플립플랍 비디오 키',
    member_id bigint not null comment '회원 아이디',
    title varchar(25) not null comment '제목',
    url varchar(200) not null comment '방송 URL',
    thumbnail varchar(16) not null comment '방송 썸네일 파일',
    notice varchar(100) comment '공지사항',
    pin varchar(1000) comment '핀 채팅',
    heart_count int not null comment '하트 수',
    started_at datetime not null comment '방송 시작 일시',
    ended_at datetime comment '방송 종료 일시',
    created_at datetime not null comment '생성 일시'
) charset = utf8mb4 comment '방송 정보';

create table broadcast_viewer (
    id bigint auto_increment primary key  comment '시청자 아이디',
    broadcast_id bigint not null comment '방송 아이디',
    member_id bigint comment '회원 아이디',
    sorted_username varchar(20) not null comment '정렬된 회원명',
    type varchar(20) not null comment '회원 구분',
    status varchar(20) not null comment '상태',
    is_suspended boolean not null comment '정지 여부',
    suspended_at datetime comment '정지 일시',
    joined_at datetime not null comment '참여 일시'
) comment '시청자 정보';


# drop table influencer;
# drop table system_option;
# drop table broadcast_viewer;
