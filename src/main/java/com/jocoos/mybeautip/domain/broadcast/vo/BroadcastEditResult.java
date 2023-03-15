package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;

public record BroadcastEditResult(Broadcast broadcast, String originalThumbnailUrl) {
    public boolean isThumbnailChanged() {
        return !broadcast.isThumbnailUrlEq(originalThumbnailUrl);
    }
}
