package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;

import java.time.ZonedDateTime;

public record ExternalBroadcastInfo(Long videoKey,
                                    String channelKey,
                                    FFLVideoRoomState videoRoomState,
                                    String liveUrl,
                                    ZonedDateTime scheduledAt,
                                    ZonedDateTime lastModifiedAt) {
}
