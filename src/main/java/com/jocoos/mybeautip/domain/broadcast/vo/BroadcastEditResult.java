package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.SCHEDULED;

public record BroadcastEditResult(Broadcast broadcast, OriginalInfo originalInfo) {

    public record OriginalInfo(BroadcastStatus status, String thumbnailUrl, ZonedDateTime startedAt) {
        public OriginalInfo(Broadcast broadcast) {
            this(broadcast.getStatus(), broadcast.getThumbnailUrl(), broadcast.getStartedAt());
        }
    }
    public boolean isThumbnailChanged() {
        return !broadcast.isThumbnailUrlEq(originalInfo().thumbnailUrl);
    }

    public boolean isStatusChanged() {
        return !broadcast.isStatusEq(originalInfo.status);
    }

    public boolean isStatusChangedToReady() {
        return isStatusChanged() && broadcast.isStatusEq(READY);
    }

    public boolean isScheduleChanged() {
        return broadcast.isStatusEq(SCHEDULED) && broadcast.isStartedAtEq(originalInfo.startedAt);
    }
}
