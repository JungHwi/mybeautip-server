package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record ViewerResponse(
        BroadcastViewerType type,
        long memberId,
        String username,
        String avatarUrl,
        @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime joinedAt) {
}
