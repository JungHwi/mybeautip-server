rename table `stores` to `legacy_stores`;
rename table `store_likes` to `legacy_store_likes`;

create table company
(
    id                  bigint auto_increment primary key comment '공급사 아이디',
    code                char(5) comment '공급사 코드',
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
    category_id    bigint not null comment '스토어 카테고리 아이디',
    country      char(2) not null comment '국가 코드',
    name        varchar(30) not null comment '국가별 카테고리명'
) charset = utf8mb4 comment '스토어 카테고리 상세 정보';

create table store
(
    id          bigint auto_increment primary key comment '스토어 아이디',
    category_id bigint not null comment '카테고리 아이디',
    brand_id bigint not null  comment '브랜드 아이디',
    delivery_fee_policy_id bigint not null comment '배송비 조건 아이디',
    country_code char(2) not null comment '국가 코드',
    status varchar(20) not null comment '스토어 상태',
    is_visible boolean not null comment '공개 여부',
    name varchar(50) not null comment '대표 노출 상품명',
    description varchar(100) comment '상세 정보',
    is_best boolean not null comment '베스트 태그 여부',
    is_new boolean not null comment '신규 태그 여부'
) charset = utf8mb4 comment '스토어 정보';

create table store_image
(
    id          bigint auto_increment primary key comment '스토어 상세 이미지 아이디',
    store_id    bigint       not null comment '상품 아이디',
    image       varchar(200) not null comment '이미지 파일명',
    type        varchar(20)  not null comment '이미지 타입',
    sort        int          not null comment '이미지 순서',
    modified_at datetime     not null comment '수정일',
    created_at  datetime     not null comment '등록일'
) charset = utf8mb4 comment '스토어 이미지 정보';

create table store_option
(
    id                   bigint auto_increment primary key comment '스토어 옵션 아이디',
    type                 varchar(20) not null comment '옵션 타입',
    is_visible           boolean     not null comment '공개 여부',
    is_sold_out          boolean     not null comment '품절 여부',
    country_product_code varchar(30) not null comment '국가 상품 코드',
    name                 varchar(50) not null comment '옵션명',
    stock                int         not null comment '재고',
    fixed_price          int         not null comment '정상가',
    purchase_price       int comment '매입가',
    discount_price       int comment '할인가',
    discount_rate        decimal comment '할인율',
    sale_price           int         not null comment '판매가',
    thumbnail            varchar(16) not null comment '썸네일 파일'
) charset = utf8mb4 comment '스토어 옵션 정보';

create table store_policy
(
    id              bigint auto_increment primary key comment '스토어 약관 및 정책 아이디',
    store_id        bigint        not null comment '스토어 아이디',
    delivery_policy varchar(2000) not null comment '배송 정책',
    product_policy  varchar(2000) not null comment '취소/교환/반품 정책',
    modified_at     datetime      not null comment '수정일',
    created_at      datetime      not null comment '등록일'
) charset = utf8mb4 comment '스토어 약관 및 정책 정보';

create table store_option_product
(
    id                bigint auto_increment primary key comment '스토어 옵션 상품 상세 매핑 아이디',
    store_option_id   bigint not null comment '스토어 옵션 아이디',
    product_detail_id bigint not null comment '상품 상세 아이디'
) charset = utf8mb4 comment '스토어 옵션 국가별 상품 매핑 정보';

create table store_option_notice
(
    id              bigint auto_increment primary key comment '스토어 옵션 고시정보 아이디',
    store_option_id bigint        not null comment '스토어 옵션 아이디',
    notice_type     varchar(50)   not null comment '고시 정보 타입',
    description     varchar(2000) not null comment '항목 내용',
    modified_at     datetime      not null comment '수정일',
    created_at      datetime      not null comment '등록일'
) charset = utf8mb4 comment '스토어 옵션 고시 정보';

create table store_option_notice_custom
(
    id               bigint auto_increment primary key comment '스토어 옵션 커스텀 고시정보 아이디',
    store_option_id  bigint        not null comment '스토어 옵션 아이디',
    item_name        varchar(50)   not null comment '항목명',
    item_description varchar(2000) not null comment '항목 내용',
    modified_at      datetime      not null comment '수정일',
    created_at       datetime      not null comment '등록일'
) charset = utf8mb4 comment '스토어 옵션 커스텀 고시 정보';

create table delivery_company (
    id          bigint auto_increment primary key comment '배송업체 아이디',
    code        char(4) comment '배송업체 코드',
    status      varchar(20) not null comment '배송업체 상태',
    name        varchar(20) not null comment '배송업체 명',
    url         varchar(1000) not null comment '배송조회 URL'
) charset = utf8mb4 comment '배송업체 정보';

create table delivery_fee_policy (
    id                  bigint auto_increment primary key comment '배송비정책 아이디',
    code                char(4) not null comment '배송코드',
    company_id          bigint not null comment '공급사 아이디',
    name                varchar(20) not null comment '대표 배송비명',
    status              varchar(20) not null comment '배송비 상태',
    type                varchar(20) not null comment '배송비 유형',
    is_default          boolean not null comment '기본 배송비 여부',
    delivery_method     varchar(20) not null comment '배송방식',
    payment_option      varchar(20) not null comment '결제방식',
    created_at          datetime not null comment '등록일'
) charset = utf8mb4 comment '배송비 정책 정보';

create table delivery_fee_policy_detail (
    id                      bigint auto_increment primary key comment '배송비정책 상세 아이디',
    delivery_fee_policy_id  bigint not null comment '배송비정책 아이디',
    country_code            char(2) not null comment '국가코드',
    name                    varchar(20) not null comment '국가별 배송비명',
    threshold               int comment '배송비 분기 조건',
    fee_below_threshold     int not null comment '조건 미만 배송비',
    fee_above_threshold     int not null comment '조건 이상 배송비'
) charset = utf8mb4 comment '배송비 정책 정보';

create table product
(
    id          bigint auto_increment primary key comment '상품 아이디',
    brand_id    bigint      comment '브랜드 아이디',
    code        char(30)    comment '상품 코드',
    is_visible  boolean     not null comment '공개 여부',
    status      varchar(20) not null comment '상품 상태',
    name        varchar(50) comment '대표 상품명',
    stock       bigint      comment '재고',
    weight      int         comment '무게',
    modified_at datetime    not null comment '수정일',
    created_at  datetime    not null comment '등록일'
) charset = utf8mb4 comment '상품 정보';

create table product_image
(
    id          bigint auto_increment primary key comment '상품 이미지 아이디',
    product_id  bigint       not null comment '상품 아이디',
    image       varchar(200) not null comment '이미지 파일명',
    sort        int          not null comment '이미지 순서',
    modified_at datetime     not null comment '수정일',
    created_at  datetime     not null comment '등록일'
) charset = utf8mb4 comment '상품 이미지 정보';

create table product_detail
(
    id                   bigint auto_increment primary key comment '상품 세부 아이디',
    product_id           bigint      not null comment '상품 아이디',
    country_product_code varchar(30) not null comment '국가 상품 코드',
    country_code         char(2)     not null comment '국가코드',
    name                 varchar(50) not null comment '상품명',
    fixed_price          int         not null comment '정상가',
    purchase_price       int comment '매입가',
    discount_price       int comment '할인가',
    discount_rate        decimal comment '할인율',
    sale_price           int         not null comment '판매가',
    description          varchar(100) comment '상세 정보',
    modified_at          datetime    not null comment '수정일',
    created_at           datetime    not null comment '등록일'
) charset = utf8mb4 comment '국가별 상품 정보';

create table product_detail_notice
(
    id                bigint auto_increment primary key comment '상품 고시정보 아이디',
    product_detail_id bigint        not null comment '상품 상세 아이디',
    notice_type       varchar(50)   not null comment '고시 정보 타입',
    description       varchar(2000) not null comment '항목 내용',
    modified_at       datetime      not null comment '수정일',
    created_at        datetime      not null comment '등록일'
) charset = utf8mb4 comment '국가별 상품 고시 및 정책 정보';

create table product_detail_notice_custom
(
    id                bigint auto_increment primary key comment '상품 커스텀 고시정보 아이디',
    product_detail_id bigint        not null comment '상품 상세 아이디',
    item_name         varchar(50)   not null comment '항목명',
    item_description  varchar(2000) not null comment '항목 내용',
    modified_at       datetime      not null comment '수정일',
    created_at        datetime      not null comment '등록일'
) charset = utf8mb4 comment '국가별 상품 커스텀 고시 정보';

create table product_detail_image
(
    id                bigint auto_increment primary key comment '상품 이미지 아이디',
    product_detail_id bigint       not null comment '상품 아이디',
    image             varchar(200) not null comment '이미지 파일명',
    type              varchar(20)  not null comment '이미지 타입',
    sort              int          not null comment '이미지 순서',
    modified_at       datetime     not null comment '수정일',
    created_at        datetime     not null comment '등록일'
) charset = utf8mb4 comment '국가별 상품 이미지 정보';

create table policy (
    country_code char(2) comment '국가코드',
    delivery_policy text not null comment '배송 정책',
    claim_policy text not null comment '취소/교환/반품 정책'
) charset = utf8mb4 comment '정책';

create table policy_history (
    id bigint primary key auto_increment comment '약관 및 정책 이력의 아이디',
    country_code char(2) not null comment '국가코드',
    before_delivery_policy text not null comment '변경전 배송 정책',
    before_claim_policy text not null comment '변경전 취소/교환/반품 정책',
    after_delivery_policy text not null comment '변경후 배송 정책',
    after_claim_policy text not null comment '변경후 취소/교환/반품 정책',
    created_at datetime not null comment '등록일시'
) charset = utf8mb4 comment '정책 이력';

# delete from flyway_schema_history where version = '0158';
# drop table company;
# drop table company_account;
# drop table company_claim;
# drop table company_permission;
# drop table brand;
# drop table store_category;
# drop table store_category_detail;
# drop table store;
# drop table delivery_company;
# drop table delivery_fee_policy;
# drop table delivery_fee_policy_detail;
# drop table product;
# drop table product_image;
# drop table product_detail;
# drop table product_detail_notice;
# drop table product_detail_notice_custom;
# drop table product_detail_image;
# drop table policy;
# drop table policy_history;
