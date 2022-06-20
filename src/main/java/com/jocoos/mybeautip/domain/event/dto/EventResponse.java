package com.jocoos.mybeautip.domain.event.dto;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponse {
    private long id;
    private EventStatus status;
    private String title;

}
