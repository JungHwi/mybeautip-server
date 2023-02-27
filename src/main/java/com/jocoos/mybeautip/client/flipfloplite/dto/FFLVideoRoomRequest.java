package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLAccessLevel;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;


public record FFLVideoRoomRequest(String appUserId,
                                  FFLVideoRoomType type,
                                  String title,
                                  String description,
                                  FFLAccessLevel accessLevel,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") ZonedDateTime scheduledAt) {

    public FFLVideoRoomRequest(String appUserId, String title, ZonedDateTime scheduledAt) {
        this(appUserId, FFLVideoRoomType.BROADCAST_RTMP, title, null, FFLAccessLevel.APP, scheduledAt);
    }
}
