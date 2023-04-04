package com.jocoos.mybeautip.domain.broadcast.event;

import com.jocoos.mybeautip.domain.broadcast.event.BroadcastEvent.BroadcastBulkEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastEvent.BroadcastEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastEvent.BroadcastForceFinishEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastStatusService;
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

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.ACTIVE_STATUSES;
import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Component
public class BroadcastEventHandler {

    private final BroadcastDao broadcastDao;

    private final BroadcastStatusService statusService;
    private final BroadcastStatusChangeNotificationService statusChangeNotificationService;
    private final BroadcastScheduleChangeNotificationService scheduleChangeNotificationService;

    @Async
    @Transactional
    @TransactionalEventListener
    public void sendNotification(BroadcastEditNotificationEvent event) {
        BroadcastEditResult result = event.result();
        sendStatusChangedNotification(result);
        sendScheduleChangedNotification(result);
    }

    @Async
    @Transactional
    @TransactionalEventListener(fallbackExecution = true)
    public void sendNotification(BroadcastBulkEditNotificationEvent event) {
        List<Long> ids = event.ids();
        if (!isEmpty(ids)) {
            List<Broadcast> broadcasts = broadcastDao.getAllByIdIn(ids);
            broadcasts.forEach(statusChangeNotificationService::send);
        }
    }

    @Async
    @Transactional
    @TransactionalEventListener(fallbackExecution = true)
    public void forceFinish(BroadcastForceFinishEvent event) {
        List<Long> memberIds = event.inactiveInfluencerMemberIds();
        if (!isEmpty(memberIds)) {
            List<Broadcast> broadcasts = broadcastDao.getAllByCreatorIdIn(memberIds, ACTIVE_STATUSES);
            statusService.forceFinishAll(broadcasts);
        }
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
