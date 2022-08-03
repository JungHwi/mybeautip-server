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
        EVENT_STATUS("event_status", "이벤트 상태"),
        COMMUNITY_CATEGORY_TYPE("community_category_type", "커뮤니티 카테고리 구분"),
        FILE_OPERATION_TYPE("file_operation_type", "파일 작업 구분"),
        ;

        private final String pageId;
        @Getter
        private final String text;
    }
}
