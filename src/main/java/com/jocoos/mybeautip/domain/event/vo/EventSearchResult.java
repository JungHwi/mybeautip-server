package com.jocoos.mybeautip.domain.event.vo;

import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class EventSearchResult {
    private final Event event;
    private final Long joinCount;

    @QueryProjection
    public EventSearchResult(Event event, Long joinCount) {
        this.event = event;
        this.joinCount = joinCount;
    }
}
