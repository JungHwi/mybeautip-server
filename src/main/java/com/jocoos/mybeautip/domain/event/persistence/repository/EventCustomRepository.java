package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;

import java.util.List;

public interface EventCustomRepository {
    List<Event> getEvents(EventSearchCondition condition);
    List<EventStatusResponse> getEventStatesWithNum();
}
