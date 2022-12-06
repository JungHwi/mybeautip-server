package com.jocoos.mybeautip.domain.event.vo;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.global.vo.Paging;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.vo.Sort;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

@Getter
@Builder
public class EventSearchCondition {

    private final EventType type;
    private final Set<EventStatus> statuses;
    private final Boolean isVisible;
    private final ZonedDateTime between;
    private final Paging paging;
    private final Sort sort;
    private final SearchOption searchOption;

    public boolean isOrderByJoinCount() {
        return sort.isOrderByJoinCount();
    }

    public String getKeyword() {
        return searchOption == null ? null : searchOption.getKeyword();
    }

    public Date getStartAt() {
        return searchOption == null ? null : searchOption.getStartAtDate();
    }

    public Date getEndAt() {
        return searchOption == null ? null : searchOption.getEndAtDate();
    }

    public Boolean isTopFix() {
        return searchOption == null ? null : searchOption.getIsTopFix();
    }
}
