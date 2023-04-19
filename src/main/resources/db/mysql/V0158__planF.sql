create table company
(
    id                  bigint auto_increment primary key comment '공급사 아이디',
    name                varchar(100) not null comment '공급사명',
    status              varchar(20) not null comment '상태',
    sales_fee           DECIMAL(5, 2) not null comment '판매 수수료 (%)',
    shipping_fee        DECIMAL(5, 2) not null comment '배송 수수료 (%)',
    business_name       varchar(100) not null comment '상호명',
    business_number     char(12) not null comment '사업자 번호',
    representative_name varchar(50) not null comment '대표자명',
    email               varchar(150) not null comment '이메일',
    phone_number        varchar(20) not null comment '전화번호',
    business_type       varchar(20) not null comment '업태',
    business_item       varchar(20) not null comment '업종',
    zipcode             varchar(6) not null comment '우편번호',
    address1            varchar(100) not null comment '주소',
    address2            varchar(50) not null comment '상세 주소',

    created_at          datetime not null comment '생성 일시'
) charset = utf8mb4 comment '공급사 정보';

create table company_account
(
    id                  bigint auto_increment primary key comment '공급사 계좌 아이디',
    company_id          bigint not null comment '공급사 아이디',
    bank_name           varchar(20) comment '은행명',
    account_number      varchar(20) comment '계좌 번호',
    owner_name          varchar(10) comment '예금주명'
) charset = utf8mb4 comment '공급사 계좌 정보';

create table company_claim
(
    id                      bigint auto_increment primary key comment '공급사 아이디',
    company_id              bigint not null comment '공급사 아이디',
    customer_center_phone   varchar(20) comment '고객센터 전화번호',
    zipcode                 varchar(6) not null comment '우편번호',
    address1                varchar(100) not null comment '주소',
    address2                varchar(50) not null comment '상세 주소'
) charset = utf8mb4 comment '공급사 정보';



# delete from flyway_schema_history where version = '0158';
# drop table company;
# drop table company_account;
# drop table company_claim;
