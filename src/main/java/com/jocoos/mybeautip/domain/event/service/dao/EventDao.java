package com.jocoos.mybeautip.domain.event.service.dao;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.domain.event.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.jocoos.mybeautip.domain.event.code.EventStatus.PROGRESS;

@RequiredArgsConstructor
@Service
public class EventDao {

    private final EventRepository repository;
    private final EventJoinRepository joinRepository;

    @Transactional(readOnly = true)
    public List<Event> getVisibleEvents(EventType type) {
        EventSearchCondition condition = EventSearchCondition.builder()
                .type(type)
                .statuses(EventStatus.visibleEventStatus)
                .build();
        return repository.getEvents(condition);
    }

    @Transactional(readOnly = true)
    public List<Event> getProgressEvents(EventType type) {
        EventSearchCondition condition = EventSearchCondition.builder()
                .type(type)
                .statuses(Collections.singleton(PROGRESS))
                .between(ZonedDateTime.now())
                .build();
        return repository.getEvents(condition);
    }

    @Transactional(readOnly = true)
    public List<Event> summary(int eventNum) {
        EventSearchCondition condition = EventSearchCondition.builder()
                .statuses(Collections.singleton(PROGRESS))
                .between(ZonedDateTime.now())
                .paging(new Paging(eventNum))
                .build();
        return repository.getEvents(condition);
    }

    @Transactional(readOnly = true)
    public List<EventStatusResponse> getEventStatesWithNum() {
        return repository.getEventStatesWithNum();
    }

    @Transactional(readOnly = true)
    public List<EventSearchResult> getEventsWithJoinCount(EventSearchCondition condition) {
        return repository.getEventsWithJoinCount(condition);
    }

    @Transactional(readOnly = true)
    public Long getTotalCount(EventSearchCondition condition) {
        return repository.getTotalCount(condition);
    }

    @Transactional(readOnly = true)
    public Long getJoinCount(Event event) {
        return joinRepository.countByEvent(event);
    }
}
