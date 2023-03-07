package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface BroadcastRepository extends ExtendedQuerydslJpaRepository<Broadcast, Long>, BroadcastCustomRepository {

    boolean existsByIdAndMemberId(long broadcastId, long memberId);

    @Query("select distinct b.startedAt from Broadcast b where b.startedAt > now() order by b.startedAt asc")
    Slice<ZonedDateTime> findAllStartedAt();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Broadcast b set b.reportCount = b.reportCount + :count where b.id = :id")
    void addReportCountAndFlush(@Param("id") long broadcastId, @Param("count") int count);
}
