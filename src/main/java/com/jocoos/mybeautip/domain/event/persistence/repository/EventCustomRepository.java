package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;

import java.util.List;
import java.util.Set;

public interface EventCustomRepository {
    List<Event> getEvents(EventType type, Set<EventStatus> statuses);
}
