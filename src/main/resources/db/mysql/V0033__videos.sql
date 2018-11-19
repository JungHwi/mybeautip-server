-- Alter goods_categories
ALTER TABLE `goods_categories` DROP COLUMN `display_on_pc`;
ALTER TABLE `goods_categories` DROP COLUMN `display_on_mobile`;

--
UPDATE `members` set avatar_url='https://s3-ap-northeast-2.amazonaws.com/mybeautip-dev/avatar/153515114023812.jpg', email='mybeautip@mybeatip.tv', intro='예뻐지는 TV - 마이뷰팁' where username='mybeautip.tv';


