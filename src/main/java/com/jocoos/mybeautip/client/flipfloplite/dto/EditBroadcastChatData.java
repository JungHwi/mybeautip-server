package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record EditBroadcastChatData(String title,
                                    String notice,
                                    String thumbnailUrl,
                                    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt,
                                    Boolean isSoundOn,
                                    Boolean isScreenShow) {

    public static EditBroadcastChatData editBroadcast(Broadcast broadcast) {
        return EditBroadcastChatData.builder()
                .title(broadcast.getTitle())
                .notice(broadcast.getNotice())
                .startedAt(broadcast.getStartedAt())
                .thumbnailUrl(broadcast.getThumbnailUrl())
                .isSoundOn(broadcast.getIsSoundOn())
                .isScreenShow(broadcast.getIsScreenShow())
                .build();
    }
}
