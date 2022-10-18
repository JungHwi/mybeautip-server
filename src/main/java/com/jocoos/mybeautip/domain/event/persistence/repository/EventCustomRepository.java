package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;

import java.util.List;
import java.util.Map;

public interface EventCustomRepository {
    List<Event> getEvents(EventSearchCondition condition);
    List<EventStatusResponse> getEventStatesWithNum();

    Long getTotalCount(EventSearchCondition condition);

    Map<Long, Long> getEventJoinCountMap(List<Long> eventIds);
}
