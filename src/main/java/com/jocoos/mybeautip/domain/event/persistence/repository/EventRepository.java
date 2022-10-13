package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends ExtendedQuerydslJpaRepository<Event, Long>, EventCustomRepository {

    List<Event> findByStatusIn(Set<EventStatus> statusSet, Pageable pageable);
    List<Event> findByTypeAndStatusIn(EventType eventType,Set<EventStatus> statusSet, Pageable pageable);
    List<Event> findByTypeAndStatus(EventType type, EventStatus status);
    Event findTopByTypeAndStatus(EventType type, EventStatus status);
    List<Event> findByIdIn(Set<Long> eventIds);
}
