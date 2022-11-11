package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends ExtendedQuerydslJpaRepository<Event, Long>, EventCustomRepository {

    List<Event> findByTypeAndStatus(EventType type, EventStatus status);
    Event findTopByTypeAndStatus(EventType type, EventStatus status);
    List<Event> findByIdIn(Set<Long> eventIds);

    List<Event> findByReservationAtLessThanEqualAndStatus(ZonedDateTime now, EventStatus status);
    List<Event> findByEndAtLessThanEqualAndStatus(ZonedDateTime now, EventStatus status);

    @Modifying
    @Query("UPDATE Event e SET e.status = :status WHERE e.id in :ids")
    int updateStatus(List<Long> ids, EventStatus status);
}
