package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record AdminBroadcastResponse(long id,
                                     BroadcastStatus status,
                                     String title,
                                     String thumbnailUrl,
                                     int viewerCount,
                                     int maxViewerCount,
                                     int heartCount,
                                     @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt,
                                     @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt,
                                     BroadcastCategoryInfo category,
                                     SimpleMemberInfo member) {
}
