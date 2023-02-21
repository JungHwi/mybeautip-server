package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record ViewerResponse(
        BroadcastViewerType type,
        Long memberId,
        BroadcastViewerStatus status,
        String username,
        String avatarUrl,
        boolean isSuspended,
        @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime joinedAt) {
}
