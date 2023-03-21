package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BroadcastNotificationDao {

    private final BroadcastNotificationRepository repository;

    @Transactional(readOnly = true)
    public List<BroadcastNotification> getNotifications(Broadcast broadcast) {
        return repository.findByBroadcastAndIsNotifyNeeded(broadcast, true);
    }
}
