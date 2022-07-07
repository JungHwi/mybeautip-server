alter table apple_members add column refresh_token varchar(500)  not null  comment 'refresh token' after member_id;

# alter table apple_members drop column refresh_token;

# delete from flyway_schema_history where version = '0154';
