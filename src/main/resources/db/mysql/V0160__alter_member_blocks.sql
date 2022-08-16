alter table mybeautip.member_blocks add column  status varchar(20) not null comment '블락 여부' after you;

# alter table member_blocks drop column status;
#
# delete from flyway_schema_history where version = '0160';
