package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface BroadcastRepository extends ExtendedQuerydslJpaRepository<Broadcast, Long>, BroadcastCustomRepository {

    boolean existsByIdAndMemberId(long broadcastId, long memberId);

    @Query("select distinct b.startedAt from Broadcast b where b.startedAt > now() order by b.startedAt asc")
    Slice<ZonedDateTime> findAllStartedAt(Pageable pageable);
}
