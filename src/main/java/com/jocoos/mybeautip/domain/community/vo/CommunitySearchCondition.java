package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;

public record CommunitySearchCondition(Long eventId, ZonedDateTime cursor, List<CommunityCategory> categories,
                                       Long memberId) {
    public boolean isCategoryDrip() {
        return this.categories.size() == 1 && DRIP.equals(this.categories.get(0).getType());
    }

    public boolean isFirstSearch() {
        return this.cursor == null;
    }
}
