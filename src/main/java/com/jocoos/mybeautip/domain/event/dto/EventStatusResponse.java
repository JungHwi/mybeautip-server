package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class EventStatusResponse {
    @JsonIgnore
    private static final String ALL = "전체";
    private final EventStatus status;
    private final String statusName;
    private final long count;

    public EventStatusResponse(long allEventNum) {
        this.status = null;
        this.statusName = ALL;
        this.count = allEventNum;
    }

    public EventStatusResponse(EventStatus status, long count) {
        this.status = status;
        this.statusName = status.getDescription();
        this.count = count;
    }

    public static List<EventStatusResponse> from(Map<EventStatus, Long> joinCountMap) {
        List<EventStatusResponse> response = new ArrayList<>();
        long allEventCount = getAllEventCount(joinCountMap);
        response.add(withAllStatus(allEventCount));
        response.addAll(withIndividualStatus(joinCountMap));
        return response;
    }

    private static long getAllEventCount(Map<EventStatus, Long> joinCountMap) {
        return joinCountMap.values().stream()
                .mapToLong(Long::valueOf)
                .sum();
    }

    private static EventStatusResponse withAllStatus(long allEventNum) {
        return new EventStatusResponse(allEventNum);
    }

    private static List<EventStatusResponse> withIndividualStatus(final Map<EventStatus, Long> joinCountMap) {
        return Arrays.stream(EventStatus.values())
                .map(eventStatus -> toResponse(joinCountMap, eventStatus))
                .toList();
    }

    private static EventStatusResponse toResponse(Map<EventStatus, Long> joinCountMap, EventStatus eventStatus) {
        return new EventStatusResponse(eventStatus, getCount(joinCountMap, eventStatus));
    }

    private static Long getCount(Map<EventStatus, Long> joinCountMap, EventStatus eventStatus) {
        return joinCountMap.getOrDefault(eventStatus, 0L);
    }
}
