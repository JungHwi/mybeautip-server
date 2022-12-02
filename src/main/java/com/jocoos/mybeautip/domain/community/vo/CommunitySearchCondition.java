package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;

@Builder
public record CommunitySearchCondition(Long eventId,
                                       Long memberId,
                                       ZonedDateTime cursor,
                                       Pageable pageable,
                                       SearchOption searchOption,
                                       List<CommunityCategory> categories) {

    public boolean isCategoryDrip() {
        return this.categories.size() == 1 && DRIP.equals(this.categories.get(0).getType());
    }

    public boolean isFirstSearch() {
        return this.cursor == null;
    }

    public Sort getSort() {
        return pageable.getSort();
    }

    public long getOffset() {
        return pageable.getOffset();
    }

    public long getPageSize() {
        return pageable.getPageSize();
    }

    public ZonedDateTime getStartAt() {
        return searchOption == null ? null : searchOption.getStartAt();
    }

    public ZonedDateTime getEndAt() {
        return searchOption == null ? null : searchOption.getEndAt();
    }

    public Boolean isReported() {
        return searchOption == null ? null : searchOption.getIsReported();
    }

}
