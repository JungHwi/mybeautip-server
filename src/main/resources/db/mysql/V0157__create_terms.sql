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
)comment '약관 히스토리 정보' charset = utf8mb4;;


# drop table member_term;
# drop table term_history;
# drop table terms;
# delete from flyway_schema_history where version = '0157';
