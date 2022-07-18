alter table member_invitation_info change share_square_image_url share_square_image_filename varchar(200);
alter table member_invitation_info change share_rectangle_image_url share_rectangle_image_filename varchar(200);

# alter table member_invitation_info drop column share_square_image_filename;
# alter table member_invitation_info drop column share_rectangle_image_filename;
#
# delete from flyway_schema_history where version = '0157';