ALTER TABLE `members` DROP COLUMN `seq`;

RENAME TABLE `member_recommendations` TO `recommended_members`;
RENAME TABLE `goods_recommendations` TO `recommended_goods`;
RENAME TABLE `motd_recommendations` TO `recommended_motds`;
RENAME TABLE `keyword_recommendations` TO `recommended_keywords`;

RENAME TABLE `members_blocks` TO `member_blocks`;
RENAME TABLE `members_followings` TO `member_followings`;
RENAME TABLE `members_reports` TO `member_reports`;


