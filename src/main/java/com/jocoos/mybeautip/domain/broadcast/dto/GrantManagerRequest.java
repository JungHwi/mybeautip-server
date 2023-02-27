package com.jocoos.mybeautip.domain.broadcast.dto;

public record GrantManagerRequest(long broadcastId,
                                  long memberId,
                                  boolean isManager) {
}
