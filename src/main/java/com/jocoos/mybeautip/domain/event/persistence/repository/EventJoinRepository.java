package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface EventJoinRepository extends DefaultJpaRepository<EventJoin, Long> {

    EventJoin findTopByMemberIdAndEventId(Long memberId, Long eventId);
    Slice<EventJoin> findByMemberIdAndIdLessThan(long memberId, long cursor, Pageable pageable);
    boolean existsByMemberIdAndEventId(Long memberId, Long eventId);
}
