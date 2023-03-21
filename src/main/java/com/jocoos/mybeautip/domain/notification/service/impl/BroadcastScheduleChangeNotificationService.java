package com.jocoos.mybeautip.domain.notification.service.impl;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastNotificationDao;
import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.BROADCAST_CHANGE_SCHEDULE;
import static com.jocoos.mybeautip.global.util.EntityUtil.extractLongList;

@RequiredArgsConstructor
@Service
public class BroadcastScheduleChangeNotificationService implements NotificationService<Broadcast> {

    private final NotificationSendService notificationSendService;
    private final BroadcastNotificationDao notificationDao;

    @Override
    public void send(Broadcast broadcast) {
        List<Long> ids = getNotifiers(broadcast);
        Map<String, String> additionalArguments = Map.of(NotificationArgument.BROADCAST_ID.name(), String.valueOf(broadcast.getId()));
        notificationSendService.send(BROADCAST_CHANGE_SCHEDULE, ids, broadcast.getThumbnailUrl(), additionalArguments);
    }

    private List<Long> getNotifiers(Broadcast broadcast) {
        List<BroadcastNotification> notifiers = notificationDao.getNotifications(broadcast);
        return extractLongList(notifiers, BroadcastNotification::getMemberId);
    }
}
