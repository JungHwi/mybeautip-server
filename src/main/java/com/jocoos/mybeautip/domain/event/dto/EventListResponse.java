package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Builder
public class EventListResponse {
    private long id;
    private EventType type;
    private EventStatus status;
    private String title;
    private String imageUrl;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime startAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime endAt;
}
