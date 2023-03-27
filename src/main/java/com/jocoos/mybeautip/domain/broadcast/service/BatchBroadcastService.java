package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastNotificationEvent.BroadcastBulkEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastViewerStatisticsEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastStatusService;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastBulkUpdateStatusCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.global.vo.Between;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@RequiredArgsConstructor
@Service
public class BatchBroadcastService {

    private final BroadcastStatusService statusService;
    private final FlipFlopLiteService flipFlopLiteService;
    private final ApplicationEventPublisher eventPublisher;

    public BroadcastBatchUpdateStatusResponse bulkChangeStatus() {
        BroadcastUpdateResult toReady = statusService.bulkChangeStatus(updateScheduledNearToReady());
        BroadcastUpdateResult toCancel = statusService.bulkChangeStatus(updateNotYetStartToCancel());
        BroadcastUpdateResult toEnd = statusService.bulkChangeStatus(updatePausedLiveToEnd());
        sendStatusChangedNotification(toReady, toCancel);
        return new BroadcastBatchUpdateStatusResponse(toReady.count(), toCancel.count(), toEnd.count());
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void syncViewer(Broadcast broadcast) {
        List<BroadcastViewerVo> newViewers = flipFlopLiteService.getAllChatMembers(broadcast.getVideoKey());
        List<Long> outManagerIds = broadcast.syncViewer(newViewers);

        if (!outManagerIds.isEmpty()) {
            FFLDirectMessageRequest request = FFLDirectMessageRequest.ofManagerOut(broadcast.getMemberId());
            flipFlopLiteService.directMessage(broadcast.getVideoKey(), request);
        }
        eventPublisher.publishEvent(new BroadcastViewerStatisticsEvent(broadcast.getId()));
    }

    private BroadcastBulkUpdateStatusCommand updateScheduledNearToReady() {
        Between between5MinutesFromNow = new Between(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(5));
        return BroadcastBulkUpdateStatusCommand.updateScheduledNearToReady(between5MinutesFromNow);
    }

    private BroadcastBulkUpdateStatusCommand updateNotYetStartToCancel() {
        return BroadcastBulkUpdateStatusCommand.updateNotYetStartToCancel(ZonedDateTime.now().minusMinutes(5));
    }

    private BroadcastBulkUpdateStatusCommand updatePausedLiveToEnd() {
        return BroadcastBulkUpdateStatusCommand.updatePausedLiveToEnd(ZonedDateTime.now().minusMinutes(1));
    }

    private void sendStatusChangedNotification(BroadcastUpdateResult toReady, BroadcastUpdateResult toCancel) {
        List<Long> notificationBroadcastIds = Stream.concat(toReady.videoKeys().stream(), toCancel.videoKeys().stream()).toList();
        eventPublisher.publishEvent(new BroadcastBulkEditNotificationEvent(notificationBroadcastIds));
    }
}
