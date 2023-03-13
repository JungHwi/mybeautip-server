package com.jocoos.mybeautip.domain.broadcast.dto;

public record VisibleMessageRequest(long broadcastId,
                                    long messageId,
                                    boolean isVisible) {
}
