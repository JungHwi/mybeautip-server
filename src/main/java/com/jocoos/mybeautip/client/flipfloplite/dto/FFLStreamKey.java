package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;

public record FFLStreamKey(long id,
                           FFLEntityState state,
                           FFLStreamKeyState streamKeyState) {
}
