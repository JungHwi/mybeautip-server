package com.jocoos.mybeautip.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventJoinResponse {

    private long result;

    public EventJoinResponse() {
        this.result = 0L;
    }
}
