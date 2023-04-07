package com.jocoos.mybeautip.domain.home.code;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;

@RequiredArgsConstructor
@Getter
public enum SummaryType implements CodeValue {
    TOP_SUMMARY("메인 페이지 상단 커뮤니티 탭", 3),
    VOTE_TOP_SUMMARY("메인 페이지 상단 결정픽 커뮤니티 탭", 2),
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

    // FIXME: DB로 count 값을 분리하거나 count 관리할 다른 좋은 방법이 있을것, 현재는 요구사항이 복잡하지 않아서 유지
    public static int getCountOfTopSummary(CommunityCategoryType type) {
        if (VOTE.equals(type)) {
            return VOTE_TOP_SUMMARY.count;
        }
        return TOP_SUMMARY.count;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
