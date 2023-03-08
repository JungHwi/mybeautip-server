package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLChatRoomDirectMessageType {
    ADMIN(""),
    DM(""),
    COMMAND("");

    private final String description;
}
