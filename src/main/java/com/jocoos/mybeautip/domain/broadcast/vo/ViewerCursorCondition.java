package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;

public record ViewerCursorCondition(BroadcastViewerType type,
                                    String username) {
}
