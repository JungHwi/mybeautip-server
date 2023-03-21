package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Long> {
    List<BroadcastNotification> findByBroadcastAndIsNotifyNeeded(Broadcast broadcast, boolean isNotifyNeeded);
}
