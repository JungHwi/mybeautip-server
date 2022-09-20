package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;

@RequiredArgsConstructor
@Getter
public class CommunitySearchCondition {
    private final Long eventId;
    private final boolean isFirstSearch;
    private final ZonedDateTime cursor;
    private final List<CommunityCategory> categories;

    public boolean isCategoryDrip() {
        return this.categories.size() == 1 && DRIP.equals(this.categories.get(0).getType());
    }
}
