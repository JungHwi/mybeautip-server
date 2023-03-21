package com.jocoos.mybeautip.domain.notification.service.impl;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastNotificationDao;
import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.*;
import static com.jocoos.mybeautip.global.util.EntityUtil.extractLongList;

@RequiredArgsConstructor
@Service
public class BroadcastStatusChangeNotificationService implements NotificationService<Broadcast> {

    private final NotificationSendService notificationSendService;
    private final BroadcastNotificationDao notificationDao;

    @Override
    public void send(Broadcast broadcast) {
        switch (broadcast.getStatus()) {
            case READY -> sendReady(broadcast);
            case LIVE -> sendLive(broadcast);
            case CANCEL -> sendCancel(broadcast);
            default -> {}
        }
    }

    private void sendReady(Broadcast broadcast) {
        Map<String, String> additionalArguments = Map.of(NotificationArgument.BROADCAST_ID.name(), String.valueOf(broadcast.getId()));
        sendToFollower(broadcast, BROADCAST_READY_TO_FOLLOWER, additionalArguments);
        notificationSendService.forceSend(BROADCAST_READY_TO_OWNER, broadcast.getMemberId(), null, additionalArguments);
    }

    private void sendLive(Broadcast broadcast) {
        Map<String, String> additionalArguments = Map.of(NotificationArgument.BROADCAST_ID.name(), String.valueOf(broadcast.getId()));
        sendToFollower(broadcast, BROADCAST_LIVE_TO_FOLLOWER, additionalArguments);
    }

    private void sendCancel(Broadcast broadcast) {
        sendToFollower(broadcast, BROADCAST_CANCEL_TO_FOLLOWER, Map.of());
        notificationSendService.forceSend(BROADCAST_CANCEL_TO_OWNER, broadcast.getMemberId(), null, Map.of());
    }

    private void sendToFollower(Broadcast broadcast, TemplateType templateType, Map<String, String> additionalArguments) {
        List<Long> ids = getNotifiers(broadcast);
        notificationSendService.send(templateType, ids, broadcast.getThumbnailUrl(), additionalArguments);
    }

    private List<Long> getNotifiers(Broadcast broadcast) {
        List<BroadcastNotification> notifiers = notificationDao.getNotifications(broadcast);
        return extractLongList(notifiers, BroadcastNotification::getMemberId);
    }
}
