package com.jocoos.mybeautip.domain.broadcast.dto;

import java.time.ZonedDateTime;

public record CreateBroadcastRequest(long memberId,
                                     String title,
                                     ZonedDateTime startedAt) {
}
