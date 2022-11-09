package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

public record AdminEventResponse(long id,
                                 EventType type,
                                 EventStatus status,
                                 Boolean isVisible,
                                 String title,
                                 String description,
                                 String thumbnailImageUrl,
                                 String bannerImageUrl,
                                 String shareRectangleImageUrl,
                                 String shareSquareImageUrl,
                                 String detailImageUrl,
                                 Long joinCount,
                                 int needPoint,
                                 AdminEventProductResponse product,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startAt,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime endAt,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT) ZonedDateTime createdAt) {
}
