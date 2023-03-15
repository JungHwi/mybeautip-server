package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record BroadcastViewerVo(BroadcastViewerType type,
                                Long memberId,
                                String username,
                                ZonedDateTime joinedAt) {
}