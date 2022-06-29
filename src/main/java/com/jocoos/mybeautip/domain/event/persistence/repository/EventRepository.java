package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends DefaultJpaRepository<Event, Long> {

    Slice<Event> findByIdLessThanAndStatusIn(long eventId, Set<EventStatus> statusSet, Pageable pageable);
    List<Event> findByTypeAndStatus(EventType type, EventStatus status);
}
