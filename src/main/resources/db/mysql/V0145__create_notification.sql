create table notification_template (
    id varchar(30) primary key comment 'Template ID - Template Type',
    description varchar(300) comment 'Notification 이 발송되는 기준 시점 설명',
    send_types varchar(50) not null comment '이용하는 발송 타입. CENTER & PUSH & SMS & EMAIL. 복수 선택 가능',
    available_arguments varchar(200) comment '사용 가능한 arguments. {,} 로 구분.'
) character set utf8mb4 comment 'notification template';

create table notification_message_center (
    id bigint auto_increment primary key comment 'Message ID',
    template_id varchar(30) not null comment 'Template ID',
    lang varchar(6) not null default 'KO' comment '언어 코드',
    message_type varchar(20) not null comment '메세지 종류. COMMUNITY || CONTENT || LOGIN',
    isLastVersion boolean not null default true comment '마지막 버전여부',
    message varchar(300) not null comment 'message',
    deep_link varchar(200) comment 'deep link'
) character set utf8mb4 comment '알림센터에서 사용할 Message 정보';

create table notification_message_push (
    id bigint auto_increment primary key comment 'Message ID',
    template_id varchar(30) not null comment 'Template ID',
    lang varchar(6) not null default 'KO' comment '언어 코드',
    message_type varchar(20) not null comment '메세지 종류. COMMUNITY || CONTENT || LOGIN',
    isLastVersion boolean not null default true comment '마지막 버전여부',
    title varchar(200) comment 'title',
    message varchar(300) not null comment 'message',
    deep_link varchar(200) comment 'deep link'
) character set utf8mb4 comment 'Push 발송할 때 사용할 Message 정보.';

create table notification_center (
    id bigint auto_increment primary key comment 'Notification ID',
    user_id bigint not null comment 'User ID',
    status varchar(20) not null default 'NOT_READ' comment 'Notification 상태. NOT_READ || READ || DELETE',
    message_id bigint not null comment 'message ID',
    arguments varchar(200) comment 'message arguments(json format)',
    imageUrl varchar(200) comment 'image URL',
    modified_at datetime not null comment '수정일시',
    created_at datetime not null comment '생성일시'
) character set utf8mb4 comment '알림센터 내역';

create table notification_send_history (
    id bigint auto_increment primary key comment 'Notification ID',
    user_id bigint not null comment 'User ID',
    platform varchar(20) not null comment '플랫폼. WEB || ANDROID || IOS || SMS || EMAIL',
    target varchar(200) not null comment '대상. WEB - TOKEN, ANDROID || IOS - ARN TOKEN, SMS - PhoneNumber, EMAIL - EMAIL',
    message_id bigint comment 'message ID',
    email_file varchar(50) comment 'EMAIL 일때 파일명',
    arguments varchar(200) comment 'message arguments(json format)',
    created_at datetime not null comment '발송일시'
) character set utf8mb4 comment '알림센터 내역';

alter table post_likes add column modified_at datetime default now();

alter table post_likes add column status varchar(20) not null default 'LIKE' after post_id ;

insert into notification_template (id, description, send_types, available_arguments)
values ('VIDEO_UPLOAD', '동영상 업로드 시, 모든 유저에게', 'CENTER,APP_PUSH', 'USER_NICKNAME,CONTENT_ID'),
       ('COMMUNITY_COMMENT', '글에 댓글이 달렸을때, 글 작성자에게', 'CENTER,APP_PUSH', 'USER_NICKNAME,POST_ID,COMMENT_ID'),
       ('COMMUNITY_LIKE_1', '글에 하트가 처음 달렸을 때, 글 작성자에게', 'CENTER,APP_PUSH', 'USER_NICKNAME,POST_ID'),
       ('COMMUNITY_LIKE_20', '글에 하트가 20개 달렸을 때, 글 작성자에게', 'CENTER,APP_PUSH', 'USER_NICKNAME,POST_ID'),
       ('COMMUNITY_COMMENT_REPLY', '댓글에 대댓글이 달렸을 때, 댓글 작성자에게', 'CENTER,APP_PUSH', 'USER_NICKNAME,POST_ID,COMMENT_ID'),
       ('NO_LOGIN_2WEEKS', '로그인 안 한지 2주째...', 'CENTER,APP_PUSH', 'USER_NICKNAME');

insert into notification_message_center (template_id, lang, message_type, isLastVersion, message, deep_link)
values ('VIDEO_UPLOAD', 'KO', 'CONTENT', true, '오늘 영상 올라왔눈데 안볼꺼얌? 마부띠 똑땅해😣', 'link://content?id={{CONTENT_ID}}'),
       ('VIDEO_UPLOAD', 'KO', 'CONTENT', true, '새 영상 올라왔다. 안 보냐?', 'link://content?id={{CONTENT_ID}}'),
       ('VIDEO_UPLOAD', 'KO', 'CONTENT', true, '울 액히 같이 영상 보러갈꽈~ 👀', 'link://content?id={{CONTENT_ID}}'),
       ('VIDEO_UPLOAD', 'KO', 'CONTENT', true, '당신에게 바치는 내 영상. 내가 당신의 마음을 훔치는 그날을 위해! 치얼스 😏🍷', 'link://content?id={{CONTENT_ID}}'),
       ('COMMUNITY_COMMENT', 'KO', 'COMMUNITY', true, '{{USER_NICKNAME}}님 댓글 달렸또오😊얼른 화긴해죠 쀼~🥰', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT', 'KO', 'COMMUNITY', true, '니 글에 댓글 달렸다. 어서 확인해라', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT', 'KO', 'COMMUNITY', true, '울 액희, 댓글 달렸는데 확인할꽈~ 🤗', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT', 'KO', 'COMMUNITY', true, '당신의 게시물에 댓글 등록! 오늘도 눈부신 당신은 내 마음에 등록! 💖', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_LIKE_1', 'KO', 'COMMUNITY', true, 'ㅊㅋㅊㅋ! {{USER_NICKNAME}}님 첫번째 하트 배달와쪄요~❤️', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_1', 'KO', 'COMMUNITY', true, '첫번째 하트 달렸다. 좋겠네', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_1', 'KO', 'COMMUNITY', true, '울 액희 첫번째 하투 받아요😍', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_1', 'KO', 'COMMUNITY', true, '첫번째 하트를 그대 품안에~~~❤️️', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_20', 'KO', 'COMMUNITY', true, '{{USER_NICKNAME}}님 하뚜 20개 받아쪄~ 멋쨍이🎀쵝오얌💕️', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_20', 'KO', 'COMMUNITY', true, '하트 20개 받았다. 잘했다 ', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_20', 'KO', 'COMMUNITY', true, '울 액희 하투 20개 받았네! 우쭈쭈~😙', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_LIKE_20', 'KO', 'COMMUNITY', true, '당신의 게시물에 하트 20개! 아름다운 당신에게는 나의 사랑을 함께! 🌹️️', 'link://post?id={{POST_ID}}'),
       ('COMMUNITY_COMMENT_REPLY', 'KO', 'COMMUNITY', true, '{{USER_NICKNAME}}님 댓글에 답글 달려쪄>0<  인기쨍이 멋있쪄👍️️', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT_REPLY', 'KO', 'COMMUNITY', true, '니 댓글에 답글 남겼다. 확인 좀 하지?️', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT_REPLY', 'KO', 'COMMUNITY', true, '울 액희 댓글에 답글 달렸어 인기많아~ 질투나~😉️', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('COMMUNITY_COMMENT_REPLY', 'KO', 'COMMUNITY', true, '당신의 댓글에 또 답글이! 역쉬 유어 마이 베이붸~😘️', 'link://post?id={{POST_ID}}&comment_id={{COMMENT_ID}}'),
       ('NO_LOGIN_2WEEKS', 'KO', 'LOGIN', true, '오디가또! 나 안보고시포? 🤨️', 'link://main'),
       ('NO_LOGIN_2WEEKS', 'KO', 'LOGIN', true, '엄마가 들어오래. 마이뷰팁에 들어오래', 'link://main'),
       ('NO_LOGIN_2WEEKS', 'KO', 'LOGIN', true, '울 액희 나 안보고 싶뉘~ 서운해~😒', 'link://main'),
       ('NO_LOGIN_2WEEKS', 'KO', 'LOGIN', true, '당신을 오랫동안 보지 못해,,내 마음에 가뭄이,,,🌵', 'link://main');

insert into notification_message_push (template_id, lang, message_type, isLastVersion, message, deep_link)
select template_id, lang, message_type, isLastVersion, message, deep_link from notification_message_center;

-- ALTER TABLE post_likes DROP COLUMN modified_at;
-- ALTER TABLE post_likes DROP COLUMN status;

-- DROP TABLE notification_template;
-- DROP TABLE notification_message_center;
-- DROP TABLE notification_message_push;
-- DROP TABLE notification_center;
-- DELETE FROM flyway_schema_history WHERE version = '0145';