package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.querydsl.core.annotations.QueryProjection;

public record ViewerCountResult(BroadcastViewerType type, BroadcastViewerStatus status, Integer count) {
    @QueryProjection
    public ViewerCountResult {
    }
}
