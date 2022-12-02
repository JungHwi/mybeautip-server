package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface EventJoinRepository extends ExtendedQuerydslJpaRepository<EventJoin, Long> {

    EventJoin findTopByMemberIdAndEventId(Long memberId, Long eventId);
    Slice<EventJoin> findByMemberIdAndIdLessThan(long memberId, long cursor, Pageable pageable);
    boolean existsByMemberIdAndEventId(Long memberId, Long eventId);

    Long countByEvent(Event event);
}
