create table influencer (
    id bigint primary key comment '회원 아이디',
    status varchar(20) not null comment '상태',
    broadcast_count int not null comment '방송 횟수',
    earned_at datetime not null comment '인플루언서 권한 마지막 획득 일시'
) comment '인플루언서 정보';


# drop table influencer;
