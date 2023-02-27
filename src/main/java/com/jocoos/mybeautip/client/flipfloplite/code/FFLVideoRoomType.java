package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLVideoRoomType {
    BROADCAST_RTMP, BROADCAST_WEBRTC, WEBINAR, VIDEO_CONFERENCE, SURVEILLANCE;
}
