package com.jocoos.mybeautip.domain.event.service.dao;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
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

    @Transactional(readOnly = true)
    public List<Event> getVisibleEvents(EventType type) {
        return repository.getEvents(new EventSearchCondition(type, EventStatus.visibleEventStatus));
    }

    public List<Event> getProgressEvents(EventType type) {
        return repository.getEvents(new EventSearchCondition(type, Collections.singleton(PROGRESS), ZonedDateTime.now()));
    }
}
