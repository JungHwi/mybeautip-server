package com.jocoos.mybeautip.domain.broadcast.vo;

import java.util.List;

public record BroadcastStatusBatchResult(List<Long> successIds,
                                         List<Long> failIds) {
}
