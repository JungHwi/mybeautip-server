create table terms (
    id bigint auto_increment comment '약관 아이디' primary key,
    title varchar(50) comment '약관 제목',
    content varchar(3000) comment '약관 내용',
    current_term_status varchar(20) comment '현재 약관 타입',
    used_type varchar(20) comment '약관이 쓰이는 곳',
    version varchar(5) comment '약관 버전',
    version_change_status varchar(20) comment '약관 업데이트 내용의 타입',
    created_at datetime comment '약관 생성 시간',
    modified_at datetime comment '약관 수정 시간'
) comment '약관 정보' charset = utf8mb4;

# drop table terms;
#
# delete from flyway_schema_history where version = '0157';