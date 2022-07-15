create table member_invitation_info (
    id bigint auto_increment comment '친구 초대 정보 아이디' primary key,
    title varchar(30) comment '친구 초대 정보 제목',
    description varchar(100) comment '친구 초대 정보 설명',
    share_square_image_url varchar(200) comment '친구 초대 정사각 이미지 url',
    share_rectangle_image_url varchar(200) comment '친구 초대 웹용 직사각 이미지 url'
);

# drop table member_invitation_info;
#
# delete from flyway_schema_history where version = '0156';