package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

@Getter
public class AdminEventListResponse {
    private final long id;
    private final EventStatus status;
    private final String title;
    private final String bannerImageUrl;
    private final Long joinCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime endAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT)
    private final ZonedDateTime createdAt;


    private AdminEventListResponse(Event event, Long joinCount) {
        this.id = event.getId();
        this.status = event.getStatus();
        this.bannerImageUrl = event.getBannerImageUrl();
        this.title = event.getTitle();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.createdAt = event.getZonedCreatedAt();
        this.joinCount = joinCount == null ? 0 : joinCount;
    }

    public static List<AdminEventListResponse> from(List<Event> events, Map<Long, Long> countMap) {
        return events.stream()
                .map(event -> new AdminEventListResponse(event, countMap.get(event.getId())))
                .collect(Collectors.toList());
    }
}
