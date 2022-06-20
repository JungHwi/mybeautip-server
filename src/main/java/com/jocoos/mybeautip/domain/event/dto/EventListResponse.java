package com.jocoos.mybeautip.domain.event.dto;

import javafx.event.EventType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventListResponse {

    public Long id;
    public EventType type;

}
