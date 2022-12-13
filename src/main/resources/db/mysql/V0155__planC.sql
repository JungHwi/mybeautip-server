create table username_combination_word (
                                           id bigint auto_increment primary key comment '조합 단어 아이디',
                                           sequence tinyint not null comment '닉네임 위치',
                                           word varchar(20) not null comment '단어'
) comment '닉네임 조합 단어 정보' charset = utf8mb4;

create table dormant_member (
                                id                  bigint auto_increment primary key,
                                tag                 char(7)                           not null,
                                status              varchar(20)      default 'ACTIVE' not null,
                                visible             tinyint          default 0        null,
                                username            varchar(50)                       null,
                                birthday            date                              null,
                                avatar_url          varchar(500)                      null,
                                email               varchar(50)                       null,
                                phone_number        varchar(20)                       null,
                                point               int              default 0        null,
                                intro               varchar(200)                      null,
                                link                tinyint unsigned default '0'      null comment 'or flag, 1:facebook 2:naver 4:kakao',
                                permission          int unsigned     default '0'      null,
                                follower_count      int unsigned     default '0'      not null,
                                following_count     int unsigned     default '0'      not null,
                                report_count        int              default 0        null,
                                public_video_count  int unsigned     default '0'      null,
                                total_video_count   int unsigned     default '0'      not null,
                                revenue             int              default 0        null,
                                revenue_modified_at datetime(3)                       null,
                                pushable            tinyint(1)       default 0        not null,
                                created_at          datetime(3)                       not null,
                                modified_at         datetime(3)                       null,
                                last_logged_at      datetime                          null,
                                deleted_at          datetime(3)                       null
) comment '휴면 회원 정보' charset = utf8mb4;

create table operation_log (
                               id             bigint auto_increment comment '운영 로그 ID' primary key,
                               target_type    varchar(20)  not null comment '메뉴 구분',
                               operation_type varchar(20)  not null comment '운영 구분',
                               target_id      varchar(255) not null comment '운영 대상 ID',
                               description    varchar(500) null comment '설명, 사유, 메모 등',
                               created_by     bigint       null comment '운영자 ID',
                               created_at     datetime     not null comment '운영일시'
) comment '운영 로그 정보' charset = utf8mb4;

alter table member_memo change column id id bigint auto_increment;

alter table member_memo change column memo content varchar(2000);

alter table member_memo add column target_id bigint after content;

alter table member_memo add column created_by bigint after target_id;

alter table event add column is_top_fix boolean default false after sorting;

alter table placard add column sorting int after status;

alter table placard add column is_top_fix boolean default 0 after color;

alter table videos add column is_top_fix boolean default false after owner;

alter table videos add column sorting int after is_top_fix;

alter table videos add column is_recommended boolean default false after is_top_fix;

alter table videos add column status varchar(20) after video_key;

alter table notices change column min_version min_version varchar(50);
alter table notices change column max_version max_version varchar(50);

-- Rollback
# drop table username_combination_word;
# drop table dormant_member;
# drop table operation_log;
# alter table member_memo change column content memo varchar(2000);
# alter table member_memo drop column target_id;
# alter table member_memo drop column created_by;
# alter table event drop column is_top_fix;
# alter table placard drop column sorting;
# alter table placard drop column is_top_fix;
# alter table videos drop column is_top_fix;
# alter table videos drop column sorting;
# alter table videos drop column is_recommended;
# alter table videos drop column status;
# delete from flyway_schema_history where version = '0155';

-- Migration
# 휴면 복귀 팝업 추가
# INSERT INTO mybeautip.popup (type, display_type, status, image_file, description, started_at, ended_at, created_at) VALUES ('WAKEUP', 'DAILY', 'ACTIVE', 'wakeup_pop', '휴면 복귀 후 뜨는 팝업', '2022-07-11 08:00:00', '2030-07-31 23:59:59', '2022-11-30 06:35:00');
# INSERT INTO mybeautip.popup_button (popup_id, name, link_type) VALUES (3, '확인', 'PREVIOUS');
#
# 금지어 추가
# insert into banned_words (word, category) value ('집나간파우치', 1);
#
# 휴면 알림 Template
# INSERT INTO mybeautip.notification_message_center (template_id, lang, message_type, last_version, message, notification_link_type) VALUES ('DORMANT_MEMBER', 'KO', 'LOGIN', 1, '1년 동안 미접속으로 휴면 계정으로 전환될 예정이야! 서운해!', 'HOME');
# INSERT INTO mybeautip.notification_message_push (template_id, lang, message_type, last_version, title, message, notification_link_type) VALUES ('DORMANT_MEMBER', 'KO', 'LOGIN', 1, null, '1년 동안 미접속으로 휴면 계정으로 전환될 예정이야! 서운해!', 'HOME');
#
# 비디오 정보 수정
# update videos set status = 'OPEN' where deleted_at is null;
# update videos set status = 'DELETE' where deleted_at is not null;
# update videos set started_at = modified_at;
#
# 회원 메모 정보 수정
# update member_memo set target_id = id;

# 랜덤 닉네임 데이터 추가
# insert into username_combination_word (sequence, word) value (1, '더러운');    insert into username_combination_word (sequence, word) value (2, '봉구스');
# insert into username_combination_word (sequence, word) value (1, '번쩍한');    insert into username_combination_word (sequence, word) value (2, '무파마');
# insert into username_combination_word (sequence, word) value (1, '소리없는');   insert into username_combination_word (sequence, word) value (2, '고기파이');
# insert into username_combination_word (sequence, word) value (1, '화려한');    insert into username_combination_word (sequence, word) value (2, '토마호크');
# insert into username_combination_word (sequence, word) value (1, '억울한');    insert into username_combination_word (sequence, word) value (2, '카테고리');
# insert into username_combination_word (sequence, word) value (1, '낙천적인');   insert into username_combination_word (sequence, word) value (2, '육자배기');
# insert into username_combination_word (sequence, word) value (1, '고독한');    insert into username_combination_word (sequence, word) value (2, '이퀄라이저');
# insert into username_combination_word (sequence, word) value (1, '희망찬');    insert into username_combination_word (sequence, word) value (2, '사슴벌레');
# insert into username_combination_word (sequence, word) value (1, '민망한');    insert into username_combination_word (sequence, word) value (2, '털모자');
# insert into username_combination_word (sequence, word) value (1, '애틋한');    insert into username_combination_word (sequence, word) value (2, '이빨');
# insert into username_combination_word (sequence, word) value (1, '상한'); insert into username_combination_word (sequence, word) value (2, '쏘가리지짐');
# insert into username_combination_word (sequence, word) value (1, '부티나는');   insert into username_combination_word (sequence, word) value (2, '새벽별');
# insert into username_combination_word (sequence, word) value (1, '아름다운');   insert into username_combination_word (sequence, word) value (2, '돌멩이');
# insert into username_combination_word (sequence, word) value (1, '센스넘치는');  insert into username_combination_word (sequence, word) value (2, '존넨쉬름');
# insert into username_combination_word (sequence, word) value (1, '촉촉한');    insert into username_combination_word (sequence, word) value (2, '해바라기');
# insert into username_combination_word (sequence, word) value (1, '소름끼치는');  insert into username_combination_word (sequence, word) value (2, '장미');
# insert into username_combination_word (sequence, word) value (1, '시뻘건');    insert into username_combination_word (sequence, word) value (2, '코스모스');
# insert into username_combination_word (sequence, word) value (1, '노르스름한');  insert into username_combination_word (sequence, word) value (2, '양귀비');
# insert into username_combination_word (sequence, word) value (1, '샛노란');    insert into username_combination_word (sequence, word) value (2, '거베라');
# insert into username_combination_word (sequence, word) value (1, '푸른'); insert into username_combination_word (sequence, word) value (2, '라일락');
# insert into username_combination_word (sequence, word) value (1, '초록초록한');  insert into username_combination_word (sequence, word) value (2, '안개꽃');
# insert into username_combination_word (sequence, word) value (1, '주황빛도는');  insert into username_combination_word (sequence, word) value (2, '카네이션');
# insert into username_combination_word (sequence, word) value (1, '빛나는');    insert into username_combination_word (sequence, word) value (2, '튤립');
# insert into username_combination_word (sequence, word) value (1, '손때묻은');   insert into username_combination_word (sequence, word) value (2, '목련');
# insert into username_combination_word (sequence, word) value (1, '갖고싶은');   insert into username_combination_word (sequence, word) value (2, '동백꽃');
# insert into username_combination_word (sequence, word) value (1, '시꺼먼');    insert into username_combination_word (sequence, word) value (2, '능소화');
# insert into username_combination_word (sequence, word) value (1, '파란'); insert into username_combination_word (sequence, word) value (2, '왕');
# insert into username_combination_word (sequence, word) value (1, '찝찝한');    insert into username_combination_word (sequence, word) value (2, '세자');
# insert into username_combination_word (sequence, word) value (1, '꼬롬한');    insert into username_combination_word (sequence, word) value (2, '선비');
# insert into username_combination_word (sequence, word) value (1, '쨍한'); insert into username_combination_word (sequence, word) value (2, '노비');
# insert into username_combination_word (sequence, word) value (1, '촵촵한');    insert into username_combination_word (sequence, word) value (2, '망나니');
# insert into username_combination_word (sequence, word) value (1, '매트한');    insert into username_combination_word (sequence, word) value (2, '각설이');
# insert into username_combination_word (sequence, word) value (1, '비싼'); insert into username_combination_word (sequence, word) value (2, '백정');
# insert into username_combination_word (sequence, word) value (1, '가성비좋은');  insert into username_combination_word (sequence, word) value (2, '주모');
# insert into username_combination_word (sequence, word) value (1, '배고픈');    insert into username_combination_word (sequence, word) value (2, '기생');
# insert into username_combination_word (sequence, word) value (1, '귀여운');    insert into username_combination_word (sequence, word) value (2, '양반');
# insert into username_combination_word (sequence, word) value (1, '차가운');    insert into username_combination_word (sequence, word) value (2, '호위무사');
# insert into username_combination_word (sequence, word) value (1, '따뜻한');    insert into username_combination_word (sequence, word) value (2, '장군');
# insert into username_combination_word (sequence, word) value (1, '빛나는');    insert into username_combination_word (sequence, word) value (2, '사또');
# insert into username_combination_word (sequence, word) value (1, '멋쟁이');    insert into username_combination_word (sequence, word) value (2, '내시');
# insert into username_combination_word (sequence, word) value (1, '또랑또랑한');  insert into username_combination_word (sequence, word) value (2, '궁녀');
# insert into username_combination_word (sequence, word) value (1, '진실한');    insert into username_combination_word (sequence, word) value (2, '추노객');
# insert into username_combination_word (sequence, word) value (1, '쌀쌀한');    insert into username_combination_word (sequence, word) value (2, '다모');
# insert into username_combination_word (sequence, word) value (1, '따뜻한');    insert into username_combination_word (sequence, word) value (2, '방자');
# insert into username_combination_word (sequence, word) value (1, '미적지근한');  insert into username_combination_word (sequence, word) value (2, '립스틱');
# insert into username_combination_word (sequence, word) value (1, '화려한');    insert into username_combination_word (sequence, word) value (2, '마스카라');
# insert into username_combination_word (sequence, word) value (1, '어리둥절');   insert into username_combination_word (sequence, word) value (2, '아이라인');
# insert into username_combination_word (sequence, word) value (1, '하늘하늘한');  insert into username_combination_word (sequence, word) value (2, '아이브로우');
# insert into username_combination_word (sequence, word) value (1, '반짝이는');   insert into username_combination_word (sequence, word) value (2, '귀걸이');
# insert into username_combination_word (sequence, word) value (1, '새파란');    insert into username_combination_word (sequence, word) value (2, '피어싱');
# insert into username_combination_word (sequence, word) value (1, '푸르른');    insert into username_combination_word (sequence, word) value (2, '반지');
# insert into username_combination_word (sequence, word) value (1, '부드러운');   insert into username_combination_word (sequence, word) value (2, '목걸이');
# insert into username_combination_word (sequence, word) value (1, '매끈한');    insert into username_combination_word (sequence, word) value (2, '렌즈');
# insert into username_combination_word (sequence, word) value (1, '푸석푸석한');  insert into username_combination_word (sequence, word) value (2, '매니큐어');
# insert into username_combination_word (sequence, word) value (1, '거뭇거뭇한');  insert into username_combination_word (sequence, word) value (2, '패디큐어');
# insert into username_combination_word (sequence, word) value (1, '뜨거운');    insert into username_combination_word (sequence, word) value (2, '립밤');
# insert into username_combination_word (sequence, word) value (1, '보드라운');   insert into username_combination_word (sequence, word) value (2, '블러셔');
# insert into username_combination_word (sequence, word) value (1, '눈에띄는');   insert into username_combination_word (sequence, word) value (2, '글리터');
# insert into username_combination_word (sequence, word) value (1, '감성적인');   insert into username_combination_word (sequence, word) value (2, '뷰러');
# insert into username_combination_word (sequence, word) value (1, '이성적인');   insert into username_combination_word (sequence, word) value (2, '개코원숭이');
# insert into username_combination_word (sequence, word) value (1, '글썽이는');   insert into username_combination_word (sequence, word) value (2, '토끼');
# insert into username_combination_word (sequence, word) value (1, '일렁이는');   insert into username_combination_word (sequence, word) value (2, '암탉');
# insert into username_combination_word (sequence, word) value (1, '깜놀하는');   insert into username_combination_word (sequence, word) value (2, '푸들');
# insert into username_combination_word (sequence, word) value (1, '솔직한');    insert into username_combination_word (sequence, word) value (2, '꿀벌');
# insert into username_combination_word (sequence, word) value (1, '건강한');    insert into username_combination_word (sequence, word) value (2, '드워프');
# insert into username_combination_word (sequence, word) value (1, '튼튼한');    insert into username_combination_word (sequence, word) value (2, '엘프');
# insert into username_combination_word (sequence, word) value (1, '까칠한');    insert into username_combination_word (sequence, word) value (2, '드라큐라');
# insert into username_combination_word (sequence, word) value (1, '자유로운');   insert into username_combination_word (sequence, word) value (2, '호빗');
# insert into username_combination_word (sequence, word) value (1, '희미한');    insert into username_combination_word (sequence, word) value (2, '드루이드');
# insert into username_combination_word (sequence, word) value (1, '애틋한');    insert into username_combination_word (sequence, word) value (2, '오크');
# insert into username_combination_word (sequence, word) value (1, '품위있는');   insert into username_combination_word (sequence, word) value (2, '악마');
# insert into username_combination_word (sequence, word) value (1, '예의바른');   insert into username_combination_word (sequence, word) value (2, '천사');
# insert into username_combination_word (sequence, word) value (1, '까다로운');   insert into username_combination_word (sequence, word) value (2, '바실리스크');
# insert into username_combination_word (sequence, word) value (1, '사랑스러운');  insert into username_combination_word (sequence, word) value (2, '유니콘');
# insert into username_combination_word (sequence, word) value (1, '우아한');    insert into username_combination_word (sequence, word) value (2, '피부결');
# insert into username_combination_word (sequence, word) value (1, '쇼킹한');    insert into username_combination_word (sequence, word) value (2, '눈동자');
# insert into username_combination_word (sequence, word) value (1, '과묵한');    insert into username_combination_word (sequence, word) value (2, '별똥별');
# insert into username_combination_word (sequence, word) value (1, '헌신적인');   insert into username_combination_word (sequence, word) value (2, '신입생');
# insert into username_combination_word (sequence, word) value (1, '쪼매난');    insert into username_combination_word (sequence, word) value (2, '손길');
# insert into username_combination_word (sequence, word) value (1, '수줍은');    insert into username_combination_word (sequence, word) value (2, '입술');
# insert into username_combination_word (sequence, word) value (1, '최상급');    insert into username_combination_word (sequence, word) value (2, '봄');
# insert into username_combination_word (sequence, word) value (1, '보라색');    insert into username_combination_word (sequence, word) value (2, '여름');
# insert into username_combination_word (sequence, word) value (1, '헬싱키');    insert into username_combination_word (sequence, word) value (2, '가을');
# insert into username_combination_word (sequence, word) value (1, '뉴욕'); insert into username_combination_word (sequence, word) value (2, '겨울');
# insert into username_combination_word (sequence, word) value (1, '파리'); insert into username_combination_word (sequence, word) value (2, '이마');
# insert into username_combination_word (sequence, word) value (1, '밀라노');    insert into username_combination_word (sequence, word) value (2, '콧대');
# insert into username_combination_word (sequence, word) value (1, '서울'); insert into username_combination_word (sequence, word) value (2, '눈매');
# insert into username_combination_word (sequence, word) value (1, '부산'); insert into username_combination_word (sequence, word) value (2, '입꼬리');
# insert into username_combination_word (sequence, word) value (1, '오슬로');    insert into username_combination_word (sequence, word) value (2, '귓볼');
# insert into username_combination_word (sequence, word) value (1, '뉴델리');    insert into username_combination_word (sequence, word) value (2, '머릿결');
# insert into username_combination_word (sequence, word) value (1, '방콕'); insert into username_combination_word (sequence, word) value (2, '속눈썹');
# insert into username_combination_word (sequence, word) value (1, '리스본');    insert into username_combination_word (sequence, word) value (2, '턱선');
# insert into username_combination_word (sequence, word) value (1, '앙카라');    insert into username_combination_word (sequence, word) value (2, '기쁨');
# insert into username_combination_word (sequence, word) value (1, '시드니');    insert into username_combination_word (sequence, word) value (2, '행복');
# insert into username_combination_word (sequence, word) value (1, '멜버른');    insert into username_combination_word (sequence, word) value (2, '소녀');
# insert into username_combination_word (sequence, word) value (1, '런던'); insert into username_combination_word (sequence, word) value (2, '소년');
# insert into username_combination_word (sequence, word) value (1, '베를린');    insert into username_combination_word (sequence, word) value (2, '아이');
# insert into username_combination_word (sequence, word) value (1, '도쿄'); insert into username_combination_word (sequence, word) value (2, '도시여자');
# insert into username_combination_word (sequence, word) value (1, '빈');  insert into username_combination_word (sequence, word) value (2, '차도녀');
# insert into username_combination_word (sequence, word) value (1, '테헤란');    insert into username_combination_word (sequence, word) value (2, '말썽쟁이');
# insert into username_combination_word (sequence, word) value (1, '코펜하겐');   insert into username_combination_word (sequence, word) value (2, '장난꾸러기');
# insert into username_combination_word (sequence, word) value (1, '킨샤사');    insert into username_combination_word (sequence, word) value (2, '오라버니');
# insert into username_combination_word (sequence, word) value (1, '바그다드');   insert into username_combination_word (sequence, word) value (2, '콧수염');
# insert into username_combination_word (sequence, word) value (1, '토론토');    insert into username_combination_word (sequence, word) value (2, '턱수염');
# insert into username_combination_word (sequence, word) value (1, '벤쿠버');    insert into username_combination_word (sequence, word) value (2, '일자눈썹');
# insert into username_combination_word (sequence, word) value (1, '런던'); insert into username_combination_word (sequence, word) value (2, '오리너구리');
# insert into username_combination_word (sequence, word) value (1, '버밍엄');    insert into username_combination_word (sequence, word) value (2, '래서판다');
# insert into username_combination_word (sequence, word) value (1, '브라아튼');   insert into username_combination_word (sequence, word) value (2, '돌고래');
# insert into username_combination_word (sequence, word) value (1, '캠브리지');   insert into username_combination_word (sequence, word) value (2, '치타');
# insert into username_combination_word (sequence, word) value (1, '옥스퍼드');   insert into username_combination_word (sequence, word) value (2, '코끼리');
# insert into username_combination_word (sequence, word) value (1, '맨체스터');   insert into username_combination_word (sequence, word) value (2, '악어');
# insert into username_combination_word (sequence, word) value (1, '에든버러');   insert into username_combination_word (sequence, word) value (2, '담비');
# insert into username_combination_word (sequence, word) value (1, '리버풀');    insert into username_combination_word (sequence, word) value (2, '수달');
# insert into username_combination_word (sequence, word) value (1, '호치민');    insert into username_combination_word (sequence, word) value (2, '노루');
# insert into username_combination_word (sequence, word) value (1, '하노이');    insert into username_combination_word (sequence, word) value (2, '사슴');
# insert into username_combination_word (sequence, word) value (1, '푸꾸옥');    insert into username_combination_word (sequence, word) value (2, '공룡');
# insert into username_combination_word (sequence, word) value (1, '다낭'); insert into username_combination_word (sequence, word) value (2, '하마');
# insert into username_combination_word (sequence, word) value (1, '프놈펜');    insert into username_combination_word (sequence, word) value (2, '코뿔소');
# insert into username_combination_word (sequence, word) value (1, '라고스');    insert into username_combination_word (sequence, word) value (2, '호랑이');
# insert into username_combination_word (sequence, word) value (1, '트리폴리');   insert into username_combination_word (sequence, word) value (2, '사자');
# insert into username_combination_word (sequence, word) value (1, '치앙마이');   insert into username_combination_word (sequence, word) value (2, '늑대');
# insert into username_combination_word (sequence, word) value (1, '컨깬'); insert into username_combination_word (sequence, word) value (2, '여우');
# insert into username_combination_word (sequence, word) value (1, '방콕'); insert into username_combination_word (sequence, word) value (2, '표범');
# insert into username_combination_word (sequence, word) value (1, '아네테');    insert into username_combination_word (sequence, word) value (2, '독수리');
# insert into username_combination_word (sequence, word) value (1, '취리히');    insert into username_combination_word (sequence, word) value (2, '펭귄');
# insert into username_combination_word (sequence, word) value (1, '타슈켄트');   insert into username_combination_word (sequence, word) value (2, '고릴라');
# insert into username_combination_word (sequence, word) value (1, '카트만두');   insert into username_combination_word (sequence, word) value (2, '뱀장어');
# insert into username_combination_word (sequence, word) value (1, '딜리'); insert into username_combination_word (sequence, word) value (2, '침팬지');
# insert into username_combination_word (sequence, word) value (1, '로마'); insert into username_combination_word (sequence, word) value (2, '두루미');
# insert into username_combination_word (sequence, word) value (1, '모스크바');   insert into username_combination_word (sequence, word) value (2, '왕도마뱀');
# insert into username_combination_word (sequence, word) value (1, '브뤼셀');    insert into username_combination_word (sequence, word) value (2, '박쥐');
# insert into username_combination_word (sequence, word) value (1, '카불'); insert into username_combination_word (sequence, word) value (2, '들소');
# insert into username_combination_word (sequence, word) value (1, '나이로비');   insert into username_combination_word (sequence, word) value (2, '다람쥐');
# insert into username_combination_word (sequence, word) value (1, '프라하');    insert into username_combination_word (sequence, word) value (2, '라마');
# insert into username_combination_word (sequence, word) value (1, '소피아');    insert into username_combination_word (sequence, word) value (2, '당나귀');
# insert into username_combination_word (sequence, word) value (1, '알제'); insert into username_combination_word (sequence, word) value (2, '비둘기');
# insert into username_combination_word (sequence, word) value (1, '마닐라');    insert into username_combination_word (sequence, word) value (2, '곰');
# insert into username_combination_word (sequence, word) value (1, '암만'); insert into username_combination_word (sequence, word) value (2, '미어캣');
# insert into username_combination_word (sequence, word) value (1, '타이베이');   insert into username_combination_word (sequence, word) value (2, '나무늘보');
# insert into username_combination_word (sequence, word) value (1, '콜롬보');    insert into username_combination_word (sequence, word) value (2, '연어');
# insert into username_combination_word (sequence, word) value (1, '자카르타');   insert into username_combination_word (sequence, word) value (2, '해파리');
# insert into username_combination_word (sequence, word) value (1, '도하'); insert into username_combination_word (sequence, word) value (2, '해삼');
# insert into username_combination_word (sequence, word) value (1, '두샨베');    insert into username_combination_word (sequence, word) value (2, '말미잘');
# insert into username_combination_word (sequence, word) value (1, '모가디슈');   insert into username_combination_word (sequence, word) value (2, '타란튤라');
# insert into username_combination_word (sequence, word) value (1, '마드리드');   insert into username_combination_word (sequence, word) value (2, '개미');
# insert into username_combination_word (sequence, word) value (1, '더블린');    insert into username_combination_word (sequence, word) value (2, '개복치');
# insert into username_combination_word (sequence, word) value (1, '자그레브');   insert into username_combination_word (sequence, word) value (2, '개미핥기');
# insert into username_combination_word (sequence, word) value (1, '호박색');    insert into username_combination_word (sequence, word) value (2, '사막여우');
# insert into username_combination_word (sequence, word) value (1, '뻘겅색');    insert into username_combination_word (sequence, word) value (2, '카멜레온');
# insert into username_combination_word (sequence, word) value (1, '빨강색');    insert into username_combination_word (sequence, word) value (2, '마스크');
# insert into username_combination_word (sequence, word) value (1, '민감한');    insert into username_combination_word (sequence, word) value (2, '핸드크림');
# insert into username_combination_word (sequence, word) value (1, '예민한');    insert into username_combination_word (sequence, word) value (2, '향수');
# insert into username_combination_word (sequence, word) value (1, '부족한');    insert into username_combination_word (sequence, word) value (2, '아이크림');
# insert into username_combination_word (sequence, word) value (1, '넘치는');    insert into username_combination_word (sequence, word) value (2, '스킨');
# insert into username_combination_word (sequence, word) value (1, '수분부족');   insert into username_combination_word (sequence, word) value (2, '로션');
# insert into username_combination_word (sequence, word) value (1, '꼼꼼한');    insert into username_combination_word (sequence, word) value (2, '선크림');
# insert into username_combination_word (sequence, word) value (1, '예의없는');   insert into username_combination_word (sequence, word) value (2, '바디로션');
# insert into username_combination_word (sequence, word) value (1, '4가지없는');  insert into username_combination_word (sequence, word) value (2, '미백크림');
# insert into username_combination_word (sequence, word) value (1, '광나는');    insert into username_combination_word (sequence, word) value (2, '미스트');
# insert into username_combination_word (sequence, word) value (1, '맛있는');    insert into username_combination_word (sequence, word) value (2, '뷰러');
# insert into username_combination_word (sequence, word) value (1, '멋있는');    insert into username_combination_word (sequence, word) value (2, 'BB');
# insert into username_combination_word (sequence, word) value (1, '지친'); insert into username_combination_word (sequence, word) value (2, 'CC');
# insert into username_combination_word (sequence, word) value (1, '피곤한');    insert into username_combination_word (sequence, word) value (2, '오일');
# insert into username_combination_word (sequence, word) value (1, '괴로운');    insert into username_combination_word (sequence, word) value (2, '21호');
# insert into username_combination_word (sequence, word) value (1, '한심한');    insert into username_combination_word (sequence, word) value (2, '23호');
# insert into username_combination_word (sequence, word) value (1, '뒤끝있는');   insert into username_combination_word (sequence, word) value (2, '애교살');
# insert into username_combination_word (sequence, word) value (1, '뒤끝없는');   insert into username_combination_word (sequence, word) value (2, '바셀린');
# insert into username_combination_word (sequence, word) value (1, '앞뒤없는');   insert into username_combination_word (sequence, word) value (2, '훈남');
# insert into username_combination_word (sequence, word) value (1, '앞만보는');   insert into username_combination_word (sequence, word) value (2, '훈녀');
# insert into username_combination_word (sequence, word) value (1, '백점짜리');   insert into username_combination_word (sequence, word) value (2, '흔남');
# insert into username_combination_word (sequence, word) value (1, '만점짜리');   insert into username_combination_word (sequence, word) value (2, '흔녀');
# insert into username_combination_word (sequence, word) value (1, '막나가는');   insert into username_combination_word (sequence, word) value (2, '틴트');
# insert into username_combination_word (sequence, word) value (1, '가려린');    insert into username_combination_word (sequence, word) value (2, '컨실러');
# insert into username_combination_word (sequence, word) value (1, '듬직한');    insert into username_combination_word (sequence, word) value (2, '파우더');
# insert into username_combination_word (sequence, word) value (1, '떡대좋은');   insert into username_combination_word (sequence, word) value (2, '클렌징폼');
# insert into username_combination_word (sequence, word) value (1, '집에있는');   insert into username_combination_word (sequence, word) value (2, '쉐딩');
# insert into username_combination_word (sequence, word) value (1, '혼자있는');   insert into username_combination_word (sequence, word) value (2, '쿨톤');
# insert into username_combination_word (sequence, word) value (1, '외로운');    insert into username_combination_word (sequence, word) value (2, '웜톤');
# insert into username_combination_word (sequence, word) value (1, '슬픈'); insert into username_combination_word (sequence, word) value (2, '트러블성');
# insert into username_combination_word (sequence, word) value (1, '화나는');    insert into username_combination_word (sequence, word) value (2, '지성');
# insert into username_combination_word (sequence, word) value (1, '울고있는');   insert into username_combination_word (sequence, word) value (2, '민감성');
# insert into username_combination_word (sequence, word) value (1, '웃고있는');   insert into username_combination_word (sequence, word) value (2, '악건성');
# insert into username_combination_word (sequence, word) value (1, '나만아는');   insert into username_combination_word (sequence, word) value (2, '악지성');
# insert into username_combination_word (sequence, word) value (1, '합정동');    insert into username_combination_word (sequence, word) value (2, '건성');
# insert into username_combination_word (sequence, word) value (1, '상수동');    insert into username_combination_word (sequence, word) value (2, '클렌징오일');
# insert into username_combination_word (sequence, word) value (1, '연남동');    insert into username_combination_word (sequence, word) value (2, '리무버');
# insert into username_combination_word (sequence, word) value (1, '망원동');    insert into username_combination_word (sequence, word) value (2, '드라이버');
# insert into username_combination_word (sequence, word) value (1, '상암동');    insert into username_combination_word (sequence, word) value (2, '한류스타');
# insert into username_combination_word (sequence, word) value (1, '행신동');    insert into username_combination_word (sequence, word) value (2, '근린공원');
# insert into username_combination_word (sequence, word) value (1, '마산동');    insert into username_combination_word (sequence, word) value (2, '동상');
# insert into username_combination_word (sequence, word) value (1, '신림동');    insert into username_combination_word (sequence, word) value (2, '돌맹이');
# insert into username_combination_word (sequence, word) value (1, '운양동');    insert into username_combination_word (sequence, word) value (2, '비석');
# insert into username_combination_word (sequence, word) value (1, '봉천동');    insert into username_combination_word (sequence, word) value (2, '병풍');
# insert into username_combination_word (sequence, word) value (1, '불광동');    insert into username_combination_word (sequence, word) value (2, '행인');
# insert into username_combination_word (sequence, word) value (1, '권선동');    insert into username_combination_word (sequence, word) value (2, '콜라겐');
# insert into username_combination_word (sequence, word) value (1, '방이동');    insert into username_combination_word (sequence, word) value (2, '리프팅');
# insert into username_combination_word (sequence, word) value (1, '청담동');    insert into username_combination_word (sequence, word) value (2, '오가닉');
# insert into username_combination_word (sequence, word) value (1, '압구정동');   insert into username_combination_word (sequence, word) value (2, '베이비');
# insert into username_combination_word (sequence, word) value (1, '성수동');    insert into username_combination_word (sequence, word) value (2, '꿀피부');
# insert into username_combination_word (sequence, word) value (1, '잠실동');    insert into username_combination_word (sequence, word) value (2, '눈썹문신');
# insert into username_combination_word (sequence, word) value (1, '석촌동');    insert into username_combination_word (sequence, word) value (2, '스펀지');
# insert into username_combination_word (sequence, word) value (1, '굵은'); insert into username_combination_word (sequence, word) value (2, '은이빨');
# insert into username_combination_word (sequence, word) value (1, '물멕이는');   insert into username_combination_word (sequence, word) value (2, '금이빨');
# insert into username_combination_word (sequence, word) value (1, '술먹이는');   insert into username_combination_word (sequence, word) value (2, '사마귀');
# insert into username_combination_word (sequence, word) value (1, '화정동');    insert into username_combination_word (sequence, word) value (2, '아토피');
# insert into username_combination_word (sequence, word) value (1, '능곡동');    insert into username_combination_word (sequence, word) value (2, '관자놀이');
# insert into username_combination_word (sequence, word) value (1, '화상입은');   insert into username_combination_word (sequence, word) value (2, '사이비');
# insert into username_combination_word (sequence, word) value (1, '갬성적인');   insert into username_combination_word (sequence, word) value (2, '유모차');
# insert into username_combination_word (sequence, word) value (1, '지켜주는');   insert into username_combination_word (sequence, word) value (2, '딸랑이');
# insert into username_combination_word (sequence, word) value (1, '공격적인');   insert into username_combination_word (sequence, word) value (2, '막차');
# insert into username_combination_word (sequence, word) value (1, '자극적인');   insert into username_combination_word (sequence, word) value (2, '휘발유');
# insert into username_combination_word (sequence, word) value (1, '매운'); insert into username_combination_word (sequence, word) value (2, '인사봇');
# insert into username_combination_word (sequence, word) value (1, '달달한');    insert into username_combination_word (sequence, word) value (2, '센터');
# insert into username_combination_word (sequence, word) value (1, '매콤한');    insert into username_combination_word (sequence, word) value (2, '파운데이션');
# insert into username_combination_word (sequence, word) value (1, '쌉싸름한');   insert into username_combination_word (sequence, word) value (2, '반곱슬');
# insert into username_combination_word (sequence, word) value (1, '달콤한');    insert into username_combination_word (sequence, word) value (2, '뿔태');
# insert into username_combination_word (sequence, word) value (1, '불타오르는');  insert into username_combination_word (sequence, word) value (2, '주접용');
# insert into username_combination_word (sequence, word) value (1, '강력한');    insert into username_combination_word (sequence, word) value (2, '삐삐');
# insert into username_combination_word (sequence, word) value (1, '흔들리는');   insert into username_combination_word (sequence, word) value (2, '무전기');
# insert into username_combination_word (sequence, word) value (1, '내려오는');   insert into username_combination_word (sequence, word) value (2, '콤퓨타');
# insert into username_combination_word (sequence, word) value (1, '올라가는');   insert into username_combination_word (sequence, word) value (2, '무야호');
# insert into username_combination_word (sequence, word) value (1, '떨어지는');   insert into username_combination_word (sequence, word) value (2, '리듬타기');
# insert into username_combination_word (sequence, word) value (1, '오나전');    insert into username_combination_word (sequence, word) value (2, '소리질러');
# insert into username_combination_word (sequence, word) value (1, '준비된');    insert into username_combination_word (sequence, word) value (2, '래퍼');
# insert into username_combination_word (sequence, word) value (1, '여기서부터');  insert into username_combination_word (sequence, word) value (2, '아티스트');
# insert into username_combination_word (sequence, word) value (1, '잔인한');    insert into username_combination_word (sequence, word) value (2, '고구마');
# insert into username_combination_word (sequence, word) value (1, '심각한');    insert into username_combination_word (sequence, word) value (2, '드르륵탁');
# insert into username_combination_word (sequence, word) value (1, '대충'); insert into username_combination_word (sequence, word) value (2, '천재임');
# insert into username_combination_word (sequence, word) value (1, '막던진');    insert into username_combination_word (sequence, word) value (2, '북극곰');
# insert into username_combination_word (sequence, word) value (1, '기고한');    insert into username_combination_word (sequence, word) value (2, '크크루삥뽕');
# insert into username_combination_word (sequence, word) value (1, '유서깊은');   insert into username_combination_word (sequence, word) value (2, '갓생');
# insert into username_combination_word (sequence, word) value (1, '킹받는');    insert into username_combination_word (sequence, word) value (2, '양파링');
# insert into username_combination_word (sequence, word) value (1, '망가진');    insert into username_combination_word (sequence, word) value (2, '덕후');
# insert into username_combination_word (sequence, word) value (1, '고장난');    insert into username_combination_word (sequence, word) value (2, '에디터');
# insert into username_combination_word (sequence, word) value (1, '버려진');    insert into username_combination_word (sequence, word) value (2, '도시락');
# insert into username_combination_word (sequence, word) value (1, '주어온');    insert into username_combination_word (sequence, word) value (2, '개발도상국');
# insert into username_combination_word (sequence, word) value (1, '숨참고');    insert into username_combination_word (sequence, word) value (2, '금은방');
# insert into username_combination_word (sequence, word) value (1, '자나깨나');   insert into username_combination_word (sequence, word) value (2, '뽀로로');
# insert into username_combination_word (sequence, word) value (1, '스쳐가는');   insert into username_combination_word (sequence, word) value (2, '루피');
# insert into username_combination_word (sequence, word) value (1, '음흉한');    insert into username_combination_word (sequence, word) value (2, '인싸');
# insert into username_combination_word (sequence, word) value (1, '잊혀진');    insert into username_combination_word (sequence, word) value (2, '아싸');
# insert into username_combination_word (sequence, word) value (1, '삼귀는');    insert into username_combination_word (sequence, word) value (2, '비상이다');
# insert into username_combination_word (sequence, word) value (1, '사귀는');    insert into username_combination_word (sequence, word) value (2, '여드름');
# insert into username_combination_word (sequence, word) value (1, '헤어진');    insert into username_combination_word (sequence, word) value (2, '주근깨');
# insert into username_combination_word (sequence, word) value (1, '둠칫둠칫');   insert into username_combination_word (sequence, word) value (2, '기미');
# insert into username_combination_word (sequence, word) value (1, '쵸크쵸크');   insert into username_combination_word (sequence, word) value (2, '주름살');
# insert into username_combination_word (sequence, word) value (1, '간지러운');   insert into username_combination_word (sequence, word) value (2, '오돌뼈');
# insert into username_combination_word (sequence, word) value (1, '치사한');    insert into username_combination_word (sequence, word) value (2, '좁쌀');
# insert into username_combination_word (sequence, word) value (1, '삐친'); insert into username_combination_word (sequence, word) value (2, '계절');
# insert into username_combination_word (sequence, word) value (1, '미쳐버린');   insert into username_combination_word (sequence, word) value (2, '날씨');
# insert into username_combination_word (sequence, word) value (1, '건조한');    insert into username_combination_word (sequence, word) value (2, '물방울');
# insert into username_combination_word (sequence, word) value (1, '오돌토돌');   insert into username_combination_word (sequence, word) value (2, '빗방울');
# insert into username_combination_word (sequence, word) value (1, '멀어지는');   insert into username_combination_word (sequence, word) value (2, '피지');
# insert into username_combination_word (sequence, word) value (1, '간드러진');   insert into username_combination_word (sequence, word) value (2, '에센스');
# insert into username_combination_word (sequence, word) value (1, '기름진');    insert into username_combination_word (sequence, word) value (2, '앰플');
# insert into username_combination_word (sequence, word) value (1, '자랑스런');   insert into username_combination_word (sequence, word) value (2, '프라이머');
# insert into username_combination_word (sequence, word) value (1, '어른스러운');  insert into username_combination_word (sequence, word) value (2, '펄땡이');
# insert into username_combination_word (sequence, word) value (1, '씩씩한');    insert into username_combination_word (sequence, word) value (2, '단칸방');
# insert into username_combination_word (sequence, word) value (1, '독립적인');   insert into username_combination_word (sequence, word) value (2, '옥탑방');
# insert into username_combination_word (sequence, word) value (1, '습습한');    insert into username_combination_word (sequence, word) value (2, '옥장판');
# insert into username_combination_word (sequence, word) value (1, '꼬깃꼬깃한');  insert into username_combination_word (sequence, word) value (2, '만물상');
# insert into username_combination_word (sequence, word) value (1, '구부러진');   insert into username_combination_word (sequence, word) value (2, '백과사전');
# insert into username_combination_word (sequence, word) value (1, '향기로운');   insert into username_combination_word (sequence, word) value (2, '젤네일');
# insert into username_combination_word (sequence, word) value (1, '전통적인');   insert into username_combination_word (sequence, word) value (2, '맛집');
# insert into username_combination_word (sequence, word) value (1, '도톰한');    insert into username_combination_word (sequence, word) value (2, '반영구');
# insert into username_combination_word (sequence, word) value (1, '폭주하는');   insert into username_combination_word (sequence, word) value (2, '리액션');
# insert into username_combination_word (sequence, word) value (1, '잃어버린');   insert into username_combination_word (sequence, word) value (2, '언니');
# insert into username_combination_word (sequence, word) value (1, '도망간');    insert into username_combination_word (sequence, word) value (2, '동생');
# insert into username_combination_word (sequence, word) value (1, '넘어진');    insert into username_combination_word (sequence, word) value (2, '형아');
# insert into username_combination_word (sequence, word) value (1, '일어선');    insert into username_combination_word (sequence, word) value (2, '꼰대');
# insert into username_combination_word (sequence, word) value (1, '달리는');    insert into username_combination_word (sequence, word) value (2, '점술가');
# insert into username_combination_word (sequence, word) value (1, '점성있는');   insert into username_combination_word (sequence, word) value (2, '타로카드');
# insert into username_combination_word (sequence, word) value (1, '깜빡한');    insert into username_combination_word (sequence, word) value (2, '핸드백');
# insert into username_combination_word (sequence, word) value (1, '복슬복슬한');  insert into username_combination_word (sequence, word) value (2, '백팩');
# insert into username_combination_word (sequence, word) value (1, '사각사각');   insert into username_combination_word (sequence, word) value (2, '스냅백');
# insert into username_combination_word (sequence, word) value (1, '살찐'); insert into username_combination_word (sequence, word) value (2, '미미');
# insert into username_combination_word (sequence, word) value (1, '빼빼마른');   insert into username_combination_word (sequence, word) value (2, '쥬쥬');
# insert into username_combination_word (sequence, word) value (1, '통통한');    insert into username_combination_word (sequence, word) value (2, '인형');
# insert into username_combination_word (sequence, word) value (1, '날씬한');    insert into username_combination_word (sequence, word) value (2, '세탁소');
# insert into username_combination_word (sequence, word) value (1, '동이난');    insert into username_combination_word (sequence, word) value (2, '이자카야');
# insert into username_combination_word (sequence, word) value (1, '메마른');    insert into username_combination_word (sequence, word) value (2, 'MZ');
# insert into username_combination_word (sequence, word) value (1, '이제는');    insert into username_combination_word (sequence, word) value (2, 'X세대');
# insert into username_combination_word (sequence, word) value (1, '뜨끈뜨끈한');  insert into username_combination_word (sequence, word) value (2, '신세대');
# insert into username_combination_word (sequence, word) value (1, '따끈따끈한');  insert into username_combination_word (sequence, word) value (2, '라떼는말야');
# insert into username_combination_word (sequence, word) value (1, '따끔한');    insert into username_combination_word (sequence, word) value (2, '제비');
# insert into username_combination_word (sequence, word) value (1, '통수치는');   insert into username_combination_word (sequence, word) value (2, '참새');
# insert into username_combination_word (sequence, word) value (1, '걱정하는');   insert into username_combination_word (sequence, word) value (2, '까치');
# insert into username_combination_word (sequence, word) value (1, '노래하는');   insert into username_combination_word (sequence, word) value (2, '까마귀');
# insert into username_combination_word (sequence, word) value (1, '영원한');    insert into username_combination_word (sequence, word) value (2, '독수리');
# insert into username_combination_word (sequence, word) value (1, '빛바랜');    insert into username_combination_word (sequence, word) value (2, '비둘기');
# insert into username_combination_word (sequence, word) value (1, '휘파람부는');  insert into username_combination_word (sequence, word) value (2, '아이디어');
# insert into username_combination_word (sequence, word) value (1, '꺠끗한');    insert into username_combination_word (sequence, word) value (2, '뒤통수');
# insert into username_combination_word (sequence, word) value (1, '귀찮은');    insert into username_combination_word (sequence, word) value (2, '다이아몬드');
# insert into username_combination_word (sequence, word) value (1, '어설픈');    insert into username_combination_word (sequence, word) value (2, '루비');
# insert into username_combination_word (sequence, word) value (1, '어리버리한');  insert into username_combination_word (sequence, word) value (2, '에메랄드');
# insert into username_combination_word (sequence, word) value (1, '똑같은');    insert into username_combination_word (sequence, word) value (2, '큐빅');
# insert into username_combination_word (sequence, word) value (1, '남다른');    insert into username_combination_word (sequence, word) value (2, '옥구슬');
# insert into username_combination_word (sequence, word) value (1, '신박한');    insert into username_combination_word (sequence, word) value (2, '진주');
# insert into username_combination_word (sequence, word) value (1, '신기한');    insert into username_combination_word (sequence, word) value (2, '사파이어');


# insert into member_memo(id, content, target_id, created_by, created_at, modified_at) value (1, 'test', 1, 1, now(), now());
# INSERT INTO dormant_member (id, tag, status, visible, username, birthday, avatar_url, email, phone_number, point, intro, link, permission, follower_count, following_count, report_count, public_video_count, total_video_count, revenue, revenue_modified_at, pushable, created_at, modified_at, last_logged_at, deleted_at) VALUES (44, 'KBUXN60', 'ACTIVE', 0, '뷰띠fx38213', null, 'img_profile_default.png', '', null, 0, null, 2, 19, 0, 0, 0, 0, 0, 0, null, 1, '2022-09-15 09:14:59.719', '2022-09-15 09:14:59.719', '2022-09-15 09:15:00', null);
# update videos set videos.is_recommended=true where id in (3, 15002, 15003);
# update member_memo set created_by=1;
