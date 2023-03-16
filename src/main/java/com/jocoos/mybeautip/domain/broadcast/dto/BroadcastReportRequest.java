package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import lombok.Builder;

@Builder
public record BroadcastReportRequest(BroadcastReportType type,
                                     Long reportedId,
                                     String reason,
                                     String description) {
}
