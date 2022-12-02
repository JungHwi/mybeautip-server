package com.jocoos.mybeautip.domain.home.code;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SummaryType implements CodeValue {
    TOP_SUMMARY("메인 페이지 상단 커뮤니티 탭", 3),
    VOTE_SUMMARY("메인 페이지 결정픽 탭", 7),
    BLIND_SUMMARY("메인 페이지 블라인드 탭", 5),
    VIDEO_SUMMARY("비디오 탭", 5),
    EVENT_SUMMARY("이벤트 탭", 7);

    private static final String SUMMARY = "_SUMMARY";
    private final String description;
    private final int count;

    public static SummaryType getByCategory(CommunityCategoryType type) {
        return SummaryType.valueOf(type.getName() + SUMMARY);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
