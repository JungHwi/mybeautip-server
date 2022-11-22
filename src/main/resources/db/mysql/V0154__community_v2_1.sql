alter table member_activity_count add column all_community_count int default 0 after video_comment_count;
alter table member_activity_count add column all_community_comment_count int default 0 after all_community_count;
alter table member_activity_count add column all_video_comment_count int default 0 after all_community_comment_count;

# alter table  member_activity_count drop column all_community_count;
# alter table  member_activity_count drop column all_community_comment_count;
# alter table  member_activity_count drop column all_video_comment_count;
