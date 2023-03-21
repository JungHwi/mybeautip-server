package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import lombok.Builder;

@Builder
public record BroadcastViewerVo(BroadcastViewerType type,
                                Long memberId,
                                String username) {
}