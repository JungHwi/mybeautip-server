package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record ChatData(String title,
                       String notice,
                       String thumbnailUrl,
                       @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt,
                       Boolean isSoundOn,
                       Boolean isScreenShow,
                       BroadcastStatus status) {

    public static ChatData editBroadcast(Broadcast broadcast) {
        return ChatData.builder()
                .title(broadcast.getTitle())
                .notice(broadcast.getNotice())
                .startedAt(broadcast.getStartedAt())
                .thumbnailUrl(broadcast.getThumbnailUrl())
                .isSoundOn(broadcast.getIsSoundOn())
                .isScreenShow(broadcast.getIsScreenShow())
                .build();
    }

    public static ChatData changeStatus(BroadcastStatus status) {
        return ChatData.builder()
                .status(status)
                .build();
    }

    public String toJson() {
        return StringConvertUtil.convertToJson(this);
    }
}
