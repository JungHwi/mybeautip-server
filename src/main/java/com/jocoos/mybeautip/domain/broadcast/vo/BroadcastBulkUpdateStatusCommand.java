package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.vo.Between;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;

@Builder
public record BroadcastBulkUpdateStatusCommand(BroadcastStatus currentStatus,
                                               BroadcastStatus updateStatus,
                                               Between startedAtBetween,
                                               ZonedDateTime updateEndedAt,
                                               ZonedDateTime startedAtLt,
                                               ZonedDateTime pausedAtLt) {

    public static BroadcastBulkUpdateStatusCommand updateScheduledNearToReady(Between between) {
        return BroadcastBulkUpdateStatusCommand.builder()
                .currentStatus(SCHEDULED)
                .updateStatus(READY)
                .startedAtBetween(between)
                .build();
    }

    public static BroadcastBulkUpdateStatusCommand updateNotYetStartToCancel(ZonedDateTime startedAtLt) {
        return BroadcastBulkUpdateStatusCommand.builder()
                .currentStatus(READY)
                .updateStatus(CANCEL)
                .startedAtLt(startedAtLt)
                .build();
    }

    public static BroadcastBulkUpdateStatusCommand updatePausedLiveToEnd(ZonedDateTime pausedAtLt) {
        return BroadcastBulkUpdateStatusCommand.builder()
                .currentStatus(LIVE)
                .updateStatus(END)
                .pausedAtLt(pausedAtLt)
                .build();
    }

    public ZonedDateTime betweenStart() {
        return startedAtBetween == null ?  null : startedAtBetween.start();
    }

    public ZonedDateTime betweenEnd() {
        return startedAtBetween == null ?  null : startedAtBetween.end();
    }
}
