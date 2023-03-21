package com.jocoos.mybeautip.domain.broadcast.event;

import com.jocoos.mybeautip.domain.broadcast.event.BroadcastNotificationEvent.BroadcastBulkEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastNotificationEvent.BroadcastEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.notification.service.impl.BroadcastScheduleChangeNotificationService;
import com.jocoos.mybeautip.domain.notification.service.impl.BroadcastStatusChangeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BroadcastNotificationEventHandler {

    private final BroadcastDao broadcastDao;
    private final BroadcastStatusChangeNotificationService statusChangeNotificationService;
    private final BroadcastScheduleChangeNotificationService scheduleChangeNotificationService;

    @Async
    @Transactional
    @TransactionalEventListener
    public void publish(BroadcastEditNotificationEvent event) {
        BroadcastEditResult result = event.result();
        sendStatusChangedNotification(result);
        sendScheduleChangedNotification(result);
    }

    @Async
    @Transactional
    @TransactionalEventListener(fallbackExecution = true)
    public void send(BroadcastBulkEditNotificationEvent event) {
        List<Broadcast> broadcasts = broadcastDao.getAllByVideoKeys(event.ids());
        broadcasts.forEach(statusChangeNotificationService::send);
    }

    private void sendScheduleChangedNotification(BroadcastEditResult result) {
        if (result.isScheduleChanged()) {
            scheduleChangeNotificationService.send(result.broadcast());
        }
    }

    private void sendStatusChangedNotification(BroadcastEditResult result) {
        if (result.isStatusChanged()) {
            statusChangeNotificationService.send(result.broadcast());
        }
    }
}
