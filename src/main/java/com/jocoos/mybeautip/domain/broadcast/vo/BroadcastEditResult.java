package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;

public record BroadcastEditResult(Broadcast broadcast, OriginalInfo originalInfo) {

    public record OriginalInfo(BroadcastStatus status, String thumbnailUrl) {
        public OriginalInfo(Broadcast broadcast) {
            this(broadcast.getStatus(), broadcast.getThumbnailUrl());
        }
    }
    public boolean isThumbnailChanged() {
        return !broadcast.isThumbnailUrlEq(originalInfo().thumbnailUrl);
    }

    public boolean isStatusChangedToReady() {
        return !broadcast.isStatusEq(originalInfo.status)
                && broadcast.isStatusEq(READY);
    }
}
