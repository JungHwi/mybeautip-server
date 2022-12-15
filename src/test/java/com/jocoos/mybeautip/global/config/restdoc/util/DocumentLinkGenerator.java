package com.jocoos.mybeautip.global.config.restdoc.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DocumentLinkGenerator {
    public static String generateLinkCode(DocUrl docUrl) {
        return String.format("link:/docs/common/%s.html[%s ,role=\"popup\"]", docUrl.pageId, docUrl.text);
    }

    public static String generateText(DocUrl docUrl) {
        return String.format("%s %s", docUrl.text, "description");
    }

    @RequiredArgsConstructor
    public enum DocUrl {
        MEMBER_STATUS("member_status", "회원 상태"),
        GRANT_TYPE("grant_type", "소셜 로그인 서비스 구분"),
        EVENT_TYPE("event_type", "이벤트 구분"),
        EVENT_STATUS("event_status", "이벤트 상태"),
        EVENT_PRODUCT_TYPE("event_product_type", "이벤트 상품 구분"),
        COMMUNITY_STATUS("community_status", "커뮤니티 상태"),
        COMMUNITY_CATEGORY_TYPE("community_category_type", "커뮤니티 카테고리 구분"),
        VIDEO_CATEGORY_TYPE("video_category_type", "비디오 카테고리 구분"),
        VIDEO_MASK_TYPE("video_mask_type", "비디오 마스크 구분"),
        FILE_OPERATION_TYPE("file_operation_type", "파일 작업 구분"),
        TERM_TYPE("term_type", "약관 타입"),
        SKIN_TYPE("skin_type", "피부 타입"),
        SKIN_WORRY("skin_worry", "피부 고민"),
        SORT_FIELD("sort_field", "정렬 필드"),
        SEARCH_TYPE("search_type", "검색 타입"),
        SCRAP_TYPE("scrap_type", "스크랩 타입"),
        PLACARD_LINK_TYPE("placard_link_type", "플랜카드 연결 타입"),
        POINT_STATUS("point_status", "포인트 상태"),
        POPUP_STATUS("popup_status", "팝업 상태 구분"),
        POPUP_DISPLAY_TYPE("popup_display_type", "팝업 노출 구분"),
        BUTTON_LINK_TYPE("button_link_type", "버튼 링크 구분"),
        OPERATION_TARGET_TYPE("operation_target_type", "운영 대상 구분"),
        OPERATION_TYPE("operation_type", "운영 행위 구분"),
        PLACARD_STATUS("placard_status", "플랜카드 상태"),
        VIDEO_COMMENT_STATUS("video_comment_status", "비디오 댓글 상태"),
        VIDEO_STATUS("video_status", "비디오 상태"),
        ROLE("role", "멤버 권한")
        ;

        private final String pageId;
        @Getter
        private final String text;
    }
}
