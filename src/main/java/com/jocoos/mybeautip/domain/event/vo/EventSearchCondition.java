package com.jocoos.mybeautip.domain.event.vo;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class EventSearchCondition {
    private final EventType type;
    private final Set<EventStatus> statuses;
    private final ZonedDateTime between;

    public EventSearchCondition(EventType type, Set<EventStatus> statuses, ZonedDateTime between) {
        this.type = type;
        this.statuses = statuses;
        this.between = between;
    }

    public EventSearchCondition(EventType type, Set<EventStatus> statuses) {
        this.type = type;
        this.statuses = statuses;
        this.between = null;
    }
}
