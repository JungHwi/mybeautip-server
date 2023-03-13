package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateStatusCondition;
import com.jocoos.mybeautip.global.vo.Between;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class BatchBroadcastService {

    private final BroadcastStatusService statusService;

    @Transactional
    public BroadcastBatchUpdateStatusResponse bulkChangeStatus() {
        // TODO 알림 발송
        long toReadyCount = statusService.bulkChangeStatus(updateScheduledNearToReady());
        long toCancelCount = statusService.bulkChangeStatus(updateNotYetStartToCancel());
        long toEndCount = statusService.bulkChangeStatus(updatePausedLiveToEnd());
        return new BroadcastBatchUpdateStatusResponse(toReadyCount, toCancelCount, toEndCount);
    }

    private BroadcastUpdateStatusCondition updateScheduledNearToReady() {
        Between between5MinutesFromNow = new Between(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(5));
        return BroadcastUpdateStatusCondition.updateScheduledNearToReady(between5MinutesFromNow);
    }

    private BroadcastUpdateStatusCondition updateNotYetStartToCancel() {
        return BroadcastUpdateStatusCondition.updateNotYetStartToCancel(ZonedDateTime.now().minusMinutes(5));
    }

    private BroadcastUpdateStatusCondition updatePausedLiveToEnd() {
        return BroadcastUpdateStatusCondition.updatePausedLiveToEnd(ZonedDateTime.now().minusMinutes(1));
    }
}
