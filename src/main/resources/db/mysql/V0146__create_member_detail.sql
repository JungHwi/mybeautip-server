alter table members add column tag char(7) not null after id;

create index idx_members_tag on members(tag);

alter table members add column birthday date after username;

create table member_detail (
   id bigint not null primary key auto_increment comment 'id',
   member_id bigint not null comment '회원 ID',
   inviter_id bigint comment '초대자 ID',
   skin_type varchar(20) comment '피부 타입',
   skin_worry varchar(50) comment '피부 고민'
) character set utf8mb4 comment 'notification template';

create index idx_member_detail_member_id on member_detail (member_id);

# alter table members drop column tag;
# alter table members drop column birthday;
# drop table member_detail;
# DELETE FROM flyway_schema_history WHERE version = '0146';