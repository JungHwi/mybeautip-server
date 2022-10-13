package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventStatusResponse {
    @JsonIgnore
    private static final String ALL = "전체";

    private final EventStatus status;
    private final String statusName;
    private final long eventNum;

    @QueryProjection
    public EventStatusResponse(EventStatus status, long eventNum) {
        this.status = status;
        this.statusName = status.getDescription();
        this.eventNum = eventNum;
    }

    public static List<EventStatusResponse> addFirstAllEventNum(List<EventStatusResponse> responses) {
        long allEventNum = responses.stream()
                .mapToLong(EventStatusResponse::getEventNum)
                .sum();
        responses.add(0, new EventStatusResponse(null, ALL, allEventNum));
        return responses;
    }
}
