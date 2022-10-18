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

    private final Long page;
    private final Long limit;
    private final String sort;
    private final String search;
    private final String keyword;
    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;
    
    public boolean isPaging() {
        return page != null;
    }
    
    public boolean isNoOffset() {
        return page == null && limit != null;
    }

    public long getOffset() {
        return page * limit;
    }
}
