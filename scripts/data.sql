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


--
-- 스토어 소개, 데이터는 Google Drive - myBeautip 운영 - 계약 - 마이뷰팁 입점 신청서(응답)
--
update videos set description='' where 1;
update videos set description='화장품정품판매업체 스킨톡톡 입니다. 리더스,메디힐,차앤박,네이처,더샘등 뷰티브랜드 총집합' where id=3;
update videos set description='바른 선택, 건강한 모발관리' where id=4;
update videos set description='순도 수소원료를 사용한 수소비누와 미용제품을 제조 판매하고 있습니다.' where id=5;
update videos set description='순도 수소원료를 사용한 수소비누와 미용제품을 제조 판매하고 있습니다.' where id=6;
update videos set description='simple but better, better but balanced' where id=7;
update videos set description='"마르힐은 철저하게 과학과 첨단기술에 기초한 브랜드입니다.\n신중히 선별한 인공성분과 특별한 식물성분을 조합해 만든 효과적인 제품입니다"' where id=8;
update videos set description='천연 꽃물 화장품,닥터플로라' where id=9;
update videos set description='이제 에브리톡톡으로 건강두피와 풍성한 건강모발을 100세까지 가꾸어 보세요.' where id=10;
update videos set description='핏유어스킨은 당신의 피부에 꼭 맞는 화장품을 만들고자 탄생한 브랜드 입니다.' where id=11;
update videos set description='리바이탈래쉬코리아 한국본사에서 수입한 리바이탈래쉬 제품들만 취급합니다' where id=12;
update videos set description='자연 유래 성분으로 피부 건강을 생각하는 코스메틱 브랜드 달스킨' where id=13;
update videos set description='유럽 명품 수입비누 편집샵입니다' where id=15;
update videos set description='lovely, lively, friendly' where id=16;
update videos set description='정품 비비크림, 에스테틱화장품, 바디오일, 피부미용재료 전문 쇼핑몰' where id=17;
update videos set description='피부미용을 위한 유효성분을 생체흡수성 특허 패치로 피부에 녹아내리는 기능성화장품' where id=18;
update videos set description='소중한 우리 가족을 위한 건강 공간입니다. 건강한제품 안심제품을 준비했습니다.' where id=19;
update videos set description='고객서비스 만족도 굿서비스! 빅파워샵! 두피영양토닉 두피관리 천연샴푸 봄이오다 공식홈페이지' where id=20;
update videos set description='비비크림, 선크림, 에센스를 한번에? 페넌트면 해결된다!' where id=21;
update videos set description='화장품 자체 브랜드 기획, 유통, 무역 전문 엠케이 코스메틱입니다' where id=22;
update videos set description='지바이오팜의 천연물질로 만든 G-Shampoo & MIRACLE 보습크림' where id=23;
update videos set description='향기로운 순간 멈칫! 향기 대표 브랜드 멈칫' where id=24;
update videos set description='피부 전문 에스테틱 화장품 팜스/올로스/엠베카/레티팜' where id=25;
update videos set description='Skin Care & Makeup Try, Feel, Get' where id=26;
update videos set description='리프레 바디케어 솔루션 / 바디케어 전문 브랜드 / 비타민 샤워필터' where id=27;
update videos set description='LSM-EGF는 생명공학 전문기업 엘에스엠(주)에서 자체 개발한 화장품 전용 EGF (epidermal growth factor)입니다.' where id=28;
update videos set description='반갑습니다. 큐피투에 오신것을 환영합니다.' where id=31;
update videos set description='선택받은 자만이 누릴 수 있는 최고의 아름다움. 소중한 당신께 그 가치와 품격을 선보여 드립니다.' where id=30;
update videos set description='전세계 여성들의 아름다움을 위한 뷰티 & 헬스케어 브랜드' where id=29;
update videos set description='자연을 사랑하는 인간의 본질적이고 유전적인 소양' where id=37;
update videos set description='10년 후 내 피부를 위한 선택' where id=32;
update videos set description='반갑습니다. 제이앤코리아에 오신것을 환영합니다.' where id=33;
update videos set description='저희 소공자는 거품을 뺀 가격으로 최고 품질의 상품만을 고객 여러분들께 제공하고자 최선을 다하겠습니다.' where id=34;
update videos set description='재생크림,egf재생,피부과,흔적크림,시카크림,여드름,아토피,피부 재개발 공사크림,피부관리' where id=35;
update videos set description='우리 가족 건강을 위한 선택' where id=36;
update videos set description='이너뷰티케어 \'알롱\' 보이지않는 곳의 아름다움이 진정한 아름다움 입니다.' where id=38;
update videos set description='야시엔코 - 夜時의 기적' where id=39;