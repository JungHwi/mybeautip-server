package com.jocoos.mybeautip.domain.broadcast.dto;

public record ViewerSuspendRequest(long broadcastId,
                                   long memberId,
                                   boolean isSuspended) {
}
