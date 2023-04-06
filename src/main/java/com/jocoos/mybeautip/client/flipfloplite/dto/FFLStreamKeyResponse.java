package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;

import java.time.ZonedDateTime;

public record FFLStreamKeyResponse(long id,
                                   FFLEntityState state,
                                   FFLStreamKeyState streamKeyState,
                                   FFLAppInfo app,
                                   FFLMemberInfo member,
                                   FFLVideoRoomResponse videoRoom,
                                   String streamKey,
                                   String liveUrl,
                                   FFLError error,
                                   ZonedDateTime createdAt,
                                   ZonedDateTime lastModifiedAt) {
}
