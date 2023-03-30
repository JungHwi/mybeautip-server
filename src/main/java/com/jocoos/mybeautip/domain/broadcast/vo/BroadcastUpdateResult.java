package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;

import java.util.List;

public record BroadcastUpdateResult(BroadcastStatus status,
                                    List<Long> successIds,
                                    List<Long> failIds) {

    public int successCount() {
        return successIds.size();
    }

    public int failCount() {
        return failIds.size();
    }
}
