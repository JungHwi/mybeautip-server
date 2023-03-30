package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;

public record BroadcastBatchUpdateStatusResponse(BroadcastStatus status, int success, int fail) {

    public static BroadcastBatchUpdateStatusResponse from(BroadcastUpdateResult result) {
        return new BroadcastBatchUpdateStatusResponse(result.status(), result.successCount(), result.failCount());
    }
}
