rename table `stores` to `legacy_stores`;
rename table `store_likes` to `legacy_store_likes`;

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
    id                      bigint auto_increment primary key comment '공급사 클레임 아이디',
    company_id              bigint not null comment '공급사 아이디',
    customer_center_phone   varchar(20) comment '고객센터 전화번호',
    zipcode                 varchar(6) not null comment '우편번호',
    address1                varchar(100) not null comment '주소',
    address2                varchar(50) not null comment '상세 주소'
) charset = utf8mb4 comment '공급사 정보';

create table company_permission
(
    id                      bigint auto_increment primary key comment '공급사 권한 아이디',
    company_id              bigint not null comment '공급사 아이디',
    create_product          varchar(20) comment '상품 등록 권한',
    update_product          varchar(20) comment '상품 수정 권한',
    delete_product          varchar(20) comment '상품 삭제 권한'
) charset = utf8mb4 comment '공급사 권한 정보';

create table brand
(
    id              bigint auto_increment primary key comment '공급사 권한 아이디',
    company_id      bigint not null comment '공급사 아이디',
    code            char(7) unique not null comment '브랜드 코드',
    status          varchar(20) not null comment '브랜드 상태',
    name            varchar(20) not null comment '브랜드명',
    description     varchar(1000) comment '브랜드 정보',
    created_at      datetime not null comment '생성 일시'
) charset = utf8mb4 comment '브랜드 정보';

create table store_category
(
    id          bigint auto_increment primary key comment '스토어 카테고리 아이디',
    code        char(8) not null comment '카테고리 코드',
    sort        int not null comment '순서',
    status      varchar(20) not null comment '카테고리 상태',
    name        varchar(30) not null comment '대표 카테고리명',
    created_at  datetime not null comment '생성 일시'
) charset = utf8mb4 comment '스토어 카테고리 정보';

create table store_category_detail
(
    id          bigint auto_increment primary key comment '스토어 카테고리 상세 정보 아이디',
    store_id    bigint not null comment '스토어 카테고리 아이디',
    country      char(2) not null comment '국가 코드',
    name        varchar(30) not null comment '국가별 카테고리명'
) charset = utf8mb4 comment '스토어 카테고리 상세 정보';


# delete from flyway_schema_history where version = '0158';
# drop table company;
# drop table company_account;
# drop table company_claim;
# drop table company_permission;
# drop table brand;
# drop table store_category;
# drop table store_category_detail;
