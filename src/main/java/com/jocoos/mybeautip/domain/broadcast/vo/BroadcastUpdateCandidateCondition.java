package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.vo.Between;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;

@Builder
public record BroadcastUpdateCandidateCondition(List<BroadcastStatus> statuses,
                                                Between startedAtBetween,
                                                ZonedDateTime startedAt,
                                                ZonedDateTime pausedAt) {

    public static BroadcastUpdateCandidateCondition updateScheduledNearToReady(Between between) {
        return BroadcastUpdateCandidateCondition.builder()
                .statuses(List.of(SCHEDULED))
                .startedAtBetween(between)
                .build();
    }

    public static BroadcastUpdateCandidateCondition updateNotYetStartToCancel(ZonedDateTime startedAtLt) {
        return BroadcastUpdateCandidateCondition.builder()
                .statuses(List.of(SCHEDULED, READY))
                .startedAt(startedAtLt)
                .build();
    }

    public static BroadcastUpdateCandidateCondition updatePausedLiveToEnd(ZonedDateTime pausedAtLt) {
        return BroadcastUpdateCandidateCondition.builder()
                .statuses(List.of(LIVE))
                .pausedAt(pausedAtLt)
                .build();
    }

    public ZonedDateTime betweenStart() {
        return startedAtBetween == null ? null : startedAtBetween.start();
    }

    public ZonedDateTime betweenEnd() {
        return startedAtBetween == null ? null : startedAtBetween.end();
    }
}
