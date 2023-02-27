package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLStreamKeyState {
    INACTIVE, ACTIVE_PREP, ACTIVE, ACTIVE_LIVE_PREP, ACTIVE_LIVE, INACTIVE_LIVE
}
