package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BroadcastRepository extends ExtendedQuerydslJpaRepository<Broadcast, Long>, BroadcastCustomRepository {

    int countByMemberId(long memberId);

    List<Broadcast> findByStatusIn(List<BroadcastStatus> broadcastStatusList);

    @Query("select b from Broadcast b join fetch b.category join fetch b.statistics where b.id = :id")
    Optional<Broadcast> findByIdWithFetch(@Param("id") Long broadcastId);

    @Query("select b.memberId from Broadcast b where b.memberId in :memberIds and b.status = :status")
    Set<Long> getCreatorIdInAndStatus(Set<Long> memberIds, BroadcastStatus status);

    Optional<Broadcast> findByVideoKey(Long videoKey);

    List<Broadcast> findAllByIdIn(List<Long> ids);

    boolean existsByIdAndMemberId(long broadcastId, long memberId);

    boolean existsByStatusAndMemberId(BroadcastStatus status, Long memberId);

    @Query("select distinct b.startedAt from Broadcast b where b.startedAt > now() order by b.startedAt asc")
    Slice<ZonedDateTime> findAllStartedAt(Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update BroadcastStatistics b set b.reportCount = b.reportCount + :count where b.id = :id")
    void addReportCountAndFlush(@Param("id") Long broadcastId, @Param("count") int count);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update BroadcastStatistics b set b.heartCount = b.heartCount + :count where b.id = :id")
    void addHeartCountAndFlush(@Param("id") Long broadcastId, @Param("count") int count);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Broadcast b set b.pausedAt = :pausedAt where b.videoKey = :videoKey")
    void updatePausedAt(@Param("videoKey") Long videoKey, @Param("pausedAt") ZonedDateTime pausedAt);
}
