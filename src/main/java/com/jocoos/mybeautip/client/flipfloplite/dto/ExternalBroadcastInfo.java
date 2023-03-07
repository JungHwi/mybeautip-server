package com.jocoos.mybeautip.client.flipfloplite.dto;

import java.time.ZonedDateTime;

public record ExternalBroadcastInfo(Long videoKey,
                                    String liveUrl,
                                    ZonedDateTime scheduledAt,
                                    ZonedDateTime lastModifiedAt) {
}
