create table event (
                       id bigint auto_increment primary key comment 'Event ID',
                       type varchar(20) not null comment '이벤트 구분',
                       status varchar(20) not null comment '이벤트 상태',
                       sorting smallint comment '정렬',
                       title varchar(100) not null comment '이벤트 제목',
                       description varchar(300) comment '설명',
                       image_url varchar(200) comment '이미지 URL',
                       need_point int comment '참여시 필요한 Point',
                       start_at datetime not null comment '이벤트 시작 일시',
                       end_at datetime comment '이벤트 종료 일시. 없으면 무한',
                       modified_at datetime not null comment '이벤트 수정 일시',
                       created_at datetime not null comment '이벤트 생성 일시'
) character set utf8mb4 comment '이벤트 정보';

create index idx_event on event (sorting, status, start_at, modified_at);

create table event_join (
                            id bigint auto_increment primary key comment 'Event Join ID',
                            event_id bigint not null comment 'event id',
                            member_id bigint not null comment 'member id',
                            status varchar(20) not null comment 'event 참여 상태',
                            event_product_id bigint comment '당첨된 이벤트 상품 ID',
                            created_at datetime not null comment '이벤트 참여 일시'
) character set utf8mb4 comment '이벤트 응모 / 당첨 정보';

create index idx_event_join on event_join(member_id);

create table event_product (
                               id bigint auto_increment primary key comment 'Event Join ID',
                               event_id bigint not null comment 'event id',
                               type varchar(20) not null comment '이벤트 상품 구분',
                               name varchar(100) not null comment '상품명',
                               quantity int not null comment '수량',
                               price int comment '상품 가격',
                               image_url varchar(200) comment '상품 이미지 URL'
) character set utf8mb4 comment '이벤트 상품 정보';

create index idx_event_product on event_product(event_id);

alter table member_points add column event_id bigint after order_id;
alter table member_point_details add column event_id bigint after order_id;

# drop table event;
# drop table event_join;
# drop table event_product;
# alter table member_points drop column event_id;
# alter table member_point_details drop column event_id;
#
# delete from flyway_schema_history where version = '0150';