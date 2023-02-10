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


# drop table influencer;
# drop table system_option;
