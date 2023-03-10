package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FFLChangeStatusData {
    private final Long videoRoomId;
    private final FFLVideoRoomState videoRoomVideoRoomState;
}
