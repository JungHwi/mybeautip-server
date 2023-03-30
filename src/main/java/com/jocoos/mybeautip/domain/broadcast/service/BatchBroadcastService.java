package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastNotificationEvent.BroadcastBulkEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastViewerStatisticsEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.batch.BroadcastBatchUseCaseFactory;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@RequiredArgsConstructor
@Service
public class BatchBroadcastService {

    private final BroadcastBatchUseCaseFactory batchUseCaseFactory;
    private final FlipFlopLiteService flipFlopLiteService;
    private final ApplicationEventPublisher eventPublisher;

    public List<BroadcastBatchUpdateStatusResponse> bulkChangeStatus() {
        List<BroadcastUpdateResult> results = batchUseCaseFactory.doBatches();
        sendStatusChangedNotification(results);
        return results.stream()
                .map(BroadcastBatchUpdateStatusResponse::from)
                .toList();
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

    private void sendStatusChangedNotification(List<BroadcastUpdateResult> results) {
        List<Long> ids = results
                .stream()
                .map(BroadcastUpdateResult::successIds)
                .flatMap(Collection::stream)
                .toList();
        eventPublisher.publishEvent(new BroadcastBulkEditNotificationEvent(ids));
    }
}
