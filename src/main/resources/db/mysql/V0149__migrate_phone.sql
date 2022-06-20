# phone migration
create table members_backup (select * from members);

update members
set phone_number = replace(trim(phone_number), '-', '')
where LOCATE(phone_number, '-') > 0 or locate(phone_number, ' ') > 0;

create table addresses_backup (select * from addresses);

update addresses
set phone = replace(trim(phone), '-', '')
where LOCATE(phone, '-') > 0 or locate(phone, ' ') > 0;

# Notification Index
create index idx_notification_center on notification_center
(user_id, status, created_at);
