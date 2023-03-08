package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLChatRoomBroadcastMessageType {
    ADMIN(""),
    MESSAGE(""),
    COMMAND("");

    private final String description;
}
