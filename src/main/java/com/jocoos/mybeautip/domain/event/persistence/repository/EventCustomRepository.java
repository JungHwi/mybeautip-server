package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;

import java.util.List;
import java.util.Map;

public interface EventCustomRepository {
    List<Event> getEvents(EventSearchCondition condition);
    Map<EventStatus, Long> getEventStatesWithNum();

    List<EventSearchResult> getEventsWithJoinCount(EventSearchCondition condition);

    Long getTotalCount(EventSearchCondition condition);

}
