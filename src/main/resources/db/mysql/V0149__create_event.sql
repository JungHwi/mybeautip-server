-- EVENT
create table event (
    id bigint auto_increment primary key comment '이벤트 ID',
    type varchar(20) not null comment '이벤트 구분',
    relation_id bigint comment '관련된 아이디',
    status varchar(20) not null comment '이벤트 상태',
    sorting smallint comment '정렬',
    title varchar(100) not null comment '이벤트 제목',
    description varchar(300) comment '설명',
    need_point int comment '참여시 필요한 Point',
    image_file char(16) comment '메인 이미지 파일명',
    thumbnail_image_file char(16) comment '썸네일 파일명',
    share_square_image_file char(16) comment '공유용 정사각 이미지 파일명',
    share_rectangle_image_file char(16) comment '공유용 직사각 이미지 파일명',
    banner_image_file char(16) comment '배너 이미지 파일명',
    start_at datetime not null comment '이벤트 시작 일시',
    end_at datetime comment '이벤트 종료 일시. 없으면 무한',
    modified_at datetime not null comment '이벤트 수정 일시',
    created_at datetime not null comment '이벤트 생성 일시'
) character set utf8mb4 comment '이벤트 정보';

create table event_product (
    id bigint auto_increment primary key comment '이벤트 상품 ID',
    event_id bigint not null comment 'event id',
    type varchar(20) not null comment '이벤트 상품 구분',
    name varchar(100) not null comment '상품명',
    quantity int not null comment '수량',
    price int comment '상품 가격',
    image_file varchar(200) comment '상품 이미지 파일명'
) character set utf8mb4 comment '이벤트 상품 정보';

create table event_join (
    id bigint auto_increment primary key comment '이벤트 참가 ID',
    event_id bigint not null comment '이벤트 id',
    member_id bigint not null comment '회원 id',
    recipient_info varchar(300) comment '회원 정보',
    status varchar(20) not null comment '참여 상태',
    event_product_id bigint comment '당첨된 이벤트 상품 ID',
    created_at datetime not null comment '이벤트 참여 일시'
) character set utf8mb4 comment '이벤트 응모 / 당첨 정보';

create index idx_event_join_member_id on event_join(member_id);
create index idx_event_join_event_id_id on event_join(event_id, id);

-- PLACARD
create table placard (
    id bigint auto_increment comment '플래카드 아이디' primary key,
    status varchar(20) not null comment '상태',
    link_type varchar(20) not null comment '이동 방식 구분',
    link_argument varchar(300) comment '이동 방식에 따른 argument',
    description varchar(50) comment '플래카드 설명',
    started_at datetime not null comment '플래카드 게시 시작일시',
    ended_at datetime not null comment '플래카드 게시 종료일시',
    created_at datetime not null comment '생성일'
) comment '플래카드 정보' charset = utf8mb4;

create table placard_detail (
    id bigint auto_increment comment '플래카드 이미지 아이디' primary key,
    placard_id bigint not null comment '플래카드 아이디',
    tab_type varchar(20) not null comment 'tab 타입',
    image_file char(16) not null comment '이미지 파일명'
) comment '플래카드 이미지 정보' charset = utf8mb4;


-- POPUP
create table popup (
    id bigint auto_increment comment '팝업 아이디' primary key,
    type varchar(20) not null comment '팝업 시점 구분',
    display_type varchar(20) not null comment '팝업 노출 구분',
    status varchar(20) not null comment '상태',
    image_file char(16) comment '팝업 이미지 파일명',
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

-- drop table event;
-- drop table event_product;
-- drop table event_join;
-- drop table placard;
-- drop table placard_detail;
-- drop table popup;
-- drop table popup_button;

-- delete from flyway_schema_history where version = '0149';