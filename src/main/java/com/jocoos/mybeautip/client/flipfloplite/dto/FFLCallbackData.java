package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record FFLCallbackData(Long videoRoomId,
                              FFLVideoRoomState videoRoomVideoRoomState,
                              Long streamKeyId,
                              FFLStreamKeyState streamKeyStreamKeyState) { }
