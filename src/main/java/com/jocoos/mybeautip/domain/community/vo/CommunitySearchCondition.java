package com.jocoos.mybeautip.domain.community.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.vo.SearchKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;

@RequiredArgsConstructor
@Getter
public class CommunitySearchCondition {

    private final Long eventId;

    private final Long memberId;
    private final ZonedDateTime cursor;
    private final Pageable pageable;
    private final SearchKeyword searchKeyword;
    private List<CommunityCategory> categories;

    public CommunitySearchCondition(Long eventId, ZonedDateTime cursor, List<CommunityCategory> categories) {
        this.eventId = eventId;
        this.cursor = cursor;
        this.categories = categories;
        this.memberId = null;
        this.pageable = null;
        this.searchKeyword = null;
    }

    @Builder
    public CommunitySearchCondition(Pageable pageable, SearchKeyword searchKeyword) {
        this.eventId = null;
        this.cursor = null;
        this.memberId = null;
        this.pageable = pageable;
        this.searchKeyword = searchKeyword;
    }

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
        return searchKeyword == null ? null : searchKeyword.getStartAt();
    }

    public ZonedDateTime getEndAt() {
        return searchKeyword == null ? null : searchKeyword.getEndAt();
    }

    public void setCategories(List<CommunityCategory> categories) {
        this.categories = categories;
    }
}
