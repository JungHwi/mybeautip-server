package com.jocoos.mybeautip.client.flipfloplite.dto;

import java.time.ZonedDateTime;

public record FFLChatRoomResponse(Long totalChatMessageCount,
                                  Long totalChatMemberCount,
                                  boolean closed,
                                  ZonedDateTime createdAt) {
}
