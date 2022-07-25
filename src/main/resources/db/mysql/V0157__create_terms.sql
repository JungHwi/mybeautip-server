create table terms (
    id bigint auto_increment comment '약관 아이디' primary key,
    title varchar(50) comment '약관 제목',
    content varchar(3000) comment '약관 내용',
    current_term_status varchar(20) comment '현재 약관 타입',
    used_in varchar(20) comment '약관이 쓰이는 곳',
    version varchar(5) comment '약관 버전',
    version_change_status varchar(20) comment '약관 업데이트 내용의 타입',
    created_at datetime comment '약관 생성 시간',
    modified_at datetime comment '약관 수정 시간'
) comment '약관 정보' charset = utf8mb4;


create table member_term (
     id bigint auto_increment comment '멤버 약관 아이디' primary key,
     is_accept boolean comment '멤버의 약관 동의 여부',
     version varchar(5) comment '멤버가 행동을 취한 약관의 버전',
     term_id bigint comment '약관 아이디',
     member_id bigint comment '멤버 아이디',
     created_at datetime comment '생성 시간',
     modified_at datetime comment '수정 시간',
     foreign key (member_id) references members(id),
     foreign key (term_id) references terms(id)
) comment '멤버 약관 행동 내역 정보' charset = utf8mb4;
#
# drop table terms;
# drop table member_terms;
#
# delete from flyway_schema_history where version = '0157';
