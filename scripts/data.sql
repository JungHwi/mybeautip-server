insert into tags(name, ref_count, created_at) values ('틴트', 0, now());
insert into tags(name, ref_count, created_at) values ('팩트', 0, now());
insert into tags(name, ref_count, created_at) values ('베네픽트', 0, now());
insert into tags(name, ref_count, created_at) values ('무료배송', 0, now());

insert into keyword_recommendations(category, member_id, created_by, seq, created_at, started_at, ended_at) values (1, 12, 12, 1, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, tag_id, created_by, seq, created_at, started_at, ended_at) values (2, 1, 12, 2, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, tag_id, created_by, seq, created_at, started_at, ended_at) values (2, 2, 12, 3, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, member_id, created_by, seq, created_at, started_at, ended_at) values (1, 8, 12, 4, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, tag_id, created_by, seq, created_at, started_at, ended_at) values (2, 3, 12, 5, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, member_id, created_by, seq, created_at, started_at, ended_at) values (1, 7, 12, 6, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');
insert into keyword_recommendations(category, tag_id, created_by, seq, created_at, started_at, ended_at) values (2, 4, 12, 7, now(), '2018-09-01 00:00:00.000', '2018-11-01 00:00:00.000');