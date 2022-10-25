package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.event.code.EventStatus;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

public record AdminEventResponse(long id,
                                 EventStatus status,
                                 String title,
                                 String bannerImageUrl,
                                 String rollingBannerImageUrl,
                                 String shareWebImageUrl,
                                 String shareSnsImageUrl,
                                 String detailImageUrl,
                                 Long joinCount,
                                 int point,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startAt,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime endAt,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT) ZonedDateTime createdAt) {
}
