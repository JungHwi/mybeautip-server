package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLVideoRoomState {
    SCHEDULED, LIVE, LIVE_INACTIVE, ENDED, ARCHIVED
}
