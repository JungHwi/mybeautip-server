package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Long> {
    List<BroadcastNotification> findByBroadcastAndIsNotifyNeeded(Broadcast broadcast, boolean isNotifyNeeded);

    @Query("select n from BroadcastNotification n where n.memberId = :memberId and n.broadcast.id in :broadcastIds and n.isNotifyNeeded = :isNotifyNeeded")
    List<BroadcastNotification> findAllByMemberIdAndBroadcastIdInAndIsNotifyNeeded(Long memberId, Set<Long> broadcastIds, boolean isNotifyNeeded);
    Optional<BroadcastNotification> findByBroadcastAndMemberId(Broadcast broadcast, Long memberId);
}
