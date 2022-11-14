package com.jocoos.mybeautip.domain.home.vo;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;

public record SummaryCommunityCondition(Long categoryId,
                                        CommunityCategoryType categoryType,
                                        int size,
                                        Long memberId) {
}
