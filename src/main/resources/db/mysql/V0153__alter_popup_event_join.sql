alter table event_join add column recipient_info varchar(300) after member_id;

alter table popup add column display_type varchar(20) after status;

# alter table event_join drop column recipient_info;
# alter table popup drop column display;
#
# delete from flyway_schema_history where version = '0153';