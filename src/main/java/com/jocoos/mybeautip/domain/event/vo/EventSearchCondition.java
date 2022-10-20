package com.jocoos.mybeautip.domain.event.vo;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Builder
public class EventSearchCondition {
    private final EventType type;
    private final Set<EventStatus> statuses;
    private final ZonedDateTime between;
    private final Paging paging;
    private final Sort sort;
    private final SearchKeyword searchKeyword;

    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;

    public boolean isOrderByJoinCount() {
        return sort.isOrderByJoinCount();
    }

    public String getKeyword() {
        return searchKeyword == null ? null : searchKeyword.getKeyword();
    }
}
