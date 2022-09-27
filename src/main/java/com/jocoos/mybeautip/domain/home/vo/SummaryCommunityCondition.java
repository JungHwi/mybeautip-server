package com.jocoos.mybeautip.domain.home.vo;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SummaryCommunityCondition {
    private final Long categoryId;
    private final CommunityCategoryType categoryType;
    private final int size;

    public SummaryCommunityCondition(CommunityCategoryType categoryType, int size) {
        this.categoryId = null;
        this.categoryType = categoryType;
        this.size = size;
    }

    public SummaryCommunityCondition(Long categoryId, CommunityCategoryType categoryType, int size) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.size = size;
    }
}
