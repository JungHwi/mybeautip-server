alter table member_activity_count add column all_community_count int default 0 after video_comment_count;
alter table member_activity_count add column all_community_comment_count int default 0 after all_community_count;
alter table member_activity_count add column all_video_comment_count int default 0 after all_community_comment_count;

# alter table  member_activity_count drop column all_community_count;
# alter table  member_activity_count drop column all_community_comment_count;
# alter table  member_activity_count drop column all_video_comment_count;
# delete from flyway_schema_history where version = '0154';


-- 멤버 활동 카운트 업데이트
# update member_activity_count
# set all_community_count = (select count(*) from community where community.member_id = member_activity_count.id);
#
# update member_activity_count
# set community_count = (select count(*) from community where community.member_id = member_activity_count.id and community.status = 'NORMAL');
#
# update member_activity_count
# set all_community_comment_count = (select count(*) from community_comment where community_comment.member_id = member_activity_count.id);
#
# update member_activity_count
# set community_comment_count = (select count(*) from community_comment where community_comment.member_id = member_activity_count.id and community_comment.status = 'NORMAL');
#
# update member_activity_count
# set all_video_comment_count = (select count(*) from comments where comments.created_by = member_activity_count.id);
#
# update member_activity_count
# set video_comment_count = (select count(*) from comments where comments.created_by = member_activity_count.id and comments.state = 0);
